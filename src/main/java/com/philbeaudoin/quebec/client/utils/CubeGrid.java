/**
 * Copyright 2012 Philippe Beaudoin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.quebec.client.utils;

import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * This class makes it possible to determine the position of cubes within a cube grid.
 * The cubes on each line are staggered. The grid is centered given the specified number of columns
 * and lines.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class CubeGrid {
  private static final double CUBE_SIZE_X = 0.01;
  private static final double CUBE_SIZE_Y = 0.01;
  private static final double DEFAULT_DISTANCE_BETWEEN_LINES = 0.031;

  private static final double[] COLUMN_FOR_INDEX_IN_GROUP = {0, 1, 0.5, 2, 1.5};

  private final int columns;
  private final int lines;
  private final double distanceBetweenLines;
  private final double halfSizeX;

  /**
   * Creates a cube grid with a specified number of columns and lines.
   * @param columns The number of columns in the grid.
   * @param lines The number of lines in the grid.
   */
  public CubeGrid(int columns, int lines) {
    this(columns, lines, DEFAULT_DISTANCE_BETWEEN_LINES);
  }

  /**
   * Creates a cube grid with a specified number of columns and lines and a specific distance
   * between each lines.
   * @param columns The number of columns in the grid.
   * @param lines The number of lines in the grid.
   */
  public CubeGrid(int columns, int lines, double distanceBetweenLines) {
    this.columns = columns;
    this.lines = lines;
    this.distanceBetweenLines = distanceBetweenLines;
    int group = (columns - 1) / 5;
    int indexInGroup = (columns - 1) % 5;
    int maxIndexInGroup = indexInGroup >= 3 ? 3 : (indexInGroup >= 1 ? 1 : 0);
    this.halfSizeX = calculateX(group, maxIndexInGroup) / 2.0;
  }

  /**
   * Get the position of the cube at a given column and line. Columns are not ordered strictly from
   * left to right, this is to allow for a nicer overlapping of the cubes. The order is the
   * following:
   * <pre>
   *  2 4    7 9
   * 0 1 3  5 6 8
   * </pre>
   * @param column Index of the column, zero-based.
   * @param line Index of the line, zero-based.
   * @return The position at that line and column
   */
  public Vector2d getPosition(int column, int line) {
    assert column >= 0 && column < columns;
    assert line >= 0 && line < lines;
    int group = column / 5;
    int indexInGroup = column % 5;
    double y = (line - (lines - 1) / 2.0) * distanceBetweenLines;
    double x = calculateX(group, indexInGroup) - halfSizeX;
    if (columns > 2) {
      if (indexInGroup == 0 || indexInGroup % 2 == 1) {
        // Bottom line
        y += CUBE_SIZE_Y / 2.0;
      } else {
        // Top line
        y -= CUBE_SIZE_Y / 2.0;
      }
    }
    return new Vector2d(x, y);
  }

  private double calculateX(int group, int indexInGroup) {
    return (COLUMN_FOR_INDEX_IN_GROUP[indexInGroup] + 3.7 * group) * CUBE_SIZE_X;
  }
}
