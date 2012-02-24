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

package com.philbeaudoin.quebec.shared.player;

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.ScoringHelper;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.LeaderCard;
import com.philbeaudoin.quebec.shared.state.TileState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;

/**
 * The brain of an artificial intelligence that evaluates only his own move.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class AiBrainSimple implements AiBrain {

  @Override
  public GameStateChange getMove(GameState gameState) {
    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    ScoreAndMove result = scoreForBestMove(gameState, playerColor);
    if (result == null) {
      return null;
    }
    return result.move;
  }

  /**
   * Calculate the score for the best possible move for the player of a given color. If the game
   * state does not mark that player as active, we return null. That is, we don't perform
   * any mini-max here.
   * @param gameState The current game state.
   * @param playerState The player for which to find the best possible move.
   * @return The best possible move and its score, or null.
   */
  private ScoreAndMove scoreForBestMove(GameState gameState, PlayerColor playerColor) {
    if (playerColor != gameState.getCurrentPlayer().getPlayer().getColor()) {
      return null;
    }

    PossibleActions possibleActions = gameState.getPossibleActions();
    if (possibleActions == null || possibleActions.getNbActions() == 0) {
      return null;
    }

    double bestScore = -1;
    GameStateChange bestMove = null;
    for (int actionIndex = 0; actionIndex < possibleActions.getNbActions(); ++actionIndex) {
      GameState gameStateCopy = new GameState(gameState);
      GameStateChange gameStateChange = possibleActions.execute(actionIndex, gameStateCopy);
      gameStateChange.apply(gameStateCopy);
      ScoreAndMove scoreAndMove = scoreForBestMove(gameStateCopy, playerColor);
      double score = -10000;
      if (scoreAndMove == null) {
        score = evaluate(gameStateCopy, playerColor);
      } else {
        score = scoreAndMove.score;
      }
      if (score > bestScore) {
        bestScore = score;
        bestMove = gameStateChange;
      }
    }
    return new ScoreAndMove(bestScore, bestMove);
  }

  private double evaluate(GameState gameState, PlayerColor playerColor) {
    PlayerState playerState = gameState.getPlayerState(playerColor);
    addOrRemoveCubesToZonesFromTiles(gameState, true);
    int result = ScoringHelper.calculateZoneScore(gameState).getScore(playerColor);
    addOrRemoveCubesToZonesFromTiles(gameState, false);
    result += playerState.getScore();

    TileState[] modifiedTiles = new TileState[2];
    int nbModifiedTiles = addFakeStarTokens(gameState, playerColor, playerState, modifiedTiles);
    result += ScoringHelper.computeBuildingsScoringInformation(gameState).getScore(playerColor);
    removeFakeStarTokens(modifiedTiles, nbModifiedTiles);

    result += ScoringHelper.computeActiveCubesScoringInformation(gameState).getScore(playerColor);
    return result;
  }

  private int addFakeStarTokens(GameState gameState, PlayerColor playerColor,
      PlayerState playerState, TileState[] modifiedTiles) {
    // TODO Special-case for end-of-game where this is not a good evaluation.
    int nbExtraFilledSpots = (playerState.getNbActiveCubes() + 1) / 2;
    if (playerState.getLeaderCard() == LeaderCard.ECONOMIC) {
      // The extra pawn makes it possible to wait until the end of the round.
      // TODO(beaudoin): Take the number of moves until the end in consideration.
      nbExtraFilledSpots = 3;
    }
    int nbModifiedTiles = 0;
    for (TileState tileState : gameState.getTileStates()) {
      if (playerState.ownsArchitect(tileState.getArchitect())) {
        modifiedTiles[nbModifiedTiles++] = tileState;
        int nbFilledSpots = Math.min(3, tileState.countNbFilledSpots() + nbExtraFilledSpots);
        if (nbFilledSpots > 0) {
          // TODO(beaudoin): Slightly hacky: we leave the architect on and set a star token. It works
          //     because this is supported by computeBuildingsScoringInformation and it makes it
          //     easy to revert the change later, but we may want to clean this up.
          tileState.setStarToken(playerColor, nbFilledSpots);
        }
        if (nbModifiedTiles == 2) {
          break;
        }
      }
    }
    return nbModifiedTiles;
  }

  private void removeFakeStarTokens(TileState[] modifiedTiles,
      int nbModifiedTiles) {
    for (int i = 0; i < nbModifiedTiles; ++i) {
      modifiedTiles[i].setStarToken(PlayerColor.NONE, 0);
    }
  }

  private void addOrRemoveCubesToZonesFromTiles(GameState gameState, boolean add) {
    int multiplier = add ? 1 : -1;
    for (TileState tileState : gameState.getTileStates()) {
      if (tileState.getArchitect().isArchitectColor()) {
        InfluenceType influenceType = tileState.getTile().getInfluenceType();
        int nbCubes = tileState.getCubesPerSpot();
        for (int spot = 0; spot < 3; ++spot) {
          PlayerColor cubesColor = tileState.getColorInSpot(spot);
          if (cubesColor.isNormalColor()) {
            gameState.setPlayerCubesInInfluenceZone(influenceType, cubesColor,
                gameState.getPlayerCubesInInfluenceZone(influenceType, cubesColor) +
                nbCubes * multiplier);
          }
        }
      }
    }
  }

  private static class ScoreAndMove {
    final double score;
    final GameStateChange move;
    public ScoreAndMove(double score, GameStateChange move) {
      this.score = score;
      this.move = move;
    }
  }
}
