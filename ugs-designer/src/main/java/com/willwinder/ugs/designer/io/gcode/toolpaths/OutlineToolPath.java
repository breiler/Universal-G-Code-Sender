/*
    Copyright 2021-2026 Will Winder

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
package com.willwinder.ugs.designer.io.gcode.toolpaths;

import com.willwinder.ugs.designer.entities.cuttable.Cuttable;
import com.willwinder.ugs.designer.entities.cuttable.HoldingTabs;
import com.willwinder.ugs.designer.io.gcode.path.GcodePath;
import com.willwinder.ugs.designer.io.gcode.path.SegmentType;
import com.willwinder.ugs.designer.model.Settings;
import com.willwinder.universalgcodesender.model.Axis;
import com.willwinder.universalgcodesender.model.PartialPosition;
import com.willwinder.universalgcodesender.model.UnitUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.linearref.LengthIndexedLine;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Joacim Breiler
 */
public class OutlineToolPath extends AbstractToolPath {
    /**
     * Distances along an outline closer together than this are treated as the same point, so that a
     * tab landing on a corner does not add a move going nowhere.
     */
    private static final double SAME_DISTANCE_TOLERANCE = 0.0001;

    private final Cuttable source;

    private double offset;

    public OutlineToolPath(Settings settings, Cuttable source) {
        super(settings);
        this.source = source;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    @Override
    protected void addSafeHeightSegment(GcodePath gcodePath, PartialPosition coordinate, boolean isFirst) {
        if (isFirst) {
            super.addSafeHeightSegment(gcodePath, coordinate, true);
        } else {
            // Outline Paths always Start and end in the same spot so its worthwhile to only climb a smaller amount
            double safeHeightToUse = settings.getSafeHeight() + (coordinate != null && coordinate.hasZ() ? coordinate.getZ() : -getStartDepth());
            PartialPosition safeHeightCoordinate = PartialPosition.from(Axis.Z, safeHeightToUse, UnitUtils.Units.MM);
            gcodePath.addSegment(SegmentType.MOVE, safeHeightCoordinate);
        }
    }

    @Override
    public void appendGcodePath(GcodePath gcodePath, Settings settings) {
        List<Geometry> geometries = toGeometries(settings);
        HoldingTabs tabs = new HoldingTabs(source.getTabCount(), source.getTabWidth(), source.getTabHeight());

        List<List<PartialPosition>> coordinateList = new ArrayList<>();
        geometries.forEach(geometry -> {
            Outline outline = toOutline(geometry, tabs);

            coordinateList.add(outline.coordinatesAt(getStartDepth()));

            double currentDepth = getStartDepth();
            while (currentDepth < getTargetDepth()) {
                currentDepth = Math.min(currentDepth + settings.getDepthPerPass(), getTargetDepth());
                coordinateList.add(outline.coordinatesAt(currentDepth));
            }
        });

        addToGcodePath(gcodePath, coordinateList, source);
    }

    private List<Geometry> toGeometries(Settings settings) {
        if (ToolPathUtils.isClosedGeometry(source.getShape())) {
            Geometry geometry = ToolPathUtils.convertAreaToGeometry(new Area(source.getShape()), getGeometryFactory(), settings.getFlatnessPrecision());
            Geometry bufferedGeometry = geometry.buffer(offset);
            return ToolPathUtils.toGeometryList(bufferedGeometry);
        }

        return ToolPathUtils.convertShapeToGeometry(source.getShape(), getGeometryFactory(), settings.getFlatnessPrecision());
    }

    /**
     * Prepares a single outline for being cut, placing the holding tabs along it when it is a closed
     * ring that can hold a part in place.
     */
    private Outline toOutline(Geometry geometry, HoldingTabs tabs) {
        List<Double> distances = distancesAlong(geometry.getCoordinates());
        double length = distances.get(distances.size() - 1);

        TabProfile tabProfile = geometry instanceof LinearRing
                ? TabProfile.along(tabs, length, getTargetDepth())
                : TabProfile.none();

        if (tabProfile.isEmpty()) {
            return new Outline(Arrays.asList(geometry.getCoordinates()), distances, tabProfile);
        }

        // The tabs are spread out from the start of the ring, which is put in the same place regardless
        // of where the geometry happens to begin, so that they end up where the design shows them
        LinearRing ring = ToolPathUtils.rotateCoordinates((LinearRing) geometry, indexOfLowestCoordinate(geometry));
        return addTabBoundaries(ring, tabProfile);
    }

    /**
     * Adds the coordinates needed to shape the tabs to the ring, since the ramps of a tab can
     * otherwise fall between two coordinates far apart.
     */
    private Outline addTabBoundaries(LinearRing ring, TabProfile tabProfile) {
        List<Double> distances = merge(distancesAlong(ring.getCoordinates()), tabProfile.breakPoints());
        LengthIndexedLine indexedRing = new LengthIndexedLine(ring);
        List<Coordinate> coordinates = distances.stream()
                .map(indexedRing::extractPoint)
                .toList();
        return new Outline(coordinates, distances, tabProfile);
    }

    private static int indexOfLowestCoordinate(Geometry geometry) {
        Envelope envelope = geometry.getEnvelopeInternal();
        return ToolPathUtils.findNearestCoordinateIndex(geometry.getCoordinates(), new Coordinate(envelope.getMinX(), envelope.getMinY()));
    }

    private static List<Double> distancesAlong(Coordinate[] coordinates) {
        List<Double> distances = new ArrayList<>(coordinates.length);
        double distance = 0;
        distances.add(distance);
        for (int index = 1; index < coordinates.length; index++) {
            distance += coordinates[index].distance(coordinates[index - 1]);
            distances.add(distance);
        }
        return distances;
    }

    private static List<Double> merge(List<Double> distances, List<Double> distancesToAdd) {
        List<Double> sorted = new ArrayList<>(distances.size() + distancesToAdd.size());
        sorted.addAll(distances);
        sorted.addAll(distancesToAdd);
        Collections.sort(sorted);

        List<Double> merged = new ArrayList<>(sorted.size());
        for (double distance : sorted) {
            if (merged.isEmpty() || distance - merged.get(merged.size() - 1) >= SAME_DISTANCE_TOLERANCE) {
                merged.add(distance);
            }
        }

        // The outline has to end where it started, even if a tab boundary lands just before the end
        merged.set(merged.size() - 1, sorted.get(sorted.size() - 1));
        return merged;
    }

    /**
     * The coordinates of an outline to cut, together with the tabs that the tool needs to be lifted
     * over on the way around it.
     */
    private record Outline(List<Coordinate> coordinates, List<Double> distances, TabProfile tabProfile) {
        List<PartialPosition> coordinatesAt(double depth) {
            List<PartialPosition> positions = new ArrayList<>(coordinates.size());
            for (int index = 0; index < coordinates.size(); index++) {
                Coordinate coordinate = coordinates.get(index);
                double depthToCut = Math.min(depth, tabProfile.depthLimitAt(distances.get(index)));
                positions.add(new PartialPosition(coordinate.getX(), coordinate.getY(), -depthToCut, UnitUtils.Units.MM));
            }
            return positions;
        }
    }
}
