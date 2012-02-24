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

package com.philbeaudoin.quebec.shared;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.philbeaudoin.quebec.shared.player.PlayerState;
import com.philbeaudoin.quebec.shared.state.Board;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.TileState;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationInfluenceZone;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeScorePoints;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * A utility class to perform scoring.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ScoringHelper {

  /**
   * Computes the total score for all players on the zones.
   * @param gameState The current game state. Will be modified!
   * @return The total score of each player on zones. The cascade is for the last scored zone.
   */
  public static ZoneScoringInformation calculateZoneScore(GameState gameState) {
    ZoneScoringInformation lastInfo = null;
    for (ScoringPhase scoringPhase : ScoringPhase.ZONE_SCORING_PHASES) {
      if (lastInfo != null) {
        applyCascade(gameState, lastInfo);
      }
      ZoneScoringInformation info = computeZoneScoringInformation(scoringPhase, gameState);
      if (lastInfo != null) {
        unapplyCascade(gameState, lastInfo);
        // Add up the scores.
        for (PlayerColor playerColor : PlayerColor.NORMAL) {
          info.setScore(playerColor, info.getScore(playerColor) + lastInfo.getScore(playerColor));
        }
      }
      lastInfo = info;
    }
    return lastInfo;
  }

  private static void applyCascade(GameState gameState, ZoneScoringInformation lastInfo) {
    applyOrUnnaplyCascade(gameState, lastInfo, true);
  }

  private static void unapplyCascade(GameState gameState, ZoneScoringInformation lastInfo) {
    applyOrUnnaplyCascade(gameState, lastInfo, false);
  }

  private static void applyOrUnnaplyCascade(GameState gameState, ZoneScoringInformation lastInfo,
      boolean apply) {
    for (PlayerColor playerColor : PlayerColor.NORMAL) {
      int cubesToCascade = lastInfo.getCubesToCascade(playerColor);
      if (cubesToCascade > 0) {
        if (apply) {
          lastInfo.getDestination(playerColor).addTo(cubesToCascade, gameState);
        } else {
          lastInfo.getDestination(playerColor).removeFrom(cubesToCascade, gameState);
        }
      }
    }
  }

  /**
   * Adds all the game state change corresponding to points scored when scoring a given zone.
   * @param scoringPhase The scoring phase, must be a valid zone scoring phase.
   * @param gameState The current game state.
   * @param result The composite to which to add scoring game state changes.
   */
  public static void performZoneScoring(ScoringPhase scoringPhase,
      GameState gameState, GameStateChangeComposite result) {
    assert scoringPhase.isZoneScoringPhase();
    ZoneScoringInformation scoringInfo = computeZoneScoringInformation(scoringPhase, gameState);
    for (PlayerColor playerColor : PlayerColor.NORMAL) {
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

  /**
   * Computes the scoring information for that action given a game state, provided it's in a phase
   * where an influence zone is being scored.
   * @param scoringPhase The current scoring phase.
   * @param gameState The game state.
   * @return The scoring information, or null if there is no scoring information for this phase.
   */
  public static ZoneScoringInformation computeZoneScoringInformation(ScoringPhase scoringPhase,
      GameState gameState) {
    int century = gameState.getCentury();
    int scoringZoneIndex = scoringPhase.scoringZoneIndex();
    InfluenceType zoneToScore = InfluenceType.getScoringZoneForCentury(century, scoringZoneIndex);

    ZoneScoringInformation result = new ZoneScoringInformation(scoringZoneIndex, zoneToScore);
    int nbCubesOfLeaders = 0;
    for (PlayerColor playerColor : PlayerColor.NORMAL) {
      int nbCubes = gameState.getPlayerCubesInInfluenceZone(zoneToScore, playerColor);
      result.setScore(playerColor, nbCubes);
      if (nbCubes > nbCubesOfLeaders) {
        nbCubesOfLeaders = nbCubes;
      }
    }

    // Cascade if needed.
    if (nbCubesOfLeaders > 0) {
      int cubesToCascade = Math.min(5, nbCubesOfLeaders / 2);
      for (PlayerColor playerColor : PlayerColor.NORMAL) {
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
   * Computes the scoring information for that action given a game state.
   * @param gameState The game state.
   * @return The scoring information.
   */
  public static ScoringInformation computeIncompleteBuildingScoringInformation(
      GameState gameState) {
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
   * Computes the scoring information for that action given a game state.
   * @param gameState The game state.
   * @return The scoring information.
   */
  public static ScoringInformation computeActiveCubesScoringInformation(GameState gameState) {
    ScoringInformation scoringInformation = new ScoringInformation();
    for (PlayerState playerState : gameState.getPlayerStates()) {
      PlayerColor playerColor = playerState.getPlayer().getColor();
      scoringInformation.addToScore(playerColor, playerState.getNbActiveCubes() / 2);
    }
    return scoringInformation;
  }

  /**
   * Computes the scoring information for that action given a game state.
   * @param gameState The game state.
   * @return The scoring information, or null if there is no scoring information for this phase.
   */
  public static ScoringInformation computeBuildingsScoringInformation(GameState gameState) {
    ScoringInformation result = new ScoringInformation();
    for (PlayerColor playerColor : PlayerColor.NORMAL) {
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

  private static void scoreGroup(GameState gameState, PlayerColor playerColor, TileState tileState,
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
  private static class BuildingGroupScore {
    int starScore;
    int valueScore;
  }
}
