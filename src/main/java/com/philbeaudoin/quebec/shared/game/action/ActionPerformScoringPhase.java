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

package com.philbeaudoin.quebec.shared.game.action;

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.ScoringHelper;
import com.philbeaudoin.quebec.shared.ScoringInformation;
import com.philbeaudoin.quebec.shared.ScoringPhase;
import com.philbeaudoin.quebec.shared.ZoneScoringInformation;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.game.state.LeaderCard;
import com.philbeaudoin.quebec.shared.game.state.TileState;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeMoveArchitect;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeMoveLeader;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangePrepareNextCentury;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeQueuePossibleActions;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeScorePoints;
import com.philbeaudoin.quebec.shared.location.ArchitectDestination;
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationOffboardNeutral;
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationPlayer;
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationTile;
import com.philbeaudoin.quebec.shared.location.CubeDestination;
import com.philbeaudoin.quebec.shared.location.CubeDestinationInfluenceZone;
import com.philbeaudoin.quebec.shared.location.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.location.LeaderDestinationBoard;
import com.philbeaudoin.quebec.shared.location.LeaderDestinationPlayer;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.player.PlayerState;

/**
 * The action of performing a given scoring phase.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class ActionPerformScoringPhase implements GameAction {

  private ScoringPhase scoringPhase;
  private GameStateChange followup;

  public ActionPerformScoringPhase() {
    this(ScoringPhase.INIT_SCORING);
  }

  public static ActionPerformScoringPhase prepareNextCenturyAction(GameStateChange followup) {
    return new ActionPerformScoringPhase(ScoringPhase.PREPARE_NEXT_CENTURY, followup);
  }

  private ActionPerformScoringPhase(ScoringPhase scoringPhase) {
    this(scoringPhase, null);
  }

  private ActionPerformScoringPhase(ScoringPhase scoringPhase, GameStateChange followup) {
    this.scoringPhase = scoringPhase;
    this.followup = followup;
  }

  @Override
  public GameStateChange execute(GameController gameController, GameState gameState) {
    int century = gameState.getCentury();
    GameStateChangeComposite result = new GameStateChangeComposite();
    GameStateChange finalResult = result;

    switch (scoringPhase) {
    case INIT_SCORING:
      finalResult = initScoring(gameState, result);
      break;
    case SCORE_INCOMPLETE_BUILDINGS:
      addPlayerScore(result, ScoringHelper.computeIncompleteBuildingScoringInformation(gameState));
      break;
    case SCORE_ACTIVE_CUBES:
      addPlayerScore(result, ScoringHelper.computeActiveCubesScoringInformation(gameState));
      break;
    case SCORE_BUILDINGS:
      addPlayerScore(result, ScoringHelper.computeBuildingsScoringInformation(gameState));
      break;
    case FINISH_GAME:
      // result.add(new GameStateChangeReinit());
      break;
    case PREPARE_NEXT_CENTURY:
      prepareNextCentury(gameState, result);
      break;
    default:
      assert scoringPhase.isZoneScoringPhase();
      ScoringHelper.performZoneScoring(scoringPhase, gameState, result);
    }

    if (scoringPhase != ScoringPhase.PREPARE_NEXT_CENTURY &&
        scoringPhase != ScoringPhase.FINISH_GAME) {
      // TODO(beaudoin): Handle game end somehow.
      ActionPerformScoringPhase nextAction = new ActionPerformScoringPhase(
          scoringPhase.nextScoringPhase(century));
      GameState nextGameState = new GameState(gameState);
      result.apply(gameController, nextGameState);
      PossibleActions possibleActions = new PossibleActions(nextAction.getMessage(nextGameState));
      possibleActions.add(nextAction);
      result.add(new GameStateChangeQueuePossibleActions(possibleActions));
    }

    if (followup != null) {
      result.add(followup);
    }

    return finalResult;
  }

  private GameStateChange initScoring(GameState gameState, GameStateChangeComposite result) {
    GameStateChange finalResult = result;
    // Check if a player has the neutral architect.
    for (PlayerState playerState : gameState.getPlayerStates()) {
      if (playerState.getLeaderCard() == LeaderCard.ECONOMIC) {
        TileState originState = gameState.findTileUnderArchitect(PlayerColor.NEUTRAL);
        ArchitectDestination architectOrigin = null;
        if (originState != null) {
          // The neutral architect is on a building, complete it.
          finalResult = MoveArchitectHelper.completeBuilding(gameState, playerState, originState,
              result);
          architectOrigin = new ArchitectDestinationTile(originState.getTile(),
              PlayerColor.NEUTRAL);
        } else if (playerState.isHoldingNeutralArchitect()) {
          // The neutral architect is still in the player's hand.
          architectOrigin = new ArchitectDestinationPlayer(playerState.getColor(), true);
        }
        if (architectOrigin != null) {
          // Move the neutral architect entirely out of the board.
          result.add(new GameStateChangeMoveArchitect(architectOrigin,
              new ArchitectDestinationOffboardNeutral()));
        }
      }
    }

    // Return all the cards.
    for (PlayerState playerState : gameState.getPlayerStates()) {
      LeaderCard leaderCard = playerState.getLeaderCard();
      if (leaderCard != null) {
        result.add(new GameStateChangeMoveLeader(new LeaderDestinationPlayer(leaderCard,
            playerState.getColor()), new LeaderDestinationBoard(leaderCard)));
      }
    }
    return finalResult;
  }

  private void prepareNextCentury(GameState gameState,
      GameStateChangeComposite result) {
    // Move all cubes back to passive.
    for (PlayerColor playerColor : PlayerColor.NORMAL) {
      CubeDestination destination = new CubeDestinationPlayer(playerColor, false);
      for (InfluenceType zone : InfluenceType.values()) {
        int nbCubes = gameState.getPlayerCubesInInfluenceZone(zone, playerColor);
        if (nbCubes > 0) {
          result.add(new GameStateChangeMoveCubes(nbCubes,
              new CubeDestinationInfluenceZone(zone, playerColor), destination));
        }
      }
    }
    result.add(new GameStateChangePrepareNextCentury());
  }

  /**
   * Adds {@link GameStateChangeScorePoints} to reflect the scoring information of the player.
   * @param result The {@link GameStateChangeComposite} to which to add the game state changes.
   * @param scoringInfo Information on the scores to add.
   */
  private void addPlayerScore(GameStateChangeComposite result,
      ScoringInformation scoringInfo) {
    for (PlayerColor playerColor : PlayerColor.NORMAL) {
      int score = scoringInfo.getScore(playerColor);
      if (score > 0) {
        result.add(new GameStateChangeScorePoints(playerColor, score));
      }
    }
  }

  @Override
  public boolean isAutomatic() {
    return false;
  }

  @Override
  public void accept(GameActionVisitor visitor) {
    visitor.visit(this);
  }

  /**
   * Returns the message that explain this scoring.
   * @param gameState the current game state.
   * @return The message.
   */
  public Message getMessage(GameState gameState) {
    switch (scoringPhase) {
    case INIT_SCORING:
      return new Message.Text("scoringPhaseBegins");
    case SCORE_INCOMPLETE_BUILDINGS:
      return new Message.InformationOnIncompleteBuildingsScore(
          ScoringHelper.computeIncompleteBuildingScoringInformation(gameState));
    case SCORE_ACTIVE_CUBES:
      return new Message.InformationOnActiveCubesScore(
          ScoringHelper.computeActiveCubesScoringInformation(gameState));
    case SCORE_BUILDINGS:
      return new Message.InformationOnBuildingsScore(
          ScoringHelper.computeBuildingsScoringInformation(gameState));
    case FINISH_GAME:
      return new Message.Text("gameCompleted");
    case PREPARE_NEXT_CENTURY:
      return new Message.Text("prepareNextCentury");
    default:
      assert scoringPhase.isZoneScoringPhase();
      return new Message.InformationOnZoneScore(
          ScoringHelper.computeZoneScoringInformation(scoringPhase, gameState));
    }
  }

  /**
   * Computes the scoring information for that action given a game state, provided it's in a phase
   * where an influence zone is being scored.
   * @param gameState The game state.
   * @return The scoring information, or null if there is no scoring information for this phase.
   */
  public ZoneScoringInformation computeZoneScoringInformation(GameState gameState) {
    if (!scoringPhase.isZoneScoringPhase()) {
      return null;
    }
    return ScoringHelper.computeZoneScoringInformation(scoringPhase, gameState);
  }
}
