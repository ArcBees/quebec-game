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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Random;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.ScoringHelper;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.action.GameAction;
import com.philbeaudoin.quebec.shared.game.action.PossibleActions;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.game.state.LeaderCard;
import com.philbeaudoin.quebec.shared.game.state.TileState;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;

/**
 * The brain of an artificial intelligence that evaluates only his own move.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class AiBrainSimple implements AiBrain {

  private static final double LEVEL = 1.0;  // Level of the AI player. 1.0 for the best player.

  @Override
  public GameAction getMove(GameController gameController, GameState gameState) {
    PlayerColor playerColor = gameState.getCurrentPlayer().getColor();
    // TODO(beaudoin): AIs with a level < 1 play too much architect moves.
    double percentile = 1.0 - Random.nextDouble() * (0.1 * (1.0 - LEVEL));
    ScoreAndMove result = scoreForBestMove(gameController, gameState, playerColor, percentile);
    if (result == null) {
      return null;
    }
    return result.move;
  }

  @Override
  public String getSuffix() {
    return "AI";
  }

  /**
   * Calculate the score for the best possible move for the player of a given color. If the game
   * state does not mark that player as active, we return null. That is, we don't perform
   * any mini-max here.
   * @param gameController The game controller.
   * @param gameState The current game state.
   * @param playerState The player for which to find the best possible move.
   * @param percent The percentile of the move to take, 1.0 is going to take the best move.
   * @return The best possible move and its score, or null.
   */
  private ScoreAndMove scoreForBestMove(GameController gameController, GameState gameState,
      PlayerColor playerColor, double percent) {
    if (playerColor != gameState.getCurrentPlayer().getColor()) {
      return null;
    }

    PossibleActions possibleActions = gameState.getPossibleActions();
    if (possibleActions == null || possibleActions.getNbActions() == 0) {
      return null;
    }

    double bestScore = -1;
    ArrayList<ScoreAndMove> moves = new ArrayList<ScoreAndMove>(possibleActions.getNbActions());
    for (int actionIndex = 0; actionIndex < possibleActions.getNbActions(); ++actionIndex) {
      GameState gameStateCopy = new GameState(gameState);
      GameAction gameAction = possibleActions.getAction(actionIndex);
      GameStateChange gameStateChange = gameAction.execute(gameController, gameStateCopy);
      gameStateChange.apply(gameController, gameStateCopy);
      ScoreAndMove scoreAndMove = scoreForBestMove(gameController, gameStateCopy, playerColor, 1.0);
      double score = -10000;
      if (scoreAndMove == null) {
        score = evaluate(gameStateCopy, playerColor);
      } else {
        score = scoreAndMove.score;
      }
      if (score > bestScore) {
        bestScore = score;
        moves.add(new ScoreAndMove(score, gameAction));
      }
    }

    double targetScore = percent * bestScore;
    ScoreAndMove selectedMove = null;
    for (ScoreAndMove move : moves) {
      if (move.score >= targetScore && (selectedMove == null || move.score <= selectedMove.score)) {
        selectedMove = move;
      }
    }

    return selectedMove;
  }

  private double evaluate(GameState gameState, PlayerColor playerColor) {
    PlayerState playerState = gameState.getPlayerState(playerColor);
    addOrRemoveCubesToZonesFromTiles(gameState, true);
    double result = ScoringHelper.calculateZoneScore(gameState).getScore(playerColor);
    addOrRemoveCubesToZonesFromTiles(gameState, false);
    result += playerState.getScore();

    MoveCount movesUntilScoring = estimateMovesUntilScoring(gameState, playerColor);

    TileState[] modifiedTiles = new TileState[2];
    int nbModifiedTiles = addFakeStarTokens(gameState, playerColor, playerState, movesUntilScoring,
        modifiedTiles);
    result += ScoringHelper.computeBuildingsScoringInformation(gameState).getScore(playerColor);
    removeFakeStarTokens(modifiedTiles, nbModifiedTiles);

    result += ScoringHelper.computeActiveCubesScoringInformation(gameState).getScore(playerColor);

    result += calculateBonusForHoldingLeaders(gameState, playerState, movesUntilScoring);
    return result;
  }

  private double calculateBonusForHoldingLeaders(GameState gameState, PlayerState playerState,
      MoveCount movesUntilScoring) {
    LeaderCard leaderCard = playerState.getLeaderCard();
    if (leaderCard == null) {
      return 0;
    }
    // TODO(beaudoin): Many constants here, extract them out.
    switch (leaderCard) {
    case CITADEL:
      // Already counted by other means.
      return 0;
    case RELIGIOUS:
      // Can play on own tile, estimate each future move is worth 1.25 extra points.
      return movesUntilScoring.moves * 1.06 / (double) gameState.getNbPlayers();
    case POLITIC:
      // Can send cubes to any zone, cubes on tiles and active cubes are worth an extra 0.4 points
      // provided there are enough architect moves left.
      double scale = movesUntilScoring.architectMoves / 10.0;
      int cubesOnTiles = calculateCubesOnTiles(gameState, playerState.getColor());
      return (cubesOnTiles * 1.0 + playerState.getNbActiveCubes() * 0.25) * scale;
    case ECONOMIC:
      // Can grab more tiles, each architect move is worth extra point, it's worth more in
      // play than in hand.
      return movesUntilScoring.architectMoves *
          (playerState.isHoldingNeutralArchitect() ? 0.2 : 0.6);
    case CULTURAL_TWO_THREE:
    case CULTURAL_FOUR_FIVE:
      // Score with each move, each own architect move is worth 2 extra point.
      int nbPlayers = gameState.getNbPlayers();
      return movesUntilScoring.architectMoves *
          (LeaderCard.getPointsForCultural(leaderCard, 1) + 0.5) / nbPlayers;
    default:
      return 0;
    }
  }

  private int calculateCubesOnTiles(GameState gameState, PlayerColor playerColor) {
    int result = 0;
    for (TileState tileState : gameState.getTileStates()) {
      if (tileState.getArchitect().isArchitectColor()) {
        for (int spot = 0; spot < 3; ++spot) {
          if (tileState.getColorInSpot(spot) == playerColor) {
            result += tileState.getCubesPerSpot();
          }
        }
      }
    }
    return result;
  }

  private int addFakeStarTokens(GameState gameState, PlayerColor playerColor,
      PlayerState playerState, MoveCount movesUntilScoring, TileState[] modifiedTiles) {
    int nbExtraFilledSpots = (playerState.getNbActiveCubes() + 1) / 2;
    if (playerState.getLeaderCard() == null) {
      // No cards, can hold on a bit more.
      nbExtraFilledSpots++;
    }
    int nbExtraFilledSpotsNeutralArchitect = 0;
    if (playerState.getLeaderCard() == LeaderCard.ECONOMIC) {
      // The extra pawn makes it possible to wait until the end of the round.
      nbExtraFilledSpots = 3;
      // For the neutral architect it's slightly different.
      nbExtraFilledSpotsNeutralArchitect = Math.min(3,
          movesUntilScoring.moves / gameState.getNbPlayers());
    }
    int nbModifiedTiles = 0;
    for (TileState tileState : gameState.getTileStates()) {
      PlayerColor architect = tileState.getArchitect();
      if (playerState.ownsArchitect(architect)) {
        modifiedTiles[nbModifiedTiles++] = tileState;
        int nbFilledSpots;
        if (architect == PlayerColor.NEUTRAL) {
          nbFilledSpots = Math.min(3, tileState.countNbFilledSpots() +
              nbExtraFilledSpotsNeutralArchitect);
        } else {
          nbFilledSpots = Math.min(3, tileState.countNbFilledSpots() + nbExtraFilledSpots);
        }
        if (nbFilledSpots > 0) {
          // TODO(beaudoin): Slightly hacky: we leave the architect on and set a star token. It
          //     work because this is supported by computeBuildingsScoringInformation and it makes
          //     it easy to revert the change later, but we may want to clean this up.
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

  private MoveCount estimateMovesUntilScoring(GameState gameState, PlayerColor playerColor) {
    List<PlayerState> playerStates = gameState.getPlayerStates();
    int nbPlayers = playerStates.size();
    double nbActives[] = new double[nbPlayers];
    double nbPassives[] = new double[nbPlayers];
    int nbUnusedTiles = 0;
    int century = gameState.getCentury();
    for (TileState tileState : gameState.getTileStates()) {
      if (tileState.isAvailableForArchitect(century)) {
        nbUnusedTiles++;
      }
    }

    int scoringPlayerIndex = 0;
    for (int i = 0; i < nbPlayers; ++i) {
      PlayerState playerState = playerStates.get(i);
      nbActives[i] = playerState.getNbActiveCubes();
      nbPassives[i] = playerState.getNbPassiveCubes();
      if (playerState.getColor() == playerColor) {
        scoringPlayerIndex = i;
      }
    }

    MoveCount result = new MoveCount();
    int i = (scoringPlayerIndex + 1) % nbPlayers;
    while (true) {
      result.moves++;
      // TODO(beaudoin): The following should work here, but it doesn't.
      // if (playerState.getPlayer().getColor() == playerColor) {
      //   result.playerMoves++;
      // }
      if (nbActives[i] == 0 && nbPassives[i] == 0) {
        break;
      }
      if (nbActives[i] <= 1) {
        // Move architect.
        result.architectMoves++;
        if (nbUnusedTiles == 0) {
          break;
        }
        nbUnusedTiles--;
        // Activate 3 cubes.
        nbActives[i] += Math.min(3, nbPassives[i]);
        nbPassives[i] -= Math.min(3, nbPassives[i]);
      } else {
        // Send 2 cubes from active and 1.25 cubes from passive.
        nbActives[i] -= Math.min(2, nbActives[i]);
        nbPassives[i] -= Math.min(1.25, nbPassives[i]);
      }
    }
    return result;
  }

  private static class ScoreAndMove {
    final double score;
    final GameAction move;
    public ScoreAndMove(double score, GameAction move) {
      this.score = score;
      this.move = move;
    }
  }

  private static class MoveCount {
    int moves;
    int architectMoves;
  }
}
