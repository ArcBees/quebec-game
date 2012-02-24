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

import java.util.List;

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.action.ActionPerformScoringPhase;
import com.philbeaudoin.quebec.shared.action.ActionSendCubesToZone;
import com.philbeaudoin.quebec.shared.action.ActionSendWorkers;
import com.philbeaudoin.quebec.shared.action.ActionTakeLeaderCard;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.player.Player;
import com.philbeaudoin.quebec.shared.player.PlayerState;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * A controller to manipulate the state of a game.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameController {

  // Number of cubes per player for a 2, 3, 4, 5 player game.
  private static final int CUBES_FOR_N_PLAYERS[] = { 25, 25, 22, 20 };

  /**
   * Clears the game state and resets the entire game to the initial state.
   * @param gameState The state to reset and initialize.
   */
  public void initGame(GameState gameState, List<Player> players) {
    gameState.setCentury(0);

    int nbPlayers = players.size();
    assert nbPlayers >= 2 && nbPlayers <= 5;
    int cubesPerPlayer = CUBES_FOR_N_PLAYERS[nbPlayers - 2];
    List<PlayerState> playerStates = gameState.getPlayerStates();
    playerStates.clear();
    for (Player player : players) {
      PlayerState playerState = new PlayerState(player);
      playerState.setHoldingArchitect(true);
      playerState.setNbPassiveCubes(cubesPerPlayer - 3);
      playerState.setNbActiveCubes(3);
      playerStates.add(playerState);
    }
    playerStates.get(0).setCurrentPlayer(true);

    List<TileState> tileStates = gameState.getTileStates();
    tileStates.clear();
    TileDeck tileDeck = new TileDeck();
    for (int column = 0; column < 18; ++column) {
      for (int line = 0; line < 8; ++line) {
        BoardAction boardAction = Board.actionForTileLocation(column, line);
        if (boardAction != null) {
          Tile tile = tileDeck.draw(boardAction.getInfluenceType());
          TileState tileState = new TileState(tile, new Vector2d(column, line));
          tileState.setArchitect(PlayerColor.NONE);
          tileStates.add(tileState);
        }
      }
    }

    // TODO: The specific cards available depend on the number of players.
    List<LeaderCard> availableLeaderCards = gameState.getAvailableLeaderCards();
    availableLeaderCards.clear();
    for (LeaderCard leaderCard : LeaderCard.values()) {
      availableLeaderCards.add(leaderCard);
    }

    for (InfluenceType influenceType : InfluenceType.values()) {
      for (PlayerColor playerColor : PlayerColor.NORMAL) {
        gameState.setPlayerCubesInInfluenceZone(influenceType, playerColor, 0);
      }
    }

    configurePossibleActions(gameState);
  }

  /**
   * Configure the possible actions given the current game state.
   * @param gameState The state on which to configure possible actions.
   */
  public void configurePossibleActions(GameState gameState) {
    int century = gameState.getCentury();

    PlayerState currentPlayer = gameState.getCurrentPlayer();
    int nbActiveCubes = currentPlayer.getNbActiveCubes();

    if (currentPlayer.getNbTotalCubes() == 0) {
      // Trigger the scoring right now.
      PossibleActions possibleActions = new PossibleActions(new Message.ScoringPhaseBegins());
      possibleActions.add(new ActionPerformScoringPhase());
      gameState.setPossibleActions(possibleActions);
      return;
    }

    PossibleActions possibleActions = new PossibleActions();
    gameState.setPossibleActions(possibleActions);

    // If the player holds his own architect and it's not the first century, then he must move it
    // back to the board.
    boolean mustMoveArchitect = century > 0 && currentPlayer.isHoldingArchitect();

    // Mark moving architect actions.
    getPossibleMoveArchitectActions(gameState, possibleActions);

    // Mark sending workers as a possible action.
    for (TileState tileState : gameState.getTileStates()) {
      if (!mustMoveArchitect &&
          tileState.getArchitect().isArchitectColor() &&
          nbActiveCubes >= tileState.getCubesPerSpot() &&
          tileState.getColorInSpot(2) == PlayerColor.NONE) {
        possibleActions.add(new ActionSendWorkers(true, tileState.getTile()));
      }
    }

    // Mark moving one cube to influence zones as a possible action.
    if (!mustMoveArchitect && nbActiveCubes >= 1) {
      for (InfluenceType influenceZone : InfluenceType.values()) {
        possibleActions.add(new ActionSendCubesToZone(1, true, influenceZone));
      }
    }

    // Mark getting a leader card as a possible action.
    if (!mustMoveArchitect && currentPlayer.getLeaderCard() == null) {
      for (LeaderCard leaderCard : gameState.getAvailableLeaderCards()) {
        possibleActions.add(new ActionTakeLeaderCard(leaderCard));
      }
    }
  }

  /**
   * Retrieve the possible architect movement actions.
   * @param gameState The state to reset and initialize.
   * @param possibleActions The possible actions into which to add architect movement actions.
   */
  public void getPossibleMoveArchitectActions(GameState gameState,
      PossibleActions possibleActions) {
    PlayerState currentPlayer = gameState.getCurrentPlayer();
    int century = gameState.getCentury();
    boolean canMoveArchitect = false;
    for (TileState tileState : gameState.getTileStates()) {
      canMoveArchitect = addArchitectMoveActionIfPossible(century, possibleActions, currentPlayer,
          tileState) || canMoveArchitect;
    }

    if (!canMoveArchitect) {
      // No tile to move architect to, make it possible to end the round by moving the architect.
      possibleActions.add(new ActionMoveArchitect(null, false));
      // If the player has the yellow leader, he can also move the neutral architect.
      if (currentPlayer.getLeaderCard() == LeaderCard.ECONOMIC &&
          !currentPlayer.isHoldingNeutralArchitect()) {
        possibleActions.add(new ActionMoveArchitect(null, true));
      }
    }
  }

  /**
   * Adds an action to move the architect to the specified tile, if possible.
   * @param century The current century.
   * @param possibleActions The list of possible actions to add to.
   * @param currentPlayer The current player.
   * @param tileState The tile on to which to send the architect, if possible.
   * @return True if the architect can be moved to that tile, false otherwise.
   */
  private boolean addArchitectMoveActionIfPossible(int century,
      PossibleActions possibleActions, PlayerState currentPlayer,
      TileState tileState) {
    if (tileState.getTile().getCentury() == century &&
        tileState.getArchitect() == PlayerColor.NONE &&
        !tileState.isBuildingFacing()) {
      possibleActions.add(new ActionMoveArchitect(tileState.getTile(), false));
      // If the player has the yellow leader, he can also move the neutral architect.
      if (currentPlayer.getLeaderCard() == LeaderCard.ECONOMIC) {
        possibleActions.add(new ActionMoveArchitect(tileState.getTile(), true));
      }
      return true;
    }
    return false;
  }

  /**
   * Prepare the game state for the next century. Leader cards are expected to be returned already.
   * @param gameState The current game state.
   */
  public void prepareNextCentury(GameState gameState) {
    int oldCentury = gameState.getCentury();
    assert oldCentury < 3;
    int newCentury = oldCentury + 1;
    gameState.setCentury(newCentury);
    for (TileState tileState : gameState.getTileStates()) {
      if (!tileState.isBuildingFacing() && tileState.getArchitect() == PlayerColor.NONE &&
          tileState.getTile().getCentury() == oldCentury) {
        tileState.setBuildingFacing(true);
      }
    }
    configurePossibleActions(gameState);
  }
}