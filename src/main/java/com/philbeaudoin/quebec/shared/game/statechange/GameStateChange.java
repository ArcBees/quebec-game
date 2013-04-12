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

package com.philbeaudoin.quebec.shared.game.statechange;

import java.io.Serializable;

import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.state.GameState;

/**
 * A class can can track a single change of the game state.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface GameStateChange extends Serializable {
  /**
   * Apply this change to the specified game change.
   * @param gameController The game controller.
   * @param gameState The game state to apply this change to.
   */
  void apply(GameController gameController, GameState gameState);

  /**
   * Accepts a visitor.
   * @param visitor The visitor.
   */
  <T> T accept(GameStateChangeVisitor<T> visitor);
}
