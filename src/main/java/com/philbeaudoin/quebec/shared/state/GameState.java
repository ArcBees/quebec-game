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

package com.philbeaudoin.quebec.shared.state;

import java.util.ArrayList;

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.player.Player;
import com.philbeaudoin.quebec.shared.player.PlayerState;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * The state of the entire game.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameState {

  private final GameController gameController;
  private final ArrayList<PlayerState> playerStates;
  private final ArrayList<TileState> tileStates;
  private final ArrayList<LeaderCard> availableLeaderCards;

  private final InfluenceZoneState influenceZoneState[] = new InfluenceZoneState[5];

  private int century;

  private PossibleActions possibleActions;

  public GameState(GameController gameController) {
    this.gameController = gameController;
    playerStates = new ArrayList<PlayerState>();
    tileStates = new ArrayList<TileState>();
    availableLeaderCards = new ArrayList<LeaderCard>();
    for (int i = 0; i < influenceZoneState.length; ++i) {
      influenceZoneState[i] = new InfluenceZoneState();
    }
    century = 0;
  }

  /**
   * Copy constructor performing a deep copy of every mutable object contained in this one.
   * @param other The game state to copy.
   */
  public GameState(GameState other) {
    gameController = other.gameController;
    century = other.century;
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
   * Access the current century.
   * @returns The current century.
   */
  public int getCentury() {
    return century;
  }

  /**
   * Set the current century.
   * @param century The current century.
   */
  public void setCentury(int century) {
    this.century = century;
  }

  /**
   * Get the states of all the players.
   * @return The player states.
   */
  public ArrayList<PlayerState> getPlayerStates() {
    return playerStates;
  }

  /**
   * Get the state of a given player.
   * @param playerColor The color of the player for which to get the state, must not be
   *     {@code PlayerColor.NONE} or {@code PlayerColor.NEUTRAL}.
   * @return The state of a player with that color, {@code null} if not found.
   */
  public PlayerState getPlayerState(PlayerColor playerColor) {
    assert playerColor.isNormalColor();
    for (PlayerState playerState : playerStates) {
      if (playerState.getPlayer().getColor() == playerColor) {
        return playerState;
      }
    }
    return null;
  }

  /**
   * Get the states of all the tiles.
   * @return The tile states.
   */
  public ArrayList<TileState> getTileStates() {
    return tileStates;
  }

  /**
   * Get the state of a given tile. The tiles are compared by pointer, so they must come from the
   * same pool.
   * @param tile The tile for which to get the state.
   * @return The state of that tile, {@code null} if not found.
   */
  public TileState findTileState(Tile tile) {
    for (TileState tileState : tileStates) {
      if (tileState.getTile() == tile) {
        return tileState;
      }
    }
    return null;
  }

  /**
   * Get the state of the tile containing the given architect.
   * @param playerColor The color of the architect to look for, cannot be NONE.
   * @return The state of that tile, {@code null} if this architect was not found on any tile.
   */
  public TileState findTileUnderArchitect(PlayerColor playerColor) {
    for (TileState tileState : tileStates) {
      if (tileState.getArchitect() == playerColor) {
        return tileState;
      }
    }
    return null;
  }

  /**
   * Get the state of the tile at a specific location the given architect.
   * @param location The location at which to look for a tile.
   * @return The state of that tile, {@code null} if no tile was found at that location.
   */
  public TileState findTileAtLocation(Vector2d location) {
    for (TileState tileState : tileStates) {
      if (tileState.getLocation().equals(location)) {
        return tileState;
      }
    }
    return null;
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
   * Sets the player and number of cubes in a given influence zone.
   * TODO(beaudoin): Can we get rid of this and use only change actions?
   * @param influenceType The type of the influence zone.
   * @param playerColor The color of the player, must not be NONE or NEUTRAL.
   * @param nbCubes The number of cubes in the influence zone for that player.
   */
  public void setPlayerCubesInInfluenceZone(InfluenceType influenceType, PlayerColor playerColor,
      int nbCubes) {
    assert playerColor.isNormalColor();
    influenceZoneState[influenceType.ordinal()].setCubesForPlayer(playerColor, nbCubes);
  }

  /**
   * Returns the state of the currently active player.
   * @return The currently active player state.
   */
  public PlayerState getCurrentPlayer() {
    for (PlayerState playerState : playerStates) {
      if (playerState.isCurrentPlayer()) {
        return playerState;
      }
    }
    assert false;
    return null;
  }

  /**
   * Access the possible actions in this state.
   * @return The possible actions in the current state.
   */
  public PossibleActions getPossibleActions() {
    return possibleActions;
  }

  /**
   * Set the possible actions in this state.
   * @param possibleActions The possible actions in the current state.
   */
  public void setPossibleActions(PossibleActions possibleActions) {
    this.possibleActions = possibleActions;
  }

  /**
   * Setup the initial game state for a given list of player.
   * @param players The list of players.
   */
  public void initGame(ArrayList<Player> players) {
    gameController.initGame(this, players);
  }

  /**
   * Prepare the beginning of the next century.
   */
  public void prepareNextCentury() {
    gameController.prepareNextCentury(this);
  }

  /**
   * Switch to the next player and setup the board for it.
   * @param prepareActions True to prepare the possible actions, false to not prepare them.
   */
  public void nextPlayer(boolean prepareActions) {
    boolean lastWasActive = false;
    for (PlayerState playerState : playerStates) {
      if (lastWasActive) {
        playerState.setCurrentPlayer(true);
        lastWasActive = false;
      } else if (playerState.isCurrentPlayer()) {
        lastWasActive = true;
        playerState.setCurrentPlayer(false);
      }
    }
    if (lastWasActive) {
      playerStates.get(0).setCurrentPlayer(true);
    }
    if (prepareActions) {
      gameController.configurePossibleActions(this);
    }
  }

  /**
   * Set the current player to a given color.
   * @param playerColor The player to set as active.
   */
  public void setCurrentPlayer(PlayerColor playerColor) {
    for (PlayerState playerState : playerStates) {
      playerState.setCurrentPlayer(playerState.getPlayer().getColor() == playerColor);
    }
  }

  /**
   * Count the number of player who hold a player card.
   * @return The number of player who hold a player card.
   */
  public int nbPlayerWithLeaders() {
    int result = 0;
    for (PlayerState playerState : playerStates) {
      if (playerState.getLeaderCard() != null) {
        result++;
      }
    }
    return result;
  }

  /**
   * Access the controller of this game state.
   * @return The controller.
   */
  public GameController getController() {
    return gameController;
  }

  /**
   * Checks whether or not the current set of possible actions is associated with a global message.
   * @return True if there is a global possible action message, false otherwise.
   */
  public boolean hasPossibleActionMessage() {
    return possibleActions != null && possibleActions.getTextBoxInfo() != null;
  }

  /**
   * Returns the number of players in the game.
   * @return The number of players.
   */
  public int getNbPlayers() {
    return playerStates.size();
  }
}
