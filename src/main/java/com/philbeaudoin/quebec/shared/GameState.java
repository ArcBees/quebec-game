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
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameState {

  private final ArrayList<PlayerState> playerStates;
  private final ArrayList<TileState> tileStates;
  private final ArrayList<LeaderCard> availableLeaderCards;

  private final InfluenceZoneState influenceZoneState[] = new InfluenceZoneState[5];

  public GameState() {
    playerStates = new ArrayList<PlayerState>();
    tileStates = new ArrayList<TileState>();
    availableLeaderCards = new ArrayList<LeaderCard>();
    for (int i = 0; i < influenceZoneState.length; ++i) {
      influenceZoneState[i] = new InfluenceZoneState();
    }
  }

  /**
   * Copy constructor performing a deep copy of every mutable object contained in this one.
   * @param other The game state to copy.
   */
  public GameState(GameState other) {
    playerStates = new ArrayList<PlayerState>(other.playerStates.size());
    for (PlayerState playerState : other.playerStates) {
      playerStates.add(new PlayerState(playerState));
    }

    tileStates = new ArrayList<TileState>(other.tileStates.size());
    for (TileState tileState : other.tileStates) {
      tileStates.add(new TileState(tileState));
    }

    availableLeaderCards = new ArrayList<LeaderCard>(other.availableLeaderCards.size());
    for (LeaderCard leaderCard : other.availableLeaderCards) {
      availableLeaderCards.add(leaderCard); // Leader cards are unmutable.
    }

    for (int i = 0; i < influenceZoneState.length; ++i) {
      influenceZoneState[i] = new InfluenceZoneState(other.influenceZoneState[i]);
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
   * @param playerColor The color of the player, must not be NONE or NEUTRAL.
   */
  public int getPlayerCubesInInfluenceZone(InfluenceType influenceType, PlayerColor playerColor) {
    assert playerColor.isNormalColor();
    return influenceZoneState[influenceType.ordinal()].getCubesForPlayer(playerColor);
  }

  /**
   * Gets the player and number of cubes in a given influence zone.
   * @param influenceType The type of the influence zone.
   * @param playerColor The color of the player, must not be NONE or NEUTRAL.
   * @param nbCubes The number of cubes in the influence zone for that player.
   */
  public void setPlayerCubesInInfluenceZone(InfluenceType influenceType, PlayerColor playerColor,
      int nbCubes) {
    assert playerColor.isNormalColor();
    influenceZoneState[influenceType.ordinal()].setCubesForPlayer(playerColor, nbCubes);
  }
}
