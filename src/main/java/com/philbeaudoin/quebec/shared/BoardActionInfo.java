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
 * Information for one of the board actions available in the game.
 *
 * @author Philippe Beaudoin
 */
public class BoardActionInfo {
  private final Vector2d location;
  private final InfluenceType influenceType;
  private final int cubesPerSpot;

  /**
   * Create information for one of the actions in the game.
   *
   * @param column The column of the action spot on the board.
   * @param line The line of the action spot on the board.
   * @param influenceType The type (color) of the influence for this action.
   * @param cubesPerSpot The number of cubes for each spot on a building associated with this
   *     action.
   */
  public BoardActionInfo(int column, int line, InfluenceType influenceType,
      int cubesPerSpot) {
    this.location = new Vector2d(column, line);
    this.influenceType = influenceType;
    this.cubesPerSpot = cubesPerSpot;
  }

  /**
   * @return The location of the action spot on the board, an integer vector.
   */
  public Vector2d getLocation() {
    return location;
  }

  /**
   * @return The type (color) of the influence for this action.
   */
  public InfluenceType getInfluenceType() {
    return influenceType;
  }

  /**
   * @return The number of cubes for each spot on a building associated with this action.
   */
  public int getCubesPerSpot() {
    return cubesPerSpot;
  }
}