/*
    Copyright 2025 Joacim Breiler

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
package com.willwinder.universalgcodesender.fx.component;

import javafx.scene.control.Label;

public class ErrorLabel extends Label {
    public ErrorLabel(String text) {
        super(text);
        setMaxWidth(Double.MAX_VALUE);
        setWrapText(true);
        setStyle("""
             -fx-background-radius: 4;
             -fx-background-color: #c00;
             -fx-padding: 10 10 10 10;
             -fx-text-fill: white;
        """);
    }
}
