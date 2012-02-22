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

package com.philbeaudoin.quebec.shared.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.ScoringInformation;
import com.philbeaudoin.quebec.shared.ScoringPhase;
import com.philbeaudoin.quebec.shared.ZoneScoringInformation;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.state.Board;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.LeaderCard;
import com.philbeaudoin.quebec.shared.state.PlayerState;
import com.philbeaudoin.quebec.shared.state.TileState;
import com.philbeaudoin.quebec.shared.statechange.ArchitectDestination;
import com.philbeaudoin.quebec.shared.statechange.ArchitectDestinationOffboardNeutral;
import com.philbeaudoin.quebec.shared.statechange.ArchitectDestinationPlayer;
import com.philbeaudoin.quebec.shared.statechange.ArchitectDestinationTile;
import com.philbeaudoin.quebec.shared.statechange.CubeDestination;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationInfluenceZone;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveArchitect;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveLeader;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangePrepareNextCentury;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeQueuePossibleActions;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeScorePoints;
import com.philbeaudoin.quebec.shared.statechange.LeaderDestinationBoard;
import com.philbeaudoin.quebec.shared.statechange.LeaderDestinationPlayer;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * The action of performing a given scoring phase.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ActionPerformScoringPhase implements GameAction {

  private final ScoringPhase scoringPhase;

  public ActionPerformScoringPhase() {
    this(ScoringPhase.INIT_SCORING);
  }

  private ActionPerformScoringPhase(ScoringPhase scoringPhase) {
    this.scoringPhase = scoringPhase;
  }

  @Override
  public GameStateChange execute(GameState gameState) {
    int century = gameState.getCentury();
    GameStateChangeComposite result = new GameStateChangeComposite();
    GameStateChange finalResult = result;

    switch (scoringPhase) {
    case INIT_SCORING:
      finalResult = initScoring(gameState, result);
      break;
    case SCORE_INCOMPLETE_BUILDINGS:
      addPlayerScore(result, computeIncompleteBuildingScoringInformation(gameState));
      break;
    case SCORE_ACTIVE_CUBES:
      addPlayerScore(result, computeActiveCubesScoringInformation(gameState));
      break;
    case SCORE_BUILDINGS:
      addPlayerScore(result, computeBuildingsScoringInformation(gameState));
      break;
    case FINISH_GAME:
      break;
    case PREPARE_NEXT_CENTURY:
      prepareNextCentury(gameState, result);
      break;
    default:
      assert scoringPhase.isZoneScoringPhase();
      performZoneScoring(gameState, result);
    }

    if (scoringPhase != ScoringPhase.PREPARE_NEXT_CENTURY &&
        scoringPhase != ScoringPhase.FINISH_GAME) {
      // TODO(beaudoin): Handle game end somehow.
      ActionPerformScoringPhase nextAction = new ActionPerformScoringPhase(
          scoringPhase.nextScoringPhase(century));
      GameState nextGameState = new GameState(gameState);
      result.apply(nextGameState);
      PossibleActions possibleActions = new PossibleActions(nextAction.getMessage(nextGameState));
      possibleActions.add(nextAction);
      result.add(new GameStateChangeQueuePossibleActions(possibleActions));
    }

    return finalResult;
  }

  private void performZoneScoring(GameState gameState,
      GameStateChangeComposite result) {
    ZoneScoringInformation scoringInfo = computeZoneScoringInformation(gameState);
    for (PlayerColor playerColor : PlayerColor.values()) {
      if (playerColor.isNormalColor()) {
        int score = scoringInfo.getScore(playerColor);
        if (score > 0) {
          result.add(new GameStateChangeScorePoints(playerColor, score));
        }
        int cubesToCascade = scoringInfo.getCubesToCascade(playerColor);
        if (cubesToCascade > 0) {
          result.add(new GameStateChangeMoveCubes(cubesToCascade,
              scoringInfo.getOrigin(playerColor), scoringInfo.getDestination(playerColor)));
        }
      }
    }
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
          architectOrigin = new ArchitectDestinationPlayer(playerState.getPlayer().getColor(),
              true);
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
            playerState.getPlayer().getColor()), new LeaderDestinationBoard(leaderCard)));
      }
    }
    return finalResult;
  }

  private void prepareNextCentury(GameState gameState,
      GameStateChangeComposite result) {
    // Move all cubes back to passive.
    for (PlayerColor playerColor : PlayerColor.values()) {
      if (!playerColor.isNormalColor()) {
        continue;
      }
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
    for (PlayerColor playerColor : PlayerColor.values()) {
      if (playerColor.isNormalColor()) {
        int score = scoringInfo.getScore(playerColor);
        if (score > 0) {
          result.add(new GameStateChangeScorePoints(playerColor, score));
        }
      }
    }
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
      return new Message.ScoringPhaseBegins();
    case SCORE_INCOMPLETE_BUILDINGS:
      return new Message.InformationOnIncompleteBuildingsScore(
          computeIncompleteBuildingScoringInformation(gameState));
    case SCORE_ACTIVE_CUBES:
      return new Message.InformationOnActiveCubesScore(
          computeActiveCubesScoringInformation(gameState));
    case SCORE_BUILDINGS:
      return new Message.InformationOnBuildingsScore(
          computeBuildingsScoringInformation(gameState));
    case FINISH_GAME:
      return new Message.GameCompleted();
    case PREPARE_NEXT_CENTURY:
      return new Message.PrepareNextCentury();
    default:
      assert scoringPhase.isZoneScoringPhase();
      return new Message.InformationOnZoneScore(computeZoneScoringInformation(gameState));
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
    int century = gameState.getCentury();
    int scoringZoneIndex = scoringPhase.scoringZoneIndex();
    InfluenceType zoneToScore = InfluenceType.getScoringZoneForCentury(century, scoringZoneIndex);

    ZoneScoringInformation result = new ZoneScoringInformation(scoringZoneIndex, zoneToScore);
    int nbCubesOfLeaders = 0;
    for (PlayerColor playerColor : PlayerColor.values()) {
      if (!playerColor.isNormalColor()) {
        continue;
      }
      int nbCubes = gameState.getPlayerCubesInInfluenceZone(zoneToScore, playerColor);
      result.setScore(playerColor, nbCubes);
      if (nbCubes > nbCubesOfLeaders) {
        nbCubesOfLeaders = nbCubes;
      }
    }

    // Cascade if needed.
    if (nbCubesOfLeaders > 0) {
      int cubesToCascade = Math.min(5, nbCubesOfLeaders / 2);
      for (PlayerColor playerColor : PlayerColor.values()) {
        if (!playerColor.isNormalColor()) {
          continue;
        }
        if (gameState.getPlayerCubesInInfluenceZone(zoneToScore, playerColor) ==
            nbCubesOfLeaders) {
          result.setCubesToCascade(playerColor, cubesToCascade);
          result.setOrigin(playerColor, new CubeDestinationInfluenceZone(zoneToScore, playerColor));
          if (scoringZoneIndex == 4) {
            result.setDestination(playerColor, new CubeDestinationPlayer(playerColor, true));
          } else {
            result.setDestination(playerColor, new CubeDestinationInfluenceZone(
                InfluenceType.getScoringZoneForCentury(
                    century, scoringZoneIndex + 1), playerColor));
          }
        }
      }
    }

    return result;
  }

  /**
   * Computes the scoring information for that action given a game state, provided it's currently
   * scoring buildings.
   * @param gameState The game state.
   * @return The scoring information, or null if there is no scoring information for this phase.
   */
  private ScoringInformation computeIncompleteBuildingScoringInformation(GameState gameState) {
    if (scoringPhase != ScoringPhase.SCORE_INCOMPLETE_BUILDINGS) {
      return null;
    }
    ScoringInformation scoringInformation = new ScoringInformation();
    for (TileState tileState : gameState.getTileStates()) {
      if (tileState.getArchitect().isArchitectColor()) {
        for (int spot = 0; spot < 3; ++spot) {
          PlayerColor playerColor = tileState.getColorInSpot(spot);
          if (playerColor.isNormalColor()) {
            scoringInformation.addToScore(playerColor, tileState.getCubesPerSpot());
          }
        }
      }
    }
    return scoringInformation;
  }

  /**
   * Computes the scoring information for that action given a game state, provided it's currently
   * scoring active cubes.
   * @param gameState The game state.
   * @return The scoring information, or null if there is no scoring information for this phase.
   */
  private ScoringInformation computeActiveCubesScoringInformation(GameState gameState) {
    if (scoringPhase != ScoringPhase.SCORE_ACTIVE_CUBES) {
      return null;
    }
    ScoringInformation scoringInformation = new ScoringInformation();
    for (PlayerState playerState : gameState.getPlayerStates()) {
      PlayerColor playerColor = playerState.getPlayer().getColor();
      scoringInformation.addToScore(playerColor, playerState.getNbActiveCubes() / 2);
    }
    return scoringInformation;
  }

  /**
   * Computes the scoring information for that action given a game state, provided it's currently
   * scoring buildings.
   * @param gameState The game state.
   * @return The scoring information, or null if there is no scoring information for this phase.
   */
  private ScoringInformation computeBuildingsScoringInformation(GameState gameState) {
    if (scoringPhase != ScoringPhase.SCORE_BUILDINGS) {
      return null;
    }
    ScoringInformation result = new ScoringInformation();
    for (PlayerColor playerColor : PlayerColor.values()) {
      if (!playerColor.isNormalColor()) {
        continue;
      }
      Set<TileState> visited = new HashSet<TileState>();
      int totalScore = 0;
      BuildingGroupScore largestGroup = null;
      for (TileState tileState : gameState.getTileStates()) {
        if (tileState.getStarTokenColor() == playerColor && !visited.contains(tileState)) {
          BuildingGroupScore group = new BuildingGroupScore();
          scoreGroup(gameState, playerColor, tileState, visited, group);
          totalScore += group.starScore;
          if (largestGroup == null || group.valueScore > largestGroup.valueScore) {
            largestGroup = group;
          }
        }
      }
      if (largestGroup != null) {
        // Remove the star score and add the value score of the largest group.
        totalScore -= largestGroup.starScore;
        totalScore += largestGroup.valueScore;
      }
      result.setScore(playerColor, totalScore);
    }
    return result;
  }

  private void scoreGroup(GameState gameState, PlayerColor playerColor, TileState tileState,
      Set<TileState> visited, BuildingGroupScore group) {
    assert tileState.getStarTokenColor() == playerColor;
    int nbStars = tileState.getNbStars();
    assert nbStars > 0 && nbStars <= 3;
    int valueScore = nbStars * (nbStars + 1) / 2;
    assert !visited.contains(tileState);

    visited.add(tileState);
    group.starScore += nbStars;
    group.valueScore += valueScore;

    // Find valid neighbors.
    ArrayList<Vector2d> neighbors = Board.neighborsForLocation(tileState.getLocation());
    for (Vector2d neighbor : neighbors) {
      TileState neighborTileState = gameState.findTileAtLocation(neighbor);
      if (neighborTileState != null && neighborTileState.getStarTokenColor() == playerColor &&
          !visited.contains(neighborTileState)) {
        scoreGroup(gameState, playerColor, neighborTileState, visited, group);
      }
    }
  }

  /**
   * Keep tracks of the score, for a group of building, using either the number of stars or the
   * larger value.
   */
  private class BuildingGroupScore {
    int starScore;
    int valueScore;
  }
}
