/*
    Copyright 2020-2023 Will Winder

    This file is part of Universal Gcode Sender (UGS).

    UGS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UGS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UGS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.willwinder.ugs.nbp.core.actions;

import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.ugs.nbp.lib.services.LocalizingService;
import com.willwinder.universalgcodesender.gcode.ICommandCreator;
import com.willwinder.universalgcodesender.gcode.util.Code;
import com.willwinder.universalgcodesender.gcode.util.GcodeParserException;
import com.willwinder.universalgcodesender.gcode.util.GcodeUtils;
import com.willwinder.universalgcodesender.listeners.ControllerState;
import com.willwinder.universalgcodesender.listeners.UGSEventListener;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.model.PartialPosition;
import com.willwinder.universalgcodesender.model.UGSEvent;
import com.willwinder.universalgcodesender.model.UnitUtils;
import com.willwinder.universalgcodesender.model.events.ControllerStateEvent;
import com.willwinder.universalgcodesender.model.events.FileStateEvent;
import com.willwinder.universalgcodesender.types.GcodeCommand;
import com.willwinder.universalgcodesender.uielements.helpers.LoaderDialogHelper;
import com.willwinder.universalgcodesender.utils.GUIHelpers;
import com.willwinder.universalgcodesender.utils.GcodeStreamReader;
import com.willwinder.universalgcodesender.utils.IGcodeStreamReader;
import com.willwinder.universalgcodesender.utils.MathUtils;
import com.willwinder.universalgcodesender.utils.SimpleGcodeStreamReader;
import com.willwinder.universalgcodesender.utils.ThreadHelper;
import com.willwinder.universalgcodesender.visualizer.GcodeViewParse;
import com.willwinder.universalgcodesender.visualizer.LineSegment;
import com.willwinder.universalgcodesender.visualizer.LineSegmentToPartialPositionMapper;
import com.willwinder.universalgcodesender.visualizer.VisualizerUtils;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;

import javax.swing.Action;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * An action that will parse the loaded gcode file and generate a movement path for outlining
 * the cutting path using the current Z coordinate. If there is any rapid movements in the file
 * these will be ignored. It will only process the coordinates in the X/Y-plane.
 *
 * @author Joacim Breiler
 */
@ActionID(
        category = LocalizingService.OutlineCategory,
        id = LocalizingService.OutlineActionId)
@ActionRegistration(
        iconBase = OutlineAction.ICON_BASE,
        displayName = "resources/MessagesBundle#" + LocalizingService.OutlineTitleKey,
        lazy = false)
@ActionReferences({
        @ActionReference(
                path = LocalizingService.MENU_PROGRAM,
                position = 1200,
                separatorBefore = 1199)
})
public final class OutlineAction extends ProgramAction implements UGSEventListener {

    public static final String ICON_BASE = "resources/icons/outline.svg";
    public static final double ARC_SEGMENT_LENGTH = 0.5;
    private static final Logger LOGGER = Logger.getLogger(OutlineAction.class.getSimpleName());
    private final transient BackendAPI backend;

    public OutlineAction() {
        this.backend = CentralLookup.getDefault().lookup(BackendAPI.class);
        this.backend.addUGSEventListener(this);

        putValue("iconBase", ICON_BASE);
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon(ICON_BASE, false));
        putValue("menuText", LocalizingService.OutlineTitle);
        putValue(NAME, LocalizingService.OutlineTitle);
        putValue(Action.SHORT_DESCRIPTION, LocalizingService.OutlineToolTip);
        setEnabled(isEnabled());
    }

    @Override
    public void UGSEvent(UGSEvent cse) {
        if (cse instanceof ControllerStateEvent || cse instanceof FileStateEvent) {
            java.awt.EventQueue.invokeLater(() -> setEnabled(isEnabled()));
        }
    }

    @Override
    public boolean isEnabled() {
        return backend != null &&
                backend.getControllerState() == ControllerState.IDLE &&
                backend.getGcodeFile() != null;
    }

    @Override
    public void execute(ActionEvent e) {
        ThreadHelper.invokeLater(() -> {
            try {
                LOGGER.finest("Generating the outline of the gcode model");
                LoaderDialogHelper.showDialog("Generating outline", 1500, (Component) e.getSource());
                File gcodeFile = backend.getProcessedGcodeFile();
                List<GcodeCommand> gcodeCommands = generateOutlineCommands(gcodeFile);

                LOGGER.finest("Sending the outline to the controller");
                backend.getController().queueStream(new SimpleGcodeStreamReader(gcodeCommands));
                backend.getController().beginStreaming();
            } catch (Exception ex) {
                GUIHelpers.displayErrorDialog(ex.getLocalizedMessage());
            } finally {
                LoaderDialogHelper.closeDialog();
            }
        });
    }

    public List<GcodeCommand> generateOutlineCommands(File gcodeFile) throws IOException, GcodeParserException {
        List<LineSegment> gcodeLineList = parseGcodeLinesFromFile(gcodeFile);

        // We only care about carving motion, filter those commands out
        List<PartialPosition> pointList = gcodeLineList.parallelStream()
                .filter(lineSegment -> !lineSegment.isFastTraverse())
                .flatMap(new LineSegmentToPartialPositionMapper())
                .filter(partialPosition -> partialPosition.hasX() && partialPosition.hasY())
                .distinct()
                .toList();

        return generateConvexHullCommands(pointList);
    }

    private List<GcodeCommand> generateConvexHullCommands(List<PartialPosition> pointList) {
        List<PartialPosition> outline = MathUtils.generateConvexHull(pointList);
        ICommandCreator commandCreator = backend.getCommandCreator();
        return outline.stream()
                .map(point -> commandCreator.createCommand(GcodeUtils.generateMoveToCommand(Code.G90.name() + Code.G1.name(), point, getJogFeedRate())))
                .toList();
    }

    private double getJogFeedRate() {
        UnitUtils.Units preferredUnits = backend.getSettings().getPreferredUnits();
        return backend.getSettings().getJogFeedRate() * UnitUtils.scaleUnits(preferredUnits, UnitUtils.Units.MM);
    }

    private List<LineSegment> parseGcodeLinesFromFile(File gcodeFile) throws IOException, GcodeParserException {
        List<LineSegment> result;

        GcodeViewParse gcvp = new GcodeViewParse();
        try (IGcodeStreamReader gsr = new GcodeStreamReader(gcodeFile, backend.getCommandCreator())) {
            result = gcvp.toObjFromReader(gsr, ARC_SEGMENT_LENGTH);
        } catch (GcodeStreamReader.NotGcodeStreamFile e) {
            List<String> linesInFile = VisualizerUtils.readFiletoArrayList(gcodeFile.getAbsolutePath());
            result = gcvp.toObjRedux(linesInFile, ARC_SEGMENT_LENGTH);
        }

        return result;
    }
}
