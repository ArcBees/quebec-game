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

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.state.GameController;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;

/**
 * A artificial intelligence player playing locally.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class PlayerLocalAi extends PlayerBase {

  private AiBrain aiBrain;

  public PlayerLocalAi(PlayerColor color, String name, AiBrain aiBrain) {
    super(color, name + " " + aiBrain.getSuffix());
    this.aiBrain = aiBrain;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private PlayerLocalAi() {
  }

  @Override
  public <T> T accept(PlayerVisitor<T> visitor) {
    return visitor.visit(this);
  }

  /**
   * Get the move to execute given a game state. This player must be the current active player in
   * the provided game state.
   * @param gameController The game controller.
   * @param gameState The game state.
   * @return The move to execute or null if there are no moves available in that state.
   */
  public GameStateChange getMove(GameController gameController, GameState gameState) {
    assert gameState.getCurrentPlayer().getColor() == getColor();
    return aiBrain.getMove(gameController, gameState);
  }
}
