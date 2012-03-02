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

import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;

/**
 * The brain of an artificial intelligence.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface AiBrain {
  /**
   * Returns the move to make, for the current player, given a game state.
   * @param gameState The game state.
   * @return The move to make.
   */
  GameStateChange getMove(GameState gameState);

  /**
   * Gets a short name that can be used to identify that type of brain.
   * @return The brain name, like "AI" or "AI 2".
   */
  String getSuffix();
}
