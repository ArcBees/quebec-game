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
 * The state of the entire game.
 * @author Philippe Beaudoin
 */
public class GameState {

  private final ArrayList<PlayerState> playerStates = new ArrayList<PlayerState>();
  private final ArrayList<TileState> tileStates = new ArrayList<TileState>();
  private final ArrayList<LeaderCard> availableLeaderCards = new ArrayList<LeaderCard>();

  private final InfluenceZoneState influenceZoneState[] = new InfluenceZoneState[5];

  public GameState() {
    for (int i = 0; i < influenceZoneState.length; ++i) {
      influenceZoneState[i] = new InfluenceZoneState();
    }
  }

  /**
   * Get the states of all the players.
   * @return The player states.
   */
  public ArrayList<PlayerState> getPlayerStates() {
    return playerStates;
  }

  /**
   * Get the states of all the tiles.
   * @return The tile states.
   */
  public ArrayList<TileState> getTileStates() {
    return tileStates;
  }

  /**
   * Get the list of unused leader cards.
   * @return The unused player cards.
   */
  public ArrayList<LeaderCard> getAvailableLeaderCards() {
    return availableLeaderCards;
  }

  /**
   * Gets the player and number of cubes in a given influence zone.
   * @param influenceType The type of the influence zone.
   */
  public ArrayList<InfluenceZoneState.PlayerCubes> getPlayerCubesInInfluenceZone(
      InfluenceType influenceType) {
    return influenceZoneState[influenceType.ordinal()].getPlayerCubes();
  }
}
