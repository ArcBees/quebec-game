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

package com.philbeaudoin.quebec.shared.statechange;

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.PlayerState;

/**
 * A change of the game state where a given player score points.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameStateChangeScorePoints implements GameStateChange {

  private final PlayerColor scoringPlayer;
  private final int nbPoints;

  public GameStateChangeScorePoints(PlayerColor scoringPlayer, int nbPoints) {
    assert scoringPlayer.isNormalColor();
    this.scoringPlayer = scoringPlayer;
    this.nbPoints = nbPoints;
  }

  @Override
  public void apply(GameState gameState) {
    PlayerState playerState = gameState.getPlayerState(scoringPlayer);
    playerState.setScore(playerState.getScore() + nbPoints);
  }

  @Override
  public void accept(GameStateChangeVisitor visitor) {
    visitor.visit(this);
  }

  /**
   * Access the color of the player scoring points.
   * @return The color of the player.
   */
  public PlayerColor getScoringPlayer() {
    return scoringPlayer;
  }

  /**
   * Access the number of points scored.
   * @return The number of points scored.
   */
  public int getNbPoints() {
    return nbPoints;
  }
}
