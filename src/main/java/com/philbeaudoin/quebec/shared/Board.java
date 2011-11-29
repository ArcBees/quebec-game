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

import com.philbeaudoin.quebec.shared.utils.MutableVector2d;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Encodes the state of the game board.
 *
 * @author beaudoin
 */
public class Board {

  // Keeps the location of the action for each tile location;
  private static final Vector2d[] indexToActionLoc = new Vector2d[9 * 8];

  private static int locToIndex(int c, int l) {
    return l * 9 + c;
  }

  private static void initIfNeeded() {
    // We know that location 2, 1 should be valid, use it as a sentinel.
    if (indexToActionLoc[11] == null) {
      indexToActionLoc[locToIndex(2,1)] = new Vector2d(3,0);
      indexToActionLoc[locToIndex(4,1)] = new Vector2d(3,0);
      indexToActionLoc[locToIndex(6,1)] = new Vector2d(7,0);
      indexToActionLoc[locToIndex(8,1)] = new Vector2d(7,0);
      indexToActionLoc[locToIndex(3,2)] = new Vector2d(1,2);
      indexToActionLoc[locToIndex(5,2)] = new Vector2d(6,3);
      indexToActionLoc[locToIndex(7,2)] = new Vector2d(9,2);
      indexToActionLoc[locToIndex(0,3)] = new Vector2d(1,2);
      indexToActionLoc[locToIndex(2,3)] = new Vector2d(1,2);
      indexToActionLoc[locToIndex(4,3)] = new Vector2d(3,4);
      indexToActionLoc[locToIndex(8,3)] = new Vector2d(6,3);
      indexToActionLoc[locToIndex(1,4)] = new Vector2d(3,4);
      indexToActionLoc[locToIndex(5,4)] = new Vector2d(6,3);
      indexToActionLoc[locToIndex(7,4)] = new Vector2d(8,5);
      indexToActionLoc[locToIndex(0,5)] = new Vector2d(1,6);
      indexToActionLoc[locToIndex(2,5)] = new Vector2d(1,6);
      indexToActionLoc[locToIndex(4,5)] = new Vector2d(3,4);
      indexToActionLoc[locToIndex(6,5)] = new Vector2d(5,6);
      indexToActionLoc[locToIndex(3,6)] = new Vector2d(5,6);
      indexToActionLoc[locToIndex(7,6)] = new Vector2d(8,5);
      indexToActionLoc[locToIndex(6,7)] = new Vector2d(5,6);
      indexToActionLoc[locToIndex(8,7)] = new Vector2d(10,7);
    }
  }

  /**
   * Checks if the given location is valid for holding a tile.
   * @param c The location column.
   * @param l The location line.
   * @return {@code true} if the location is valid, {@code false} otherwise.
   */
  public static boolean isLocationValid(int c, int l) {
    initIfNeeded();
    // It's symmetrical, fold over the right side.
    if (c > 8) {
      return (indexToActionLoc[locToIndex(17 - c, 7 - l)] != null);
    } else {
      return (indexToActionLoc[locToIndex(c, l)] != null);
    }
  }

  /**
   * Returns the location of the action given the location of a tile.
   * @param c The location column.
   * @param l The location line.
   * @return An integer vector containing the location of the action, or {@code null} if the passed
   *     location is not valid for a tile.
   */
  public static Vector2d actionLocationForTileLocation(int c, int l) {
    // It's symmetrical, fold over the right side.
    boolean flip = c > 8;
    MutableVector2d result;
    if (flip) {
      result = new MutableVector2d(indexToActionLoc[locToIndex(17 - c, 7 - l)]);
    } else {
      result = new MutableVector2d(indexToActionLoc[locToIndex(c, l)]);
    }
    if (flip) {
      result.set(17 - result.getX(), 7 - result.getY());
    }
    return result;
  }

  private static final double PI_OVER_3 = 1.04719755;
  private static final double PI_OVER_2 = 1.57079633;
  private static final double PI_3_OVER_2 = 4.71238898;

  /**
   * Returns the rotation angle of the tile placed at a given location.
   * @param c The location column.
   * @param l The location line.
   * @return The rotation angle at that location in radians, 0 if it's an invalid location.
   */
  public static double rotationAngleForLocation(int c, int l) {
    Vector2d actionLocation = actionLocationForTileLocation(c, l);
    double actionC = actionLocation.getX();
    double actionL = actionLocation.getY();

    if (actionC < c) {
      return PI_3_OVER_2 + (l - actionL) * PI_OVER_3;
    } else {
      return PI_OVER_2 + (actionL - l) * PI_OVER_3;
    }
  }

}
