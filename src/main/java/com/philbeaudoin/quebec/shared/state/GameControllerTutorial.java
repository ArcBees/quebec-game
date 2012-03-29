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

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionActivateCubes;
import com.philbeaudoin.quebec.shared.action.ActionExplicit;
import com.philbeaudoin.quebec.shared.action.ActionExplicitHighlightActiveTiles;
import com.philbeaudoin.quebec.shared.action.ActionExplicitHighlightArchitectTiles;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.action.GameAction;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationPlayer;
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationTile;
import com.philbeaudoin.quebec.shared.location.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.location.CubeDestinationTile;
import com.philbeaudoin.quebec.shared.location.Location;
import com.philbeaudoin.quebec.shared.location.LocationBottomCenter;
import com.philbeaudoin.quebec.shared.location.LocationCenter;
import com.philbeaudoin.quebec.shared.location.LocationPlayerAreas;
import com.philbeaudoin.quebec.shared.location.LocationRelative;
import com.philbeaudoin.quebec.shared.location.LocationScore;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.message.TextBoxInfo;
import com.philbeaudoin.quebec.shared.player.Player;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveArchitect;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeNextPlayer;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeQueuePossibleActions;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeReinit;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * A controller to manipulate the state of a game for the tutorial.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameControllerTutorial implements GameController {

  private static final Location CENTER = new LocationCenter();
  private static final Location BOTTOM_CENTER = new LocationBottomCenter();
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
    Location target;

    target = new CubeDestinationTile(findTile(gameState, 6, 5), PlayerColor.BLACK, 0);
    prependStep("tutorialThreeSpotsPerTile", relativeToTarget(target, 0, 2), target,
        new ActionExplicitHighlightArchitectTiles(new Message.Text("continueMsg"), nextStep()));
    prependStep("tutorialWhereToSendWorkers", BOTTOM_CENTER, null,
        new ActionExplicitHighlightArchitectTiles(new Message.Text("continueMsg"), nextStep()));
    prependStep("tutorialSendActiveWorkers");
    prependStep("tutorialFourthPlayerFirstMove", CENTER, null,
        new GameStateChangeNextPlayer(false));
    prependStep("tutorialThirdPlayerFirstMove", CENTER, null,
        generateFirstMove(gameState, PlayerColor.ORANGE, 8, 3));
    prependStep("tutorialSecondPlayerFirstMove", CENTER, null,
        generateFirstMove(gameState, PlayerColor.WHITE, 15, 6));
    prependStep("tutorialGoToSecondPlayerFirstMove", CENTER, null,
        generateFirstMove(gameState, PlayerColor.PINK, 13, 4));
    target = new CubeDestinationPlayer(PlayerColor.BLACK, true);
    prependStep("tutorialActivateAfterMove", relativeToTarget(target, 2, 2), target,
        new ActionActivateCubes(3, nextStep()));
    prependStep("tutorialPerformMoveArchitect", BOTTOM_CENTER, null,
        new ActionMoveArchitect(findTile(gameState, 6, 5), false, 0, nextStep()));
    prependStep("tutorialWhereToMoveArchitect", BOTTOM_CENTER, null,
        new ActionExplicitHighlightActiveTiles(new Message.Text("continueMsg"), nextStep()));
    prependStep("tutorialFirstMove");
    target = new CubeDestinationPlayer(PlayerColor.BLACK, true);
    prependStep("tutorialActiveCubes", relativeToTarget(target, 2, 2), target);
    target = new CubeDestinationPlayer(PlayerColor.BLACK, false);
    prependStep("tutorialPassiveCubes", relativeToTarget(target, 2, 2), target);
    target = new ArchitectDestinationPlayer(PlayerColor.BLACK, false);
    prependStep("tutorialArchitect", relativeToTarget(target, 2, 2), target);
    target = new LocationScore(0);
    prependStep("tutorialGoal", relativeToTarget(target, -2, -2), target);
    target = new LocationPlayerAreas();
    prependStep("tutorialPlayers", relativeToTarget(target, 2, 0), target);
    prependStep("tutorialIntro");
    gameState.setPossibleActions(startingActions);
  }

  private Location relativeToTarget(Location target, double x, double y) {
    return new LocationRelative(target, new Vector2d(x, y));
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
    prependStep(messageMethodName, CENTER, null);
  }

  /**
   * Prepends a simple scenario step with a text box, a pointer and a continue button. This step
   * will take place before any other that have been added.
   * @param messageMethodName The method name of the message to display.
   * @param anchor The location where the text box is anchored.
   * @param pointTo The direction the text box points to.
   */
  private void prependStep(String messageMethodName, Location anchor, Location pointTo) {
    prependStep(messageMethodName, anchor, pointTo,
        new ActionExplicit(new Message.Text("continueMsg"), nextStep()));
  }

  /**
   * Prepends a simple scenario step with a text box, a pointer and a continue button. This step
   * will take place before any other that have been added.
   * @param messageMethodName The method name of the message to display.
   * @param anchor The location where the text box is anchored.
   * @param pointTo The direction the text box points to.
   * @param gameStateChange The game state change to apply once the move is executed.
   */
  private void prependStep(String messageMethodName, Location anchor, Location pointTo,
      GameStateChange gameStateChange) {
    GameStateChangeComposite totalGameStateChange = new GameStateChangeComposite();
    totalGameStateChange.add(gameStateChange);
    totalGameStateChange.add(nextStep());
    prependStep(messageMethodName, anchor, pointTo,
        new ActionExplicit(new Message.Text("continueMsg"), totalGameStateChange));
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
  private void prependStep(String messageMethodName, Location anchor, Location pointTo,
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
   * Find the tile at a given position on the board.
   * @param gameState The current game state.
   * @param x The x position.
   * @param y The y position.
   * @return The tile where the user should send his architect.
   */
  private Tile findTile(GameState gameState, int x, int y) {
    Vector2d location = new Vector2d(x, y);
    for (TileState tileState : gameState.getTileStates()) {
      if (tileState.getLocation().equals(location)) {
        assert tileState.isAvailableForArchitect(0);
        return tileState.getTile();
      }
    }
    return null;
  }

  /**
   * Switch to the next player and execute his move.
   * @param gameState The current game state.
   * @param color The player to which we're switching.
   * @param x The x coordinate of the tile on which he sends his architect.
   * @param y The y coordinate of the tile on which he sends his architect.
   * @return A game state change corresponding to the move.
   */
  private GameStateChange generateFirstMove(GameState gameState, PlayerColor color, int x, int y) {
    GameStateChangeComposite result = new GameStateChangeComposite();
    result.add(new GameStateChangeNextPlayer(false));
    result.add(new GameStateChangeMoveArchitect(
        new ArchitectDestinationPlayer(color, false),
        new ArchitectDestinationTile(findTile(gameState, x, y), color)));
    result.add(new GameStateChangeMoveCubes(3,
        new CubeDestinationPlayer(color, false),
        new CubeDestinationPlayer(color, true)));
    return result;
  }

}