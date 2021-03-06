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

package com.philbeaudoin.quebec.client.game;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.playerAgent.PlayerAgentFactories;
import com.philbeaudoin.quebec.client.playerAgent.PlayerAgentGenerator;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.GameControllerBasic;
import com.philbeaudoin.quebec.shared.game.action.ActionActivateCubes;
import com.philbeaudoin.quebec.shared.game.action.ActionExplicit;
import com.philbeaudoin.quebec.shared.game.action.ActionExplicitHighlightBoardActions;
import com.philbeaudoin.quebec.shared.game.action.ActionExplicitHighlightTiles;
import com.philbeaudoin.quebec.shared.game.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.game.action.ActionPerformScoringPhase;
import com.philbeaudoin.quebec.shared.game.action.ActionSendCubesToZone;
import com.philbeaudoin.quebec.shared.game.action.ActionSendWorkers;
import com.philbeaudoin.quebec.shared.game.action.ActionTakeLeaderCard;
import com.philbeaudoin.quebec.shared.game.action.GameAction;
import com.philbeaudoin.quebec.shared.game.action.GameActionLifecycle;
import com.philbeaudoin.quebec.shared.game.action.GameActionLifecycleActor;
import com.philbeaudoin.quebec.shared.game.action.PossibleActions;
import com.philbeaudoin.quebec.shared.game.state.Board;
import com.philbeaudoin.quebec.shared.game.state.CannedShuffler;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.game.state.LeaderCard;
import com.philbeaudoin.quebec.shared.game.state.Tile;
import com.philbeaudoin.quebec.shared.game.state.TileState;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeFlipTile;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeMoveArchitect;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeMoveLeader;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeNextPlayer;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeQueuePossibleActions;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeReinit;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeScorePoints;
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationOffboardNeutral;
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationPlayer;
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationTile;
import com.philbeaudoin.quebec.shared.location.CubeDestinationInfluenceZone;
import com.philbeaudoin.quebec.shared.location.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.location.CubeDestinationTile;
import com.philbeaudoin.quebec.shared.location.LeaderDestinationBoard;
import com.philbeaudoin.quebec.shared.location.LeaderDestinationPlayer;
import com.philbeaudoin.quebec.shared.location.Location;
import com.philbeaudoin.quebec.shared.location.LocationBoardAction;
import com.philbeaudoin.quebec.shared.location.LocationBottomCenter;
import com.philbeaudoin.quebec.shared.location.LocationCenter;
import com.philbeaudoin.quebec.shared.location.LocationPlayerAreas;
import com.philbeaudoin.quebec.shared.location.LocationRelative;
import com.philbeaudoin.quebec.shared.location.LocationScore;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.message.TextBoxInfo;
import com.philbeaudoin.quebec.shared.player.Player;
import com.philbeaudoin.quebec.shared.player.PlayerState;
import com.philbeaudoin.quebec.shared.utils.Callback;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * A controller to manipulate the state of a game for the tutorial.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameControllerTutorial implements GameController {

  private static final Location CENTER = new LocationCenter();
  private static final Location BOTTOM_CENTER = new LocationBottomCenter();

  private final PlayerAgentGenerator playerAgentGenerator;
  private final GameStateRenderer gameStateRenderer;

  private PossibleActions startingActions;

  @Inject
  GameControllerTutorial(PlayerAgentFactories playerAgentFactories,
      @Assisted GameStateRenderer gameStateRenderer) {
    this.playerAgentGenerator = playerAgentFactories.createPlayerAgentGenerator(this);
    this.gameStateRenderer = gameStateRenderer;
  }

  @Override
  public void initGame(GameState gameState, List<Player> players) {
    assert players.size() == 4;  // The tutorial is designed only for 4 players.
    GameControllerBasic.resetGameState(gameState, players, new CannedShuffler());
    configurePossibleActions(gameState);
  }

  @Override
  public void configurePossibleActions(GameState gameState) {
    // List of all steps in reverse order.
    Location target;
    prependStep("tutorialGameComplete", BOTTOM_CENTER, null);
    prependStep("tutorialScoreActiveCubes", CENTER, null,
        scorePoints(0, 0, 1, 2, null));
    prependStep("tutorialScoreCubesOnBuildings", BOTTOM_CENTER, null,
        scorePoints(2, 2, 0, 4, null));
    prependStep("tutorialOthersScoreStarTokens", BOTTOM_CENTER, null,
        scorePoints(0, 26, 21, 20, null));
    prependStep("tutorialSmallBlackGroups", BOTTOM_CENTER, null,
        new ActionExplicitHighlightTiles(new Message.Text("continueMsg"),
            getSmallGroupsTiles(gameState), nextStep(scorePoints(8, 0, 0, 0, null))));
    prependStep("tutorialLargestBlackGroup", BOTTOM_CENTER, null,
        new ActionExplicitHighlightTiles(new Message.Text("continueMsg"),
            getMainGroupTiles(gameState), nextStep(scorePoints(25, 0, 0, 0, null))));
    prependStep("tutorialIntroToFinalScoring", BOTTOM_CENTER, null);
    prependStep("tutorialJumpToFinalScoring", BOTTOM_CENTER, null, jumpToFinalScoring(gameState));
    prependStep("tutorialSecondCenturyBegins", CENTER, null,
        ActionPerformScoringPhase.prepareNextCenturyAction(nextStep()));
    target = relativeToTarget(
        new CubeDestinationInfluenceZone(InfluenceType.CULTURAL, PlayerColor.BLACK), -2.5, 1);
    prependStep("tutorialScoringCultural", relativeToTarget(target, -1, 1), target,
        scorePoints(2, 3, 4, 5,
          new GameStateChangeMoveCubes(2,
              new CubeDestinationInfluenceZone(InfluenceType.CULTURAL, PlayerColor.ORANGE),
              new CubeDestinationPlayer(PlayerColor.ORANGE, true))));
    target = relativeToTarget(
        new CubeDestinationInfluenceZone(InfluenceType.ECONOMIC, PlayerColor.BLACK), 2.5, 1);
    prependStep("tutorialScoringEconomic", relativeToTarget(target, 1, 1), target,
        scorePoints(6, 7, 6, 12,
          new GameStateChangeMoveCubes(5,
              new CubeDestinationInfluenceZone(InfluenceType.ECONOMIC, PlayerColor.ORANGE),
              new CubeDestinationInfluenceZone(InfluenceType.CULTURAL, PlayerColor.ORANGE))));
    target = relativeToTarget(
        new CubeDestinationInfluenceZone(InfluenceType.POLITIC, PlayerColor.BLACK), 2.5, -1);
    prependStep("tutorialScoringPolitics", relativeToTarget(target, 1, -1), target,
        scorePoints(7, 2, 7, 0, cascadePolitics()));
    target = relativeToTarget(
        new CubeDestinationInfluenceZone(InfluenceType.RELIGIOUS, PlayerColor.BLACK), -2.5, -1);
    prependStep("tutorialScoringReligious", relativeToTarget(target, -1, -1), target,
        scorePoints(6, 2, 2, 5,
          new GameStateChangeMoveCubes(3,
              new CubeDestinationInfluenceZone(InfluenceType.RELIGIOUS, PlayerColor.BLACK),
              new CubeDestinationInfluenceZone(InfluenceType.POLITIC, PlayerColor.BLACK))));
    prependStep("tutorialCascadingOrder", relativeToTarget(target, -1, -1), target);
    target = relativeToTarget(
        new CubeDestinationInfluenceZone(InfluenceType.CITADEL, PlayerColor.BLACK), -2.5, 0);
    prependStep("tutorialMajorityCitadel", relativeToTarget(target, -1, 0), target,
        new GameStateChangeMoveCubes(2,
            new CubeDestinationInfluenceZone(InfluenceType.CITADEL, PlayerColor.BLACK),
            new CubeDestinationInfluenceZone(InfluenceType.RELIGIOUS, PlayerColor.BLACK)));
    prependStep("tutorialScoringCitadel", relativeToTarget(target, -1, 0), target,
        scorePoints(4, 3, 1, 0, null));
    prependStep("tutorialReturnAllCards", CENTER, null,
        new ActionCustomReturnLeaderCards(nextStep()));
    prependStep("tutorialEndOfCentury");
    prependStep("tutorialSendToInfluenceZone", BOTTOM_CENTER, null,
        new ActionSendCubesToZone(1, true, InfluenceType.CITADEL,
            nextStep(new GameStateChangeNextPlayer(false))));
    prependStep("tutorialCouldMoveArchitectToEnd", BOTTOM_CENTER, null);
    prependStep("tutorialJumpNearTheEnd", CENTER, null,
        new ActionJumpToEndOfCentury(nextStep()));
    prependStep("tutorialLeaderActivateCubes", CENTER, null,
        new ActionCustomTakeLeader(nextStep(new GameStateChangeNextPlayer(false))));
    prependStep("tutorialPickALeader", BOTTOM_CENTER, null,
        new ActionTakeLeaderCard(LeaderCard.RELIGIOUS,
            nextStep(new GameStateChangeNextPlayer(false))),
        new ActionTakeLeaderCard(LeaderCard.POLITIC,
            nextStep(new GameStateChangeNextPlayer(false))),
        new ActionTakeLeaderCard(LeaderCard.ECONOMIC,
            nextStep(new GameStateChangeNextPlayer(false))),
        new ActionTakeLeaderCard(LeaderCard.CULTURAL_FOUR_FIVE,
            nextStep(new GameStateChangeNextPlayer(false))),
        new ActionTakeLeaderCard(LeaderCard.CITADEL,
            nextStep(new GameStateChangeNextPlayer(false))));
    prependStep("tutorialSelectLeader");
    prependJumpAhead3(gameState);
    prependStep("tutorialAfterArchitectMove3", BOTTOM_CENTER, null);
    prependStep("tutorialAfterArchitectMove2", BOTTOM_CENTER, null);
    prependStep("tutorialAfterArchitectMove1", BOTTOM_CENTER, null);
    prependStep("tutorialMoveArchitectAgain", BOTTOM_CENTER, null,
        new ActionMoveArchitect(findTile(gameState, 8, 1), false, 3,
            nextStep(new GameStateChangeNextPlayer(false))));
    prependJumpAhead2(gameState);
    prependStep("tutorialContributingToOwnBuilding", BOTTOM_CENTER, null);
    prependStep("tutorialContributeToOwnBuilding", BOTTOM_CENTER, null,
        new ActionSendWorkers(true, findTile(gameState, 8, 3),
            nextStep(new GameStateChangeNextPlayer(false))));
    prependJumpAhead1(gameState);
    prependStep("tutorialJumpAheadOneTurn", BOTTOM_CENTER, null);
    prependStepWithAction("tutorialDontWorry", BOTTOM_CENTER, null,
        new ActionExplicitHighlightBoardActions(new Message.Text("continueMsg"), nextStep()));
    prependStepWithAction("tutorialAllDistrictHaveAction", BOTTOM_CENTER, null,
        new ActionExplicitHighlightBoardActions(new Message.Text("continueMsg"), nextStep()));
    prependStep("tutorialNowExecuteAction", BOTTOM_CENTER, null,
        new ActionSendCubesToZone(2, false, InfluenceType.POLITIC,
            nextStep(new GameStateChangeNextPlayer(false))),
        new ActionSendCubesToZone(2, false, InfluenceType.CULTURAL,
            nextStep(new GameStateChangeNextPlayer(false))));
    target = relativeToTarget(new LocationBoardAction(Board.actionForTileLocation(6, 5)), 0, 1);
    prependStep("tutorialActionCanBeExecuted", relativeToTarget(target, 0, 2), target);
    prependStep("tutorialPerformSendCubes", BOTTOM_CENTER, null,
        new ActionSendWorkers(true, findTile(gameState, 6, 5), nextStep()));
    target = relativeToTarget(new LocationBoardAction(Board.actionForTileLocation(6, 5)), 0, -1);
    prependStep("tutorialCostOfAContribution", relativeToTarget(target, 0, 2), target,
        new ActionExplicitHighlightTiles(new Message.Text("continueMsg"), 
            getArchitectTiles(gameState), nextStep()));
    target = new CubeDestinationTile(findTile(gameState, 6, 5), PlayerColor.BLACK, 0);
    prependStep("tutorialThreeSpotsPerTile", relativeToTarget(target, 0, 2), target,
        new ActionExplicitHighlightTiles(new Message.Text("continueMsg"), 
            getArchitectTiles(gameState), nextStep()));
    prependStep("tutorialWhereToSendWorkers", BOTTOM_CENTER, null,
        new ActionExplicitHighlightTiles(new Message.Text("continueMsg"), 
            getArchitectTiles(gameState), nextStep()));
    prependStep("tutorialSendActiveWorkers");
    prependStep("tutorialFourthPlayerFirstMove", CENTER, null);
    prependStep("tutorialThirdPlayerFirstMove", CENTER, null,
        generateFirstMove(gameState, PlayerColor.ORANGE, 6, 5));
    prependStep("tutorialSecondPlayerFirstMove", CENTER, null,
        generateFirstMove(gameState, PlayerColor.WHITE, 15, 6));
    prependStep("tutorialGoToSecondPlayerFirstMove", CENTER, null,
        generateFirstMove(gameState, PlayerColor.PINK, 13, 4));
    target = new CubeDestinationPlayer(PlayerColor.BLACK, true);
    prependStep("tutorialActivateAfterMove", relativeToTarget(target, 2, 2), target,
        new ActionActivateCubes(3, nextStep(new GameStateChangeNextPlayer(false))));
    prependStep("tutorialPerformMoveArchitect", BOTTOM_CENTER, null,
        new ActionMoveArchitect(findTile(gameState, 8, 3), false, 0, nextStep()));
    prependStep("tutorialWhereToMoveArchitect", BOTTOM_CENTER, null,
        new ActionExplicitHighlightTiles(new Message.Text("continueMsg"), 
            findActiveTiles(gameState), nextStep()));
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

  @Override
  public void performAction(GameState gameState, GameAction gameAction) {
    // TODO(beaudoin): Remove code duplication between this and the GameControllerClient.
    final GameActionLifecycle gameActionLifecycle = new GameActionLifecycle();
    final GameStateChange gameStateChange = gameAction.execute(this, gameState);
    final GameState stateAfter = new GameState(gameState);
    gameStateChange.apply(this, stateAfter);

    gameActionLifecycle.addActor(gameStateRenderer.createAnimationActor(stateAfter,
        gameStateChange));
    gameActionLifecycle.addActor(new GameActionLifecycleActor() {
      @Override
      public void onStart(Callback completedCallback) {
        completedCallback.execute();
      }

      @Override
      public void onFinalize(Callback completedCallback) {
        completedCallback.execute();
      }

      @Override
      public void onComplete() {
        gameStateRenderer.renderInteractions(stateAfter, playerAgentGenerator);
      }
    });

    gameActionLifecycle.start();
  }

  @Override
  public void setGameState(GameState gameState) {
    // TODO(beaudoin): Remove code duplication between this and the GameControllerClient.
    gameStateRenderer.render(gameState);
    gameStateRenderer.renderInteractions(gameState, playerAgentGenerator);
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
    int oldCentury = gameState.getCentury();
    assert oldCentury < 3;
    int newCentury = oldCentury + 1;
    gameState.setCentury(newCentury);
    for (TileState tileState : gameState.getTileStates()) {
      if (tileState.isAvailableForArchitect(oldCentury)) {
        tileState.setBuildingFacing(true);
      }
    }
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
   * Prepends a complex scenario step with a text box and ensures action description are described
   * when the user hovers his mouse over them. You must make sure that the last item of the
   * {@link GameStateChange} associated with {@code gameAction} is the one returned by
   * {@link #nextStep()}. This step will take place before any other that have been added.
   * @param messageMethodName The method name of the message to display.
   * @param anchor The location where the text box is anchored.
   * @param pointTo The direction the text box points to.
   * @param gameActions The available game actions when the step is performed.
   */
  private void prependStepWithAction(String messageMethodName, Location anchor, Location pointTo,
      GameAction... gameActions) {
    prependStep(new TextBoxInfo(new Message.MultilineText(messageMethodName, 0.9), anchor, pointTo),
        true, gameActions);
  }

  /**
   * Prepends a complex scenario step with a text box. You must make sure that the last item of the
   * {@link GameStateChange} associated with {@code gameAction} is the one returned by
   * {@link #nextStep()}. This step will take place before any other that have been added.
   * @param messageMethodName The method name of the message to display.
   * @param anchor The location where the text box is anchored.
   * @param pointTo The direction the text box points to.
   * @param gameActions The available game actions when the step is performed.
   */
  private void prependStep(String messageMethodName, Location anchor, Location pointTo,
      GameAction... gameActions) {
    prependStep(new TextBoxInfo(new Message.MultilineText(messageMethodName, 0.9), anchor, pointTo),
        false, gameActions);
  }

  /**
   * Prepends a complex scenario step. You must make sure that the last item of the
   * {@link GameStateChange} associated with {@code gameAction} is the one returned by
   * {@link #nextStep()}. This step will take place before any other that have been added.
   * @param textBoxInfo Information on the text box to display.
   * @param displayActions True if the actions should be displayed on mouse hover.
   * @param gameActions The available game actions when the step is performed.
   */
  private void prependStep(TextBoxInfo textBoxInfo, boolean displayActions,
      GameAction... gameActions) {
    startingActions = new PossibleActions(textBoxInfo, displayActions);
    for (GameAction gameAction : gameActions) {
      startingActions.add(gameAction);
    }
  }

  /**
   * Creates a game state change that initiates the next step in the scenario.
   * @return The game state change.
   */
  private GameStateChange nextStep() {
    return nextStep(null);
  }

  /**
   * Creates a game state change that initiates the next step in the scenario.
   * @param prepend The action to do before .
   * @return The game state change.
   */
  private GameStateChange nextStep(GameStateChange prepend) {
    GameStateChange result;
    if (startingActions != null) {
      result = new GameStateChangeQueuePossibleActions(startingActions);
    } else {
      // TODO(beaudoin): This should go back to the menu.
      result = new GameStateChangeReinit();
    }
    if (prepend != null) {
      GameStateChangeComposite composite = new GameStateChangeComposite();
      composite.add(prepend);
      composite.add(result);
      return composite;
    }
    return result;
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
        return tileState.getTile();
      }
    }
    return null;
  }

  /**
   * Find all the active tiles for a given game state.
   * @param gameState The current game state.
   * @return All the currently active tiles.
   */
  private ArrayList<Tile> findActiveTiles(GameState gameState) {
    ArrayList<Tile> tiles = new ArrayList<Tile>();
    int century = gameState.getCentury();
    for (TileState tileState : gameState.getTileStates()) {
      if (tileState.isAvailableForArchitect(century)) {
        tiles.add(tileState.getTile());
      }
    }
    return tiles;
  }

  /**
   * Returns the tiles of the 4 architects that should be highlighted on the fist "send workers"
   * action.
   * @param gameState The current game state.
   * @return The four architect tiles.
   */
  private ArrayList<Tile> getArchitectTiles(GameState gameState) {
    ArrayList<Tile> tiles = new ArrayList<Tile>();
    tiles.add(findTile(gameState, 6, 5));
    tiles.add(findTile(gameState, 15, 6));
    tiles.add(findTile(gameState, 13, 4));
    tiles.add(findTile(gameState, 8, 3));
    return tiles;
  }

  /**
   * Returns the tiles of the main black group during the final scoring
   * @param gameState The current game state.
   * @return All the tiles of the main group.
   */
  private ArrayList<Tile> getMainGroupTiles(GameState gameState) {
    ArrayList<Tile> tiles = new ArrayList<Tile>();
    tiles.add(findTile(gameState, 4, 1));
    tiles.add(findTile(gameState, 6, 1));
    tiles.add(findTile(gameState, 8, 1));
    tiles.add(findTile(gameState, 10, 1));
    tiles.add(findTile(gameState, 7, 2));
    tiles.add(findTile(gameState, 8, 3));
    return tiles;
  }
  /**
   * Returns the tiles of the small black groups during the final scoring
   * @param gameState The current game state.
   * @return All the tiles of the small groups.
   */
  private ArrayList<Tile> getSmallGroupsTiles(GameState gameState) {
    ArrayList<Tile> tiles = new ArrayList<Tile>();
    tiles.add(findTile(gameState, 13, 2));
    tiles.add(findTile(gameState, 10, 5));
    tiles.add(findTile(gameState, 9, 6));
    return tiles;
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
    result.add(new GameStateChangeMoveArchitect(
        new ArchitectDestinationPlayer(color, false),
        new ArchitectDestinationTile(findTile(gameState, x, y), color)));
    result.add(new GameStateChangeMoveCubes(3,
        new CubeDestinationPlayer(color, false),
        new CubeDestinationPlayer(color, true)));
    result.add(new GameStateChangeNextPlayer(false));
    return result;
  }

  /**
   * Prepend a sequence of automatic moves that jump ahead one full turn.
   */
  private void prependJumpAhead1(GameState gameState) {
    PossibleActions pinkSendWorkers = new PossibleActions();
    PossibleActions pinkBoardAction = new PossibleActions();
    PossibleActions whiteSendWorkers = new PossibleActions();
    PossibleActions whiteBoardAction = new PossibleActions();
    PossibleActions orangeSendWorkers = new PossibleActions();
    PossibleActions orangeBoardAction = new PossibleActions();

    GameStateChangeComposite composite = new GameStateChangeComposite();
    composite.add(new GameStateChangeMoveCubes(2,
        new CubeDestinationPlayer(PlayerColor.PINK, true),
        new CubeDestinationTile(findTile(gameState, 8, 3), PlayerColor.PINK, 0)));
    composite.add(new GameStateChangeQueuePossibleActions(pinkBoardAction));
    pinkSendWorkers.add(new ActionExplicit(null, composite, true));

    composite = new GameStateChangeComposite();
    composite.add(new GameStateChangeMoveCubes(1,
        new CubeDestinationPlayer(PlayerColor.PINK, false),
        new CubeDestinationInfluenceZone(InfluenceType.CITADEL, PlayerColor.PINK)));
    composite.add(new GameStateChangeMoveCubes(1,
        new CubeDestinationPlayer(PlayerColor.PINK, false),
        new CubeDestinationInfluenceZone(InfluenceType.ECONOMIC, PlayerColor.PINK)));
    composite.add(new GameStateChangeNextPlayer(false));
    composite.add(new GameStateChangeQueuePossibleActions(whiteSendWorkers));
    pinkBoardAction.add(new ActionExplicit(null, composite, true));

    composite = new GameStateChangeComposite();
    composite.add(new GameStateChangeMoveCubes(2,
        new CubeDestinationPlayer(PlayerColor.WHITE, true),
        new CubeDestinationTile(findTile(gameState, 8, 3), PlayerColor.WHITE, 1)));
    composite.add(new GameStateChangeQueuePossibleActions(whiteBoardAction));
    whiteSendWorkers.add(new ActionExplicit(null, composite, true));

    composite = new GameStateChangeComposite();
    composite.add(new GameStateChangeMoveCubes(1,
        new CubeDestinationPlayer(PlayerColor.WHITE, false),
        new CubeDestinationInfluenceZone(InfluenceType.CITADEL, PlayerColor.WHITE)));
    composite.add(new GameStateChangeMoveCubes(1,
        new CubeDestinationPlayer(PlayerColor.WHITE, false),
        new CubeDestinationInfluenceZone(InfluenceType.POLITIC, PlayerColor.WHITE)));
    composite.add(new GameStateChangeNextPlayer(false));
    composite.add(new GameStateChangeQueuePossibleActions(orangeSendWorkers));
    whiteBoardAction.add(new ActionExplicit(null, composite, true));

    composite = new GameStateChangeComposite();
    composite.add(new GameStateChangeMoveCubes(2,
        new CubeDestinationPlayer(PlayerColor.ORANGE, true),
        new CubeDestinationTile(findTile(gameState, 13, 4), PlayerColor.ORANGE, 0)));
    composite.add(new GameStateChangeQueuePossibleActions(orangeBoardAction));
    orangeSendWorkers.add(new ActionExplicit(null, composite, true));

    composite = new GameStateChangeComposite();
    composite.add(new GameStateChangeScorePoints(PlayerColor.ORANGE, 1));
    composite.add(new GameStateChangeMoveCubes(1,
        new CubeDestinationPlayer(PlayerColor.ORANGE, false),
        new CubeDestinationPlayer(PlayerColor.ORANGE, true)));
    composite.add(new GameStateChangeMoveCubes(1,
        new CubeDestinationPlayer(PlayerColor.ORANGE, false),
        new CubeDestinationInfluenceZone(InfluenceType.ECONOMIC, PlayerColor.ORANGE)));
    composite.add(new GameStateChangeNextPlayer(false));
    composite.add(nextStep());
    orangeBoardAction.add(new ActionExplicit(null, composite, true));

    startingActions = pinkSendWorkers;
  }

  /**
   * Prepend a sequence of automatic moves that jump ahead one full turn.
   */
  private void prependJumpAhead2(GameState gameState) {
    PossibleActions pinkSendWorkers = new PossibleActions();
    PossibleActions pinkBoardAction = new PossibleActions();
    PossibleActions whiteSendWorkers = new PossibleActions();
    PossibleActions orangeSendWorkers = new PossibleActions();
    PossibleActions orangeBoardAction = new PossibleActions();

    GameStateChangeComposite composite = new GameStateChangeComposite();
    composite.add(new GameStateChangeMoveCubes(1,
        new CubeDestinationPlayer(PlayerColor.PINK, true),
        new CubeDestinationTile(findTile(gameState, 15, 6), PlayerColor.PINK, 0)));
    composite.add(new GameStateChangeQueuePossibleActions(pinkBoardAction));
    pinkSendWorkers.add(new ActionExplicit(null, composite, true));

    composite = new GameStateChangeComposite();
    composite.add(new GameStateChangeMoveCubes(2,
        new CubeDestinationPlayer(PlayerColor.PINK, false),
        new CubeDestinationInfluenceZone(InfluenceType.CITADEL, PlayerColor.PINK)));
    composite.add(new GameStateChangeNextPlayer(false));
    composite.add(new GameStateChangeQueuePossibleActions(whiteSendWorkers));
    pinkBoardAction.add(new ActionExplicit(null, composite, true));

    composite = new GameStateChangeComposite();
    composite.add(new GameStateChangeMoveCubes(1,
        new CubeDestinationPlayer(PlayerColor.WHITE, true),
        new CubeDestinationTile(findTile(gameState, 15, 6), PlayerColor.WHITE, 1)));
    composite.add(new GameStateChangeNextPlayer(false));
    composite.add(new GameStateChangeQueuePossibleActions(orangeSendWorkers));
    whiteSendWorkers.add(new ActionExplicit(null, composite, true));

    composite = new GameStateChangeComposite();
    composite.add(new GameStateChangeMoveCubes(2,
        new CubeDestinationPlayer(PlayerColor.ORANGE, true),
        new CubeDestinationTile(findTile(gameState, 13, 4), PlayerColor.ORANGE, 1)));
    composite.add(new GameStateChangeQueuePossibleActions(orangeBoardAction));
    orangeSendWorkers.add(new ActionExplicit(null, composite, true));

    composite = new GameStateChangeComposite();
    composite.add(new GameStateChangeScorePoints(PlayerColor.ORANGE, 1));
    composite.add(new GameStateChangeMoveCubes(1,
        new CubeDestinationPlayer(PlayerColor.ORANGE, false),
        new CubeDestinationPlayer(PlayerColor.ORANGE, true)));
    composite.add(new GameStateChangeMoveCubes(1,
        new CubeDestinationPlayer(PlayerColor.ORANGE, false),
        new CubeDestinationInfluenceZone(InfluenceType.ECONOMIC, PlayerColor.ORANGE)));
    composite.add(new GameStateChangeNextPlayer(false));
    composite.add(nextStep());
    orangeBoardAction.add(new ActionExplicit(null, composite, true));

    startingActions = pinkSendWorkers;
  }

  /**
   * Prepend a sequence of automatic moves that jump ahead one full turn.
   */
  private void prependJumpAhead3(GameState gameState) {
    PossibleActions pinkMoveArchitect = new PossibleActions();
    PossibleActions whiteSendWorkers = new PossibleActions();
    PossibleActions whiteBoardAction = new PossibleActions();
    PossibleActions orangeMoveArchitect = new PossibleActions();

    GameStateChangeComposite composite = new GameStateChangeComposite();
    composite.add(new GameStateChangeNextPlayer(false));
    composite.add(new GameStateChangeQueuePossibleActions(whiteSendWorkers));
    pinkMoveArchitect.add(new ActionMoveArchitect(findTile(gameState, 12, 5), false, 3,
        composite, true));

    composite = new GameStateChangeComposite();
    composite.add(new GameStateChangeMoveCubes(2,
        new CubeDestinationPlayer(PlayerColor.WHITE, true),
        new CubeDestinationTile(findTile(gameState, 6, 5), PlayerColor.WHITE, 1)));
    composite.add(new GameStateChangeQueuePossibleActions(whiteBoardAction));
    whiteSendWorkers.add(new ActionExplicit(null, composite, true));

    composite = new GameStateChangeComposite();
    composite.add(new GameStateChangeMoveCubes(2,
        new CubeDestinationPlayer(PlayerColor.WHITE, false),
        new CubeDestinationInfluenceZone(InfluenceType.POLITIC, PlayerColor.WHITE)));
    composite.add(new GameStateChangeNextPlayer(false));
    composite.add(new GameStateChangeQueuePossibleActions(orangeMoveArchitect));
    whiteBoardAction.add(new ActionExplicit(null, composite, true));

    composite = new GameStateChangeComposite();
    composite.add(new GameStateChangeNextPlayer(false));
    composite.add(nextStep());
    orangeMoveArchitect.add(new ActionMoveArchitect(findTile(gameState, 7, 6), false, 3,
        composite, true));

    startingActions = pinkMoveArchitect;
  }

  /**
   * Generates a composite game state change jumping ahead until the final scoring.
   * @param gameState The game state at the beginning of the game.
   * @return The game state change
   */
  private GameStateChange jumpToFinalScoring(GameState gameState) {
    GameStateChangeComposite result = new GameStateChangeComposite();

    // Make orange active
    result.add(new GameStateChangeNextPlayer(false));
    result.add(new GameStateChangeNextPlayer(false));

    // Move architects to 4th century buildings.
    result.add(new GameStateChangeMoveArchitect(
        new ArchitectDestinationTile(findTile(gameState, 13, 2), PlayerColor.BLACK),
        new ArchitectDestinationTile(findTile(gameState, 11, 0), PlayerColor.BLACK)));  // 2 cubes.
    result.add(new GameStateChangeMoveArchitect(
        new ArchitectDestinationTile(findTile(gameState, 1, 4), PlayerColor.PINK),
        new ArchitectDestinationTile(findTile(gameState, 9, 4), PlayerColor.PINK)));  // 2 cubes.
    result.add(new GameStateChangeMoveArchitect(
        new ArchitectDestinationTile(findTile(gameState, 14, 5), PlayerColor.WHITE),
        new ArchitectDestinationTile(findTile(gameState, 2, 1), PlayerColor.WHITE)));  // 1 cube.
    result.add(new GameStateChangeMoveArchitect(
        new ArchitectDestinationTile(findTile(gameState, 8, 7), PlayerColor.ORANGE),
        new ArchitectDestinationTile(findTile(gameState, 4, 3), PlayerColor.ORANGE)));  // 2 cubes.

    // Move cubes to active buildings.
    result.add(new GameStateChangeMoveCubes(2,
        new CubeDestinationTile(findTile(gameState, 1, 4), PlayerColor.ORANGE, 0),
        new CubeDestinationTile(findTile(gameState, 11, 0), PlayerColor.ORANGE, 0)));
    result.add(new GameStateChangeMoveCubes(2,
        new CubeDestinationTile(findTile(gameState, 1, 4), PlayerColor.BLACK, 1),
        new CubeDestinationTile(findTile(gameState, 9, 4), PlayerColor.BLACK, 1)));
    result.add(new GameStateChangeMoveCubes(2,
        new CubeDestinationTile(findTile(gameState, 13, 2), PlayerColor.ORANGE, 0),
        new CubeDestinationTile(findTile(gameState, 11, 0), PlayerColor.ORANGE, 1)));
    result.add(new GameStateChangeMoveCubes(2,
        new CubeDestinationTile(findTile(gameState, 13, 2), PlayerColor.PINK, 1),
        new CubeDestinationTile(findTile(gameState, 4, 3), PlayerColor.PINK, 0)));

    // Move cubes to influence zones.
    result.add(new GameStateChangeMoveCubes(3,
        new CubeDestinationTile(findTile(gameState, 14, 5), PlayerColor.PINK, 0),
        new CubeDestinationInfluenceZone(InfluenceType.RELIGIOUS, PlayerColor.PINK)));
    result.add(new GameStateChangeMoveCubes(3,
        new CubeDestinationTile(findTile(gameState, 8, 7), PlayerColor.BLACK, 0),
        new CubeDestinationInfluenceZone(InfluenceType.POLITIC, PlayerColor.BLACK)));

    result.add(new GameStateChangeMoveCubes(1,
        new CubeDestinationPlayer(PlayerColor.BLACK, false),
        new CubeDestinationInfluenceZone(InfluenceType.CITADEL, PlayerColor.BLACK)));
    result.add(new GameStateChangeMoveCubes(4,
        new CubeDestinationPlayer(PlayerColor.BLACK, false),
        new CubeDestinationInfluenceZone(InfluenceType.RELIGIOUS, PlayerColor.BLACK)));
    result.add(new GameStateChangeMoveCubes(6,
        new CubeDestinationPlayer(PlayerColor.BLACK, false),
        new CubeDestinationInfluenceZone(InfluenceType.ECONOMIC, PlayerColor.BLACK)));
    result.add(new GameStateChangeMoveCubes(4,
        new CubeDestinationPlayer(PlayerColor.BLACK, false),
        new CubeDestinationInfluenceZone(InfluenceType.CULTURAL, PlayerColor.BLACK)));

    result.add(new GameStateChangeMoveCubes(3,
        new CubeDestinationPlayer(PlayerColor.PINK, false),
        new CubeDestinationInfluenceZone(InfluenceType.RELIGIOUS, PlayerColor.PINK)));
    result.add(new GameStateChangeMoveCubes(4,
        new CubeDestinationPlayer(PlayerColor.PINK, false),
        new CubeDestinationInfluenceZone(InfluenceType.POLITIC, PlayerColor.PINK)));
    result.add(new GameStateChangeMoveCubes(5,
        new CubeDestinationPlayer(PlayerColor.PINK, false),
        new CubeDestinationInfluenceZone(InfluenceType.ECONOMIC, PlayerColor.PINK)));
    result.add(new GameStateChangeMoveCubes(3,
        new CubeDestinationPlayer(PlayerColor.PINK, false),
        new CubeDestinationInfluenceZone(InfluenceType.CULTURAL, PlayerColor.PINK)));

    result.add(new GameStateChangeMoveCubes(2,
        new CubeDestinationPlayer(PlayerColor.WHITE, false),
        new CubeDestinationInfluenceZone(InfluenceType.CITADEL, PlayerColor.WHITE)));
    result.add(new GameStateChangeMoveCubes(3,
        new CubeDestinationPlayer(PlayerColor.WHITE, false),
        new CubeDestinationInfluenceZone(InfluenceType.RELIGIOUS, PlayerColor.WHITE)));
    result.add(new GameStateChangeMoveCubes(5,
        new CubeDestinationPlayer(PlayerColor.WHITE, false),
        new CubeDestinationInfluenceZone(InfluenceType.POLITIC, PlayerColor.WHITE)));
    result.add(new GameStateChangeMoveCubes(4,
        new CubeDestinationPlayer(PlayerColor.WHITE, false),
        new CubeDestinationInfluenceZone(InfluenceType.ECONOMIC, PlayerColor.WHITE)));
    result.add(new GameStateChangeMoveCubes(6,
        new CubeDestinationPlayer(PlayerColor.WHITE, false),
        new CubeDestinationInfluenceZone(InfluenceType.CULTURAL, PlayerColor.WHITE)));

    result.add(new GameStateChangeMoveCubes(1,
        new CubeDestinationPlayer(PlayerColor.ORANGE, false),
        new CubeDestinationInfluenceZone(InfluenceType.CITADEL, PlayerColor.ORANGE)));
    result.add(new GameStateChangeMoveCubes(3,
        new CubeDestinationPlayer(PlayerColor.ORANGE, false),
        new CubeDestinationInfluenceZone(InfluenceType.RELIGIOUS, PlayerColor.ORANGE)));
    result.add(new GameStateChangeMoveCubes(5,
        new CubeDestinationPlayer(PlayerColor.ORANGE, false),
        new CubeDestinationInfluenceZone(InfluenceType.POLITIC, PlayerColor.ORANGE)));
    result.add(new GameStateChangeMoveCubes(5,
        new CubeDestinationPlayer(PlayerColor.ORANGE, false),
        new CubeDestinationInfluenceZone(InfluenceType.ECONOMIC, PlayerColor.ORANGE)));

    // Make some active cubes
    result.add(new GameStateChangeMoveCubes(1,
        new CubeDestinationPlayer(PlayerColor.PINK, false),
        new CubeDestinationPlayer(PlayerColor.PINK, true)));
    result.add(new GameStateChangeMoveCubes(2,
        new CubeDestinationPlayer(PlayerColor.WHITE, false),
        new CubeDestinationPlayer(PlayerColor.WHITE, true)));
    result.add(new GameStateChangeMoveCubes(2,
        new CubeDestinationPlayer(PlayerColor.ORANGE, false),
        new CubeDestinationPlayer(PlayerColor.ORANGE, true)));

    // Flip some tiles and add some stars.
    result.add(new GameStateChangeFlipTile(findTile(gameState, 13, 2), PlayerColor.BLACK, 2));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 1, 4), PlayerColor.PINK, 3));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 14, 5), PlayerColor.WHITE, 2));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 8, 7), PlayerColor.ORANGE, 1));

    result.add(new GameStateChangeFlipTile(findTile(gameState, 4, 1), PlayerColor.BLACK, 3));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 6, 1), PlayerColor.BLACK, 1));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 7, 2), PlayerColor.BLACK, 3));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 10, 1), PlayerColor.BLACK, 2));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 10, 5), PlayerColor.BLACK, 3));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 9, 6), PlayerColor.BLACK, 3));

    result.add(new GameStateChangeFlipTile(findTile(gameState, 0, 3), PlayerColor.PINK, 1));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 11, 2), PlayerColor.PINK, 2));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 10, 3), PlayerColor.PINK, 3));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 12, 3), PlayerColor.PINK, 1));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 7, 4), PlayerColor.PINK, 3));

    result.add(new GameStateChangeFlipTile(findTile(gameState, 3, 2), PlayerColor.WHITE, 2));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 11, 6), PlayerColor.WHITE, 1));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 13, 6), PlayerColor.WHITE, 3));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 17, 4), PlayerColor.WHITE, 3));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 16, 3), PlayerColor.WHITE, 1));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 15, 2), PlayerColor.WHITE, 2));

    result.add(new GameStateChangeFlipTile(findTile(gameState, 2, 3), PlayerColor.ORANGE, 1));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 17, 2), PlayerColor.ORANGE, 3));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 14, 1), PlayerColor.ORANGE, 3));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 3, 6), PlayerColor.ORANGE, 2));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 4, 5), PlayerColor.ORANGE, 1));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 0, 5), PlayerColor.ORANGE, 3));

    result.add(new GameStateChangeFlipTile(findTile(gameState, 9, 0), PlayerColor.NONE, 0));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 5, 2), PlayerColor.NONE, 0));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 15, 4), PlayerColor.NONE, 0));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 15, 4), PlayerColor.NONE, 0));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 6, 7), PlayerColor.NONE, 0));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 2, 5), PlayerColor.NONE, 0));
    result.add(new GameStateChangeFlipTile(findTile(gameState, 5, 4), PlayerColor.NONE, 0));

    // Add some points.
    result.add(new GameStateChangeScorePoints(PlayerColor.BLACK, 72));
    result.add(new GameStateChangeScorePoints(PlayerColor.PINK, 78));
    result.add(new GameStateChangeScorePoints(PlayerColor.WHITE, 81));
    result.add(new GameStateChangeScorePoints(PlayerColor.ORANGE, 77));

    return result;
  }

  /**
   * Generates a composite game state change where each player scores points.
   * @param black The number of points scored by the black player.
   * @param pink The number of points scored by the pink player.
   * @param white The number of points scored by the white player.
   * @param orange The number of points scored by the orange player.
   * @param followup An action to do after scoring, if null do nothing.
   * @return The game state change
   */
  private GameStateChange scorePoints(int black, int pink, int white, int orange,
      GameStateChange followup) {
    GameStateChangeComposite result = new GameStateChangeComposite();
    result.add(new GameStateChangeScorePoints(PlayerColor.BLACK, black));
    result.add(new GameStateChangeScorePoints(PlayerColor.PINK, pink));
    result.add(new GameStateChangeScorePoints(PlayerColor.WHITE, white));
    result.add(new GameStateChangeScorePoints(PlayerColor.ORANGE, orange));
    if (followup != null) {
      result.add(followup);
    }
    return result;
  }

  /**
   * Generates the state change for cascading from politics to economics.
   * @return The politics cascade.
   */
  private GameStateChange cascadePolitics() {
    GameStateChangeComposite result = new GameStateChangeComposite();
    result.add(new GameStateChangeMoveCubes(3,
        new CubeDestinationInfluenceZone(InfluenceType.POLITIC, PlayerColor.BLACK),
        new CubeDestinationInfluenceZone(InfluenceType.ECONOMIC, PlayerColor.BLACK)));
    result.add(new GameStateChangeMoveCubes(3,
        new CubeDestinationInfluenceZone(InfluenceType.POLITIC, PlayerColor.WHITE),
        new CubeDestinationInfluenceZone(InfluenceType.ECONOMIC, PlayerColor.WHITE)));
    return result;
  }

  @SuppressWarnings("serial")
  private class ActionCustomTakeLeader extends ActionExplicit {

    public ActionCustomTakeLeader(GameStateChange action) {
      super(new Message.Text("continueMsg"), action);
    }

    /**
     * For serialization only.
     */
    @SuppressWarnings("unused")
    private ActionCustomTakeLeader() {
    }

    @Override
    public GameStateChange execute(GameController gameController, GameState gameState) {
      GameStateChangeComposite result = new GameStateChangeComposite();
      ArrayList<LeaderCard> leaders = gameState.getAvailableLeaderCards();
      PlayerColor color = gameState.getCurrentPlayer().getColor();
      if (leaders.contains(LeaderCard.RELIGIOUS)) {
        result.add(new GameStateChangeMoveLeader(
            new LeaderDestinationBoard(LeaderCard.RELIGIOUS),
            new LeaderDestinationPlayer(LeaderCard.RELIGIOUS, color)));
      } else {
        assert leaders.contains(LeaderCard.POLITIC);
        result.add(new GameStateChangeMoveLeader(
            new LeaderDestinationBoard(LeaderCard.POLITIC),
            new LeaderDestinationPlayer(LeaderCard.POLITIC, color)));
      }
      result.add(new GameStateChangeMoveCubes(1,
          new CubeDestinationPlayer(color, false), new CubeDestinationPlayer(color, true)));
      result.add(super.execute(gameController, gameState));
      return result;
    }
  }

  @SuppressWarnings("serial")
  private class ActionCustomReturnLeaderCards extends ActionExplicit {

    public ActionCustomReturnLeaderCards(GameStateChange action) {
      super(new Message.Text("continueMsg"), action);
    }

    /**
     * For serialization only.
     */
    @SuppressWarnings("unused")
    private ActionCustomReturnLeaderCards() {
    }

    @Override
    public GameStateChange execute(GameController gameController, GameState gameState) {
      GameStateChangeComposite result = new GameStateChangeComposite();
      PlayerState playerState = gameState.getPlayerState(PlayerColor.BLACK);
      LeaderCard leaderCard = playerState.getLeaderCard();
      result.add(new GameStateChangeMoveLeader(
          new LeaderDestinationPlayer(leaderCard, PlayerColor.BLACK),
          new LeaderDestinationBoard(leaderCard)));
      if (playerState.isHoldingNeutralArchitect()) {
        result.add(new GameStateChangeMoveArchitect(
            new ArchitectDestinationPlayer(PlayerColor.BLACK, true),
            new ArchitectDestinationOffboardNeutral()));
      }
      leaderCard = gameState.getPlayerState(PlayerColor.PINK).getLeaderCard();
      result.add(new GameStateChangeMoveLeader(
          new LeaderDestinationPlayer(leaderCard, PlayerColor.PINK),
          new LeaderDestinationBoard(leaderCard)));
      result.add(super.execute(gameController, gameState));
      return result;
    }
  }

  @SuppressWarnings("serial")
  private class ActionJumpToEndOfCentury extends ActionExplicit {

    public ActionJumpToEndOfCentury(GameStateChange action) {
      super(new Message.Text("continueMsg"), action);
    }

    /**
     * For serialization only.
     */
    @SuppressWarnings("unused")
    private ActionJumpToEndOfCentury() {
    }

    @Override
    public GameStateChange execute(GameController gameController, GameState gameState) {
      GameStateChangeComposite result = new GameStateChangeComposite();

      // Black 11/5 or 8/5
      // Pink 8/7
      // White 12/1
      // Orange 9/7

      // In Citadel
      //  3 pink
      //  1 white
      //  0/3 black + 3/0
      if (gameState.getPlayerCubesInInfluenceZone(InfluenceType.CITADEL, PlayerColor.BLACK) == 0) {
        result.add(new GameStateChangeMoveCubes(3,
            new CubeDestinationPlayer(PlayerColor.BLACK, false),
            new CubeDestinationInfluenceZone(InfluenceType.CITADEL, PlayerColor.BLACK)));
      }

      // Black = 8/5

      // In purple zone.
      //  4 orange + 1 orange
      //  2 black + 2 black
      //  2 white
      //  2 pink
      result.add(new GameStateChangeMoveCubes(1,
          new CubeDestinationPlayer(PlayerColor.ORANGE, true),
          new CubeDestinationInfluenceZone(InfluenceType.RELIGIOUS, PlayerColor.ORANGE)));
      result.add(new GameStateChangeMoveCubes(2,
          new CubeDestinationPlayer(PlayerColor.BLACK, true),
          new CubeDestinationInfluenceZone(InfluenceType.RELIGIOUS, PlayerColor.BLACK)));

      // Orange = 9/6
      // Black = 8/3

      // In red zone.
      //  2/4 black + 2/0
      //  5 white + 1 (from tile) + 1
      //  0 pink + 1 (from tile) + 1
      if (gameState.getPlayerCubesInInfluenceZone(InfluenceType.POLITIC, PlayerColor.BLACK) == 2) {
        result.add(new GameStateChangeMoveCubes(2,
            new CubeDestinationPlayer(PlayerColor.BLACK, true),
            new CubeDestinationInfluenceZone(InfluenceType.POLITIC, PlayerColor.BLACK)));
      }
      result.add(new GameStateChangeMoveCubes(1,
            new CubeDestinationTile(findTile(gameState, 15, 6), PlayerColor.PINK, 0),
            new CubeDestinationInfluenceZone(InfluenceType.POLITIC, PlayerColor.PINK)));
      result.add(new GameStateChangeMoveCubes(1,
          new CubeDestinationTile(findTile(gameState, 15, 6), PlayerColor.WHITE, 1),
          new CubeDestinationInfluenceZone(InfluenceType.POLITIC, PlayerColor.WHITE)));
      result.add(new GameStateChangeMoveCubes(1,
          new CubeDestinationPlayer(PlayerColor.WHITE, true),
          new CubeDestinationInfluenceZone(InfluenceType.POLITIC, PlayerColor.WHITE)));
      result.add(new GameStateChangeMoveCubes(1,
          new CubeDestinationPlayer(PlayerColor.PINK, true),
          new CubeDestinationInfluenceZone(InfluenceType.POLITIC, PlayerColor.PINK)));

      // Black = 8/3 or 8/1
      // Pink = 8/6

      // In yellow zone.
      //  1 pink + 6
      //  2 orange + 4 + 6
      //  0 white + 3
      //  0 black + 3
      result.add(new GameStateChangeMoveCubes(6,
          new CubeDestinationPlayer(PlayerColor.PINK, true),
          new CubeDestinationInfluenceZone(InfluenceType.ECONOMIC, PlayerColor.PINK)));
      result.add(new GameStateChangeMoveCubes(4,
          new CubeDestinationPlayer(PlayerColor.ORANGE, false),
          new CubeDestinationInfluenceZone(InfluenceType.ECONOMIC, PlayerColor.ORANGE)));
      result.add(new GameStateChangeMoveCubes(6,
          new CubeDestinationPlayer(PlayerColor.ORANGE, true),
          new CubeDestinationInfluenceZone(InfluenceType.ECONOMIC, PlayerColor.ORANGE)));
      result.add(new GameStateChangeMoveCubes(3,
          new CubeDestinationPlayer(PlayerColor.WHITE, false),
          new CubeDestinationInfluenceZone(InfluenceType.ECONOMIC, PlayerColor.WHITE)));
      result.add(new GameStateChangeMoveCubes(3,
          new CubeDestinationPlayer(PlayerColor.BLACK, false),
          new CubeDestinationInfluenceZone(InfluenceType.ECONOMIC, PlayerColor.BLACK)));

      // Black = 5/3 or 5/1
      // Pink = 8/0
      // White = 9/1
      // Orange = 5/0

      // In blue zone.
      //  0/2 black + 2/0
      //  0 pink + 3
      //  0 white + 4
      //  0 orange
      if (gameState.getPlayerCubesInInfluenceZone(InfluenceType.CULTURAL, PlayerColor.BLACK) == 0) {
        result.add(new GameStateChangeMoveCubes(2,
            new CubeDestinationPlayer(PlayerColor.BLACK, true),
            new CubeDestinationInfluenceZone(InfluenceType.CULTURAL, PlayerColor.BLACK)));
      }
      result.add(new GameStateChangeMoveCubes(3,
          new CubeDestinationPlayer(PlayerColor.PINK, false),
          new CubeDestinationInfluenceZone(InfluenceType.CULTURAL, PlayerColor.PINK)));
      result.add(new GameStateChangeMoveCubes(4,
          new CubeDestinationPlayer(PlayerColor.WHITE, false),
          new CubeDestinationInfluenceZone(InfluenceType.CULTURAL, PlayerColor.WHITE)));

      // Black = 5/1
      // Pink = 5/0
      // White = 5/1
      // Orange = 5/0

      // Move architect and set star tokens.
      result.add(new GameStateChangeMoveArchitect(
          new ArchitectDestinationTile(findTile(gameState, 8, 1), PlayerColor.BLACK),
          new ArchitectDestinationTile(findTile(gameState, 13, 2), PlayerColor.BLACK)));
      result.add(new GameStateChangeFlipTile(findTile(gameState, 8, 1), PlayerColor.BLACK, 2));

      result.add(new GameStateChangeMoveArchitect(
          new ArchitectDestinationTile(findTile(gameState, 12, 5), PlayerColor.PINK),
          new ArchitectDestinationTile(findTile(gameState, 1, 4), PlayerColor.PINK)));
      result.add(new GameStateChangeFlipTile(findTile(gameState, 12, 5), PlayerColor.PINK, 3));

      result.add(new GameStateChangeMoveArchitect(
          new ArchitectDestinationTile(findTile(gameState, 15, 6), PlayerColor.WHITE),
          new ArchitectDestinationTile(findTile(gameState, 14, 5), PlayerColor.WHITE)));
      result.add(new GameStateChangeFlipTile(findTile(gameState, 15, 6), PlayerColor.WHITE, 2));

      result.add(new GameStateChangeMoveArchitect(
          new ArchitectDestinationTile(findTile(gameState, 7, 6), PlayerColor.ORANGE),
          new ArchitectDestinationTile(findTile(gameState, 8, 7), PlayerColor.ORANGE)));
      result.add(new GameStateChangeFlipTile(findTile(gameState, 7, 6), PlayerColor.ORANGE, 2));

      // Move some cubes to new tiles.
      result.add(new GameStateChangeMoveCubes(2,
          new CubeDestinationPlayer(PlayerColor.ORANGE, false),
          new CubeDestinationTile(findTile(gameState, 13, 2), PlayerColor.ORANGE, 0)));
      result.add(new GameStateChangeMoveCubes(2,
          new CubeDestinationPlayer(PlayerColor.PINK, false),
          new CubeDestinationTile(findTile(gameState, 13, 2), PlayerColor.PINK, 1)));

      result.add(new GameStateChangeMoveCubes(3,
          new CubeDestinationPlayer(PlayerColor.PINK, false),
          new CubeDestinationTile(findTile(gameState, 14, 5), PlayerColor.PINK, 0)));

      result.add(new GameStateChangeMoveCubes(2,
          new CubeDestinationPlayer(PlayerColor.ORANGE, false),
          new CubeDestinationTile(findTile(gameState, 1, 4), PlayerColor.ORANGE, 0)));
      result.add(new GameStateChangeMoveCubes(2,
          new CubeDestinationPlayer(PlayerColor.BLACK, false),
          new CubeDestinationTile(findTile(gameState, 1, 4), PlayerColor.BLACK, 1)));

      result.add(new GameStateChangeMoveCubes(3,
          new CubeDestinationPlayer(PlayerColor.BLACK, false),
          new CubeDestinationTile(findTile(gameState, 8, 7), PlayerColor.BLACK, 0)));

      // Add a couple of points.
      result.add(new GameStateChangeScorePoints(PlayerColor.PINK, 5));
      result.add(new GameStateChangeScorePoints(PlayerColor.WHITE, 8));
      result.add(new GameStateChangeScorePoints(PlayerColor.ORANGE, 1));

      result.add(new GameStateChangeNextPlayer(false));
      result.add(new GameStateChangeNextPlayer(false));
      result.add(super.execute(gameController, gameState));
      return result;
    }
  }
}