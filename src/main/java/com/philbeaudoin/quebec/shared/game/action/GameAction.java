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

import java.io.Serializable;

import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;

/**
 * A possible action in the game.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface GameAction extends Serializable {
  /**
   * Apply the action to a given game state and return the game state change resulting from it.
   * The game state itself is not modified.
   * @param gameController The game controller to use.
   * @param gameState The state of the game to which to apply the action, it will not be modified.
   * @return The change to the game state resulting from the application of that action.
   */
  GameStateChange execute(GameController gameController, GameState gameState);

  /**
   * Return true if the action should be executed automatically, without user intervention. If an
   * action is automatic then it should be the only action in a group.
   * @return True if the action is automatic, false otherwise.
   */
  boolean isAutomatic();

  /**
   * Accepts a visitor.
   * @param visitor The visitor to accept.
   */
  void accept(GameActionVisitor visitor);
}
