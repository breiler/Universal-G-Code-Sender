package com.willwinder.ugs.designer;

import com.willwinder.ugs.designer.entities.selection.SelectionManager;
import com.willwinder.ugs.designer.gui.DrawingOverlayContainer;
import com.willwinder.ugs.designer.gui.DrawingScrollContainer;
import com.willwinder.ugs.designer.gui.MainMenu;
import com.willwinder.ugs.designer.gui.PopupMenuFactory;
import com.willwinder.ugs.designer.gui.ToolBox;
import com.willwinder.ugs.designer.gui.selectionsettings.SelectionSettingsPanel;
import com.willwinder.ugs.designer.gui.tree.EntitiesTree;
import com.willwinder.ugs.designer.gui.tree.EntitiesTreeController;
import com.willwinder.ugs.designer.gui.tree.EntityTreeModel;
import com.willwinder.ugs.designer.io.svg.SvgReader;
import com.willwinder.ugs.designer.logic.Controller;
import com.willwinder.ugs.designer.logic.ControllerFactory;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.model.GUIBackend;
import com.willwinder.universalgcodesender.services.LookupService;
import com.willwinder.universalgcodesender.utils.Settings;
import com.willwinder.universalgcodesender.utils.SettingsFactory;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * A test implementation of the gcode designer tool that works in stand alone mode
 *
 * @author Joacim Breiler
 */
public class DesignerMain extends JFrame {

    public static final String PROPERTY_IS_STANDALONE = "ugs.designer.standalone";
    public static final String PROPERTY_USE_SCREEN_MENU = "apple.laf.useScreenMenuBar";

    /**
     * Constructs a new graphical user interface for the program and shows it.
     */
    public DesignerMain() throws Exception {
        setupLookAndFeel();

        setTitle("UGS Designer");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1024, 768));

        Settings settings = SettingsFactory.loadSettings();
        BackendAPI backend = new GUIBackend();
        backend.applySettings(settings);
        LookupService.register(backend);

        LookupService.registerProviders(DesignerMain.class.getPackageName());

        Controller controller = ControllerFactory.getController();
        LookupService.register(controller);
        LookupService.register( ControllerFactory.getUndoManager());

        SelectionManager selectionManager = ControllerFactory.getSelectionManager();
        LookupService.register(selectionManager);

        DrawingScrollContainer drawingContainer = new DrawingScrollContainer(controller);
        selectionManager.addSelectionListener(e -> drawingContainer.repaint());

        DrawingOverlayContainer overlayToolContainer = new DrawingOverlayContainer(controller, drawingContainer);

        JSplitPane toolsSplit = createRightPanel(controller);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                overlayToolContainer, toolsSplit);
        splitPane.setResizeWeight(0.95);

        getContentPane().add(splitPane, BorderLayout.CENTER);

        ToolBox tools = new ToolBox(controller);
        add(tools, BorderLayout.NORTH);

        JMenuBar mainMenu = new MainMenu(controller);
        this.setJMenuBar(mainMenu);

        pack();
        setVisible(true);

        loadExample(controller);
        controller.getDrawing().setComponentPopupMenu(PopupMenuFactory.createPopupMenu());
        controller.getDrawing().repaint();
    }

    private static void setupLookAndFeel() {
        System.setProperty(PROPERTY_USE_SCREEN_MENU, "true");
        System.setProperty(PROPERTY_IS_STANDALONE, "true");

        UIManager.put( "MenuBar.background", "@background");
    }
    
    private JSplitPane createRightPanel(Controller controller) {
        EntityTreeModel entityTreeModel = new EntityTreeModel(controller);
        EntitiesTree entitiesTree = new EntitiesTree(controller, entityTreeModel);
        LookupService.register(new EntitiesTreeController(entitiesTree));

        JSplitPane toolsSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(entitiesTree,
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                new JScrollPane(new SelectionSettingsPanel(controller),
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        toolsSplit.setResizeWeight(0.9);
        return toolsSplit;
    }

    private void loadExample(Controller controller) {
        SvgReader svgReader = new SvgReader();
        svgReader.read(DesignerMain.class.getResourceAsStream("/sample/example.svg"))
                .ifPresent(design -> design.getEntities().forEach(controller.getDrawing()::insertEntity));
    }

    public static void main(String[] args) throws Exception {
        new DesignerMain();
    }
}
