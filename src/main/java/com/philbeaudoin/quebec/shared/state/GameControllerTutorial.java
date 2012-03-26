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

import com.philbeaudoin.quebec.shared.action.ActionActivateCubes;
import com.philbeaudoin.quebec.shared.action.ActionExplicit;
import com.philbeaudoin.quebec.shared.action.ActionExplicitHighlightActiveTiles;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.action.GameAction;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.message.BoardLocation;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.message.TextBoxInfo;
import com.philbeaudoin.quebec.shared.player.Player;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeQueuePossibleActions;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeReinit;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * A controller to manipulate the state of a game for the tutorial.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameControllerTutorial implements GameController {

  private PossibleActions startingActions;

  @Override
  public void initGame(GameState gameState, List<Player> players) {
    assert players.size() == 4;  // The tutorial is designed only for 4 players.
    GameControllerHelper.resetGameState(gameState, players, false);
    configurePossibleActions(gameState);
  }

  @Override
  public void configurePossibleActions(GameState gameState) {
    // List of all steps in reverse order.
    prependStep("tutorialActivateAfterMove", BoardLocation.BOTTOM_RIGHT_OF_TARGET,
        BoardLocation.BLACK_ACTIVE_CUBES,
        new ActionActivateCubes(3, nextStep()));
    Tile tileForMove1 = findTileForMove1(gameState);
    prependStep("tutorialPerformMoveArchitect", BoardLocation.BOTTOM_CENTER, BoardLocation.NONE,
        new ActionMoveArchitect(tileForMove1, false, 0, nextStep()));
    prependStep("tutorialWhereToMoveArchitect", BoardLocation.BOTTOM_CENTER, BoardLocation.NONE,
        new ActionExplicitHighlightActiveTiles(new Message.Text("continueMsg"), nextStep()));
    prependStep("tutorialFirstMove");
    prependStep("tutorialActiveCubes", BoardLocation.BOTTOM_RIGHT_OF_TARGET,
        BoardLocation.BLACK_ACTIVE_CUBES);
    prependStep("tutorialPassiveCubes", BoardLocation.BOTTOM_RIGHT_OF_TARGET,
        BoardLocation.BLACK_PASSIVE_CUBES);
    prependStep("tutorialArchitect", BoardLocation.BOTTOM_RIGHT_OF_TARGET,
        BoardLocation.BLACK_ARCHITECT_ON_PLAYER_AREA);
    prependStep("tutorialGoal", BoardLocation.TOP_LEFT_OF_TARGET, BoardLocation.SCORE);
    prependStep("tutorialPlayers", BoardLocation.RIGHT_OF_TARGET, BoardLocation.PLAYER_AREAS);
    prependStep("tutorialIntro");
    gameState.setPossibleActions(startingActions);
  }

  @Override
  public void getPossibleMoveArchitectActions(GameState gameState,
      PossibleActions possibleActions) {
    assert false;
  }

  @Override
  public void prepareNextCentury(GameState gameState) {
    assert false;
  }

  /**
   * Prepends a simple scenario step with only a centered message and a continue button. This step
   * will take place before any other that have been added.
   * @param messageMethodName The method name of the message to display.
   */
  private void prependStep(String messageMethodName) {
    prependStep(messageMethodName, BoardLocation.CENTER, BoardLocation.NONE);
  }

  /**
   * Prepends a simple scenario step with a text box, a pointer and a continue button. This step
   * will take place before any other that have been added.
   * @param messageMethodName The method name of the message to display.
   * @param anchor The location where the text box is anchored.
   * @param pointTo The direction the text box points to.
   */
  private void prependStep(String messageMethodName, BoardLocation anchor, BoardLocation pointTo) {
    prependStep(messageMethodName, anchor, pointTo,
        new ActionExplicit(new Message.Text("continueMsg"), nextStep()));
  }

  /**
   * Prepends a complex scenario step with a text box. You must make sure that the last item of the
   * {@link GameStateChange} associated with {@code gameAction} is the one returned by
   * {@link #nextStep()}. This step will take place before any other that have been added.
   * @param messageMethodName The method name of the message to display.
   * @param anchor The location where the text box is anchored.
   * @param pointTo The direction the text box points to.
   * @param gameAction The game action to execute when the step is performed.
   */
  private void prependStep(String messageMethodName, BoardLocation anchor, BoardLocation pointTo,
      GameAction gameAction) {
    prependStep(new TextBoxInfo(new Message.MultilineText(messageMethodName, 0.9), anchor, pointTo),
        gameAction);
  }

  /**
   * Prepends a complex scenario step. You must make sure that the last item of the
   * {@link GameStateChange} associated with {@code gameAction} is the one returned by
   * {@link #nextStep()}. This step will take place before any other that have been added.
   * @param textBoxInfo Information on the text box to display.
   * @param gameAction The game action to execute when the step is performed.
   */
  private void prependStep(TextBoxInfo textBoxInfo, GameAction gameAction) {
    startingActions = new PossibleActions(textBoxInfo);
    startingActions.add(gameAction);
  }

  /**
   * Creates a game state change that initiates the next step in the scenario.
   * @return The game state change.
   */
  private GameStateChange nextStep() {
    if (startingActions != null) {
      return new GameStateChangeQueuePossibleActions(startingActions);
    } else {
      // TODO(beaudoin): This should go back to the menu.
      return new GameStateChangeReinit();
    }
  }

  /**
   * Find the tile on which the user should send his architect for the first move.
   * @param gameState The current game state.
   * @return The tile where the user should send his architect.
   */
  private Tile findTileForMove1(GameState gameState) {
    Vector2d location = new Vector2d(6, 5);
    for (TileState tileState : gameState.getTileStates()) {
      if (tileState.getLocation().equals(location)) {
        assert tileState.isAvailableForArchitect(0);
        return tileState.getTile();
      }
    }
    return null;
  }

}