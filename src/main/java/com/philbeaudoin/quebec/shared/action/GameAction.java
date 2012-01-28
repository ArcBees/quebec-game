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

import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;

/**
 * A possible action in the game.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface GameAction {
  /**
   * Apply the action to a given game state and return the game state change resulting from it.
   * The game state itself is not modified.
   * @param gameState The state of the game to which to apply the action, it will not be modified.
   * @return The change to the game state resulting from the application of that action.
   */
  GameStateChange execute(GameState gameState);
}
