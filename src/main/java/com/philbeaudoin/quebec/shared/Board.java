/**
 * Copyright 2011 Philippe Beaudoin
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

package com.philbeaudoin.quebec.shared;

import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Encodes the state of the game board.
 *
 * @author Philippe Beaudoin
 */
public class Board {

  // Keeps the location of the action associated to each tile location.
  private static final BoardActionInfo[] boardActions = new BoardActionInfo[16];
  private static final BoardActionInfo[] locToAction = new BoardActionInfo[18 * 8];

  /**
   * Checks if the given location is valid for holding a tile.
   * @param column The location column.
   * @param line The location line.
   * @return {@code true} if the location is valid, {@code false} otherwise.
   */
  public static boolean isLocationValid(int column, int line) {
    initIfNeeded();
    return locToAction[locToIndex(column, line)] != null;
  }

  /**
   * Returns information on the action associated with the tile at a given location.
   * @param column The tile location column.
   * @param line The tile location line.
   * @return Information on the action, or {@code null} if the passed location is not valid for a
   *     tile.
   */
  public static BoardActionInfo actionInfoForTileLocation(int column, int line) {
    initIfNeeded();
    return locToAction[locToIndex(column, line)];
  }

  private static final double PI_OVER_3 = 1.04719755;
  private static final double PI_OVER_2 = 1.57079633;
  private static final double PI_3_OVER_2 = 4.71238898;

  /**
   * Returns the rotation angle of the tile placed at a given location.
   * @param column The tile location column.
   * @param line The tile location line.
   * @return The rotation angle at that location in radians, 0 if it's an invalid location.
   */
  public static double rotationAngleForLocation(int column, int line) {
    BoardActionInfo actionInfo = actionInfoForTileLocation(column, line);
    if (actionInfo == null) {
      return 0;
    }
    double actionColumn = actionInfo.getLocation().getX();
    double actionLine = actionInfo.getLocation().getY();

    if (actionColumn < column) {
      return PI_3_OVER_2 + (line - actionLine) * PI_OVER_3;
    } else {
      return PI_OVER_2 + (actionLine - line) * PI_OVER_3;
    }
  }

  /**
   * Returns the tile location for a normalized position.
   * @param x The X normalized position.
   * @param y The Y normalized position.
   * @return The nearest tile location (column, line) for the given position.
   */
  public static Vector2d locationForPosition(double x, double y) {
    double locXDouble = (x - 0.125) / 0.0342;
    long locX = Math.round(locXDouble);
    long locY = Math.round((y - 0.18) / 0.0592);
    if ((locX + locY) % 2 == 0) {
      if (locXDouble < locX) {
        locX--;
      } else {
        locX++;
      }
    }
    return new Vector2d(locX, locY);
  }

  /**
   * Returns the central normalized position for a given tile location.
   * @param column The tile column.
   * @param line The tile line.
   * @return The central normalized position.
   */
  public static Vector2d positionForLocation(int column, int line) {
    return new Vector2d(0.125 + column * 0.0342, 0.18 + line * 0.0592);
  }

  private static int locToIndex(int column, int line) {
    return line * 18 + column;
  }

  private static void initIfNeeded() {
    if (boardActions[0] == null) {
      boardActions[0] = new BoardActionInfo(3, 0, InfluenceType.CULTURAL, 1);
      boardActions[1] = new BoardActionInfo(7, 0, InfluenceType.ECONOMIC, 3);
      boardActions[2] = new BoardActionInfo(1, 2, InfluenceType.POLITIC, 3);
      boardActions[3] = new BoardActionInfo(6, 3, InfluenceType.RELIGIOUS, 2);
      boardActions[4] = new BoardActionInfo(9, 2, InfluenceType.POLITIC, 2);
      boardActions[5] = new BoardActionInfo(3, 4, InfluenceType.ECONOMIC, 2);
      boardActions[6] = new BoardActionInfo(5, 6, InfluenceType.POLITIC, 2);
      boardActions[7] = new BoardActionInfo(1, 6, InfluenceType.RELIGIOUS, 1);
      boardActions[8] = new BoardActionInfo(16, 1, InfluenceType.ECONOMIC, 1);
      boardActions[9] = new BoardActionInfo(12, 1, InfluenceType.CULTURAL, 2);
      boardActions[10] = new BoardActionInfo(14, 3, InfluenceType.RELIGIOUS, 2);
      boardActions[11] = new BoardActionInfo(8, 5, InfluenceType.CULTURAL, 2);
      boardActions[12] = new BoardActionInfo(11, 4, InfluenceType.ECONOMIC, 2);
      boardActions[13] = new BoardActionInfo(16, 5, InfluenceType.CULTURAL, 3);
      boardActions[14] = new BoardActionInfo(10, 7, InfluenceType.RELIGIOUS, 3);
      boardActions[15] = new BoardActionInfo(14, 7, InfluenceType.POLITIC, 1);

      addSymmetricalActions(2, 1, 0);
      addSymmetricalActions(4, 1, 0);
      addSymmetricalActions(6, 1, 1);
      addSymmetricalActions(8, 1, 1);
      addSymmetricalActions(3, 2, 2);
      addSymmetricalActions(5, 2, 3);
      addSymmetricalActions(7, 2, 4);
      addSymmetricalActions(0, 3, 2);
      addSymmetricalActions(2, 3, 2);
      addSymmetricalActions(4, 3, 5);
      addSymmetricalActions(8, 3, 3);
      addSymmetricalActions(1, 4, 5);
      addSymmetricalActions(5, 4, 3);
      addSymmetricalActions(7, 4, 11);
      addSymmetricalActions(0, 5, 7);
      addSymmetricalActions(2, 5, 7);
      addSymmetricalActions(4, 5, 5);
      addSymmetricalActions(6, 5, 6);
      addSymmetricalActions(3, 6, 6);
      addSymmetricalActions(7, 6, 11);
      addSymmetricalActions(6, 7, 6);
      addSymmetricalActions(8, 7, 14);
    }
  }

  private static void addSymmetricalActions(int column, int line, int actionIndex) {
    locToAction[locToIndex(column, line)] = boardActions[actionIndex];
    locToAction[locToIndex(17 - column, 7 - line)] = boardActions[15 - actionIndex];
  }
}
