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

import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.action.ActionExplicit;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.message.BoardLocation;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.message.TextBoxInfo;
import com.philbeaudoin.quebec.shared.player.Player;
import com.philbeaudoin.quebec.shared.player.PlayerState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeNextPlayer;

/**
 * A controller to manipulate the state of a game for the tutorial.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameControllerTutorial implements GameController {

  @Override
  public void initGame(GameState gameState, List<Player> players) {
    assert players.size() == 4;  // The tutorial is designed only for 4 players.
    GameControllerHelper.resetGameState(gameState, players, false);
    configurePossibleActions(gameState);
  }

  @Override
  public void configurePossibleActions(GameState gameState) {
    assert gameState.getMoveNumber() == 0;

    PossibleActions possibleActions = new PossibleActions(
        new TextBoxInfo(new Message.MultilineText("tutorialIntro", 0.9), BoardLocation.CENTER));
    gameState.setPossibleActions(possibleActions);
    possibleActions.add(new ActionExplicit(new Message.Text("continueMsg"),
        new GameStateChangeNextPlayer()));
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