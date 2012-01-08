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

import java.util.ArrayList;

/**
 * The cubes contained in an influence zone.
 *
 * @author Philippe Beaudoin
 */
public class InfluenceZoneState {

  /**
   * A number of cubes held by a player.
   * @author Philippe Beaudoin
   */
  public class PlayerCubes {
    public PlayerColor playerColor;
    public int cubes;
  }

  private final ArrayList<PlayerCubes> playerCubes = new ArrayList<PlayerCubes>();

  public ArrayList<PlayerCubes> getPlayerCubes() {
    return playerCubes;
  }
}