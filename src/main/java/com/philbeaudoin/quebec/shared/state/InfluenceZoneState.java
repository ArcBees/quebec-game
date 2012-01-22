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

package com.philbeaudoin.quebec.shared.state;

import com.philbeaudoin.quebec.shared.PlayerColor;

/**
 * The cubes contained in an influence zone.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InfluenceZoneState {

  private final Integer cubesForPlayer[] = new Integer[5];

  /**
   * Creates an empty influence zone.
   */
  public InfluenceZoneState() {
    for (int i = 0; i < 5; i++) {
      cubesForPlayer[i] = new Integer(0);
    }
  }

  /**
   * Copy constructor performing a deep copy of every mutable object contained in this one.
   * @param other The influence zone state to copy.
   */
  public InfluenceZoneState(InfluenceZoneState other) {
    for (int i = 0; i < 5; i++) {
      cubesForPlayer[i] = new Integer(other.cubesForPlayer[i]);
    }
  }

  /**
   * Gets the number of cubes for this player.
   * @param playerColor The player color, must not be NONE or NEUTRAL.
   * @return The number of cubes for this player in this influence zone.
   */
  public int getCubesForPlayer(PlayerColor playerColor) {
    assert playerColor.isNormalColor();
    return cubesForPlayer[playerColor.ordinal() -  1];
  }

  /**
   * Sets the number of cubes for this player.
   * @param playerColor The player color, must not be NONE or NEUTRAL.
   * @param nbCubes The number of cubes for this player in this influence zone.
   */
  public void setCubesForPlayer(PlayerColor playerColor, int nbCubes) {
    assert playerColor.isNormalColor();
    cubesForPlayer[playerColor.ordinal() -  1] = nbCubes;
  }
}
