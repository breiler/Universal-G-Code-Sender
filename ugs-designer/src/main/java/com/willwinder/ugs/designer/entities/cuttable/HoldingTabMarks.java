/*
    Copyright 2026 Joacim Breiler

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
package com.willwinder.ugs.designer.entities.cuttable;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Marks the parts of a shape where holding tabs will be left behind, so that they can be drawn on
 * top of the shape being cut.
 * <p>
 * The tabs are placed the same way as when the tool path is generated, using the outline of the
 * design instead of the path the tool takes around it. The marks can therefore be off by up to the
 * radius of the tool from where the tool ends up climbing onto a tab.
 *
 * @author Joacim Breiler
 */
final class HoldingTabMarks {
    private static final double FLATNESS = 0.1;

    private HoldingTabMarks() {
    }

    static Shape create(Shape shape, HoldingTabs tabs) {
        Path2D marks = new Path2D.Double();
        if (!tabs.isEnabled()) {
            return marks;
        }

        closedSubPaths(shape).forEach(subPath -> addMarks(marks, subPath, tabs));
        return marks;
    }

    private static void addMarks(Path2D marks, List<Point2D> subPath, HoldingTabs tabs) {
        List<Point2D> points = startAtLowestPoint(subPath);
        double[] distances = distancesAlong(points);
        double length = distances[distances.length - 1];

        tabs.centersAlong(length).forEach(center ->
                addMark(marks, points, distances, center - (tabs.width() / 2d), center + (tabs.width() / 2d)));
    }

    private static void addMark(Path2D marks, List<Point2D> points, double[] distances, double from, double to) {
        Point2D start = pointAt(points, distances, from);
        marks.moveTo(start.getX(), start.getY());
        for (int index = 0; index < points.size(); index++) {
            if (distances[index] > from && distances[index] < to) {
                marks.lineTo(points.get(index).getX(), points.get(index).getY());
            }
        }
        Point2D end = pointAt(points, distances, to);
        marks.lineTo(end.getX(), end.getY());
    }

    private static Point2D pointAt(List<Point2D> points, double[] distances, double distance) {
        for (int index = 1; index < distances.length; index++) {
            if (distances[index] >= distance) {
                double segmentLength = distances[index] - distances[index - 1];
                double partOfSegment = segmentLength > 0 ? (distance - distances[index - 1]) / segmentLength : 0;
                Point2D from = points.get(index - 1);
                Point2D to = points.get(index);
                return new Point2D.Double(
                        from.getX() + ((to.getX() - from.getX()) * partOfSegment),
                        from.getY() + ((to.getY() - from.getY()) * partOfSegment));
            }
        }

        return points.get(points.size() - 1);
    }

    /**
     * Moves the start of the outline to the point closest to the lower left of its bounds, matching
     * how the tool path spreads the tabs out from a start that does not depend on where the shape
     * happens to begin.
     */
    private static List<Point2D> startAtLowestPoint(List<Point2D> points) {
        double minimumX = points.stream().mapToDouble(Point2D::getX).min().orElse(0);
        double minimumY = points.stream().mapToDouble(Point2D::getY).min().orElse(0);

        int startIndex = 0;
        double shortestDistance = Double.MAX_VALUE;
        for (int index = 0; index < points.size() - 1; index++) {
            double distance = points.get(index).distance(minimumX, minimumY);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                startIndex = index;
            }
        }

        List<Point2D> rotated = new ArrayList<>(points.size());
        for (int index = 0; index < points.size() - 1; index++) {
            rotated.add(points.get((startIndex + index) % (points.size() - 1)));
        }
        rotated.add(rotated.get(0));
        return rotated;
    }

    private static double[] distancesAlong(List<Point2D> points) {
        double[] distances = new double[points.size()];
        for (int index = 1; index < points.size(); index++) {
            distances[index] = distances[index - 1] + points.get(index).distance(points.get(index - 1));
        }
        return distances;
    }

    /**
     * Flattens the shape into the closed outlines it is made up of, leaving out any open ones since
     * they do not cut anything loose that needs holding.
     */
    private static List<List<Point2D>> closedSubPaths(Shape shape) {
        List<List<Point2D>> subPaths = new ArrayList<>();
        List<Point2D> current = new ArrayList<>();
        double[] coordinates = new double[6];

        for (PathIterator iterator = shape.getPathIterator(null, FLATNESS); !iterator.isDone(); iterator.next()) {
            switch (iterator.currentSegment(coordinates)) {
                case PathIterator.SEG_MOVETO -> {
                    current = new ArrayList<>();
                    current.add(new Point2D.Double(coordinates[0], coordinates[1]));
                }
                case PathIterator.SEG_LINETO -> current.add(new Point2D.Double(coordinates[0], coordinates[1]));
                case PathIterator.SEG_CLOSE -> {
                    if (current.size() > 2) {
                        current.add(current.get(0));
                        subPaths.add(current);
                    }
                    current = new ArrayList<>();
                }
                default -> {
                    // The path is flattened, so there are no curves left to handle
                }
            }
        }

        return subPaths;
    }
}
