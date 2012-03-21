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

/**
 * A controller to manipulate the state of a game for a normal game.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameControllerStandard implements GameController {

  @Override
  public void initGame(GameState gameState, List<Player> players) {
    gameState.setCentury(0);
    GameControllerHelper.resetGameState(gameState, players, true);
    configurePossibleActions(gameState);
  }

  @Override
  public void configurePossibleActions(GameState gameState) {
    int century = gameState.getCentury();

    PlayerState currentPlayer = gameState.getCurrentPlayer();
    int nbActiveCubes = currentPlayer.getNbActiveCubes();

    if (currentPlayer.getNbTotalCubes() == 0) {
      // Trigger the scoring right now.
      PossibleActions possibleActions = new PossibleActions(new Message.Text("scoringPhaseBegins"));
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

  @Override
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
    if (tileState.isAvailableForArchitect(century)) {
      possibleActions.add(new ActionMoveArchitect(tileState.getTile(), false));
      // If the player has the yellow leader, he can also move the neutral architect.
      if (currentPlayer.getLeaderCard() == LeaderCard.ECONOMIC) {
        possibleActions.add(new ActionMoveArchitect(tileState.getTile(), true));
      }
      return true;
    }
    return false;
  }

  @Override
  public void prepareNextCentury(GameState gameState) {
    int oldCentury = gameState.getCentury();
    assert oldCentury < 3;
    int newCentury = oldCentury + 1;
    gameState.setCentury(newCentury);
    for (TileState tileState : gameState.getTileStates()) {
      if (tileState.isAvailableForArchitect(oldCentury)) {
        tileState.setBuildingFacing(true);
      }
    }
    configurePossibleActions(gameState);
  }
}