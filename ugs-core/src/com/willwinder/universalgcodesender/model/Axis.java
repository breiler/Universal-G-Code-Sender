/*
    Copyright 2018 Will Winder

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
package com.willwinder.universalgcodesender.model;

/*
 * This class defines all axis.
 * There are cartesian axis (default X, Y, Z) which are
 * displayed in the controller state, and rotation axis.
 */
public enum Axis {
  // Cartesian
  X, Y, Z,
  // Rotation
  A, // X
  B, // Y
  C  // Z
  ;

  public boolean isRotation() {
      return ! isLinear();
  }

  public boolean isLinear() {
    return this == X || this == Y || this == Z;
  }
}

