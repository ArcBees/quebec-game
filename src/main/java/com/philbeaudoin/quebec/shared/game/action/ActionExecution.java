/**
 * Copyright 2013 Philippe Beaudoin
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

import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.utils.Callback;

/**
 * The interface for classes that can execute a given action. Executing an action has various stages
 * since it can include playing an animation, sending the action to the server, etc. The action
 * returned by {@link #getAction()} should always be part of the ones returned by
 * {@link GameState#getPossibleActions()} of the game state returned by {@link #getGameState()}.
 */
public interface ActionExecution {

  /**
   * Called to execute the action and change the game state itself. This can only be called once
   * per {@link ActionExecution}.
   * @param gameState The game state before the execution.
   * @param gameAction The game action to execute.
   * @param gameStateChange The game state change resulting from applying the action to the state.
   */
  public void execute(GameState gameState, GameAction gameAction,
      GameStateChange gameStateChange);

  /**
   * Called to finalize the execution of the action. This can only be called once per
   * {@link ActionExecution}.
   * @param gameState The game state following the execution of the action.
   */
  public void finalizeExecution(GameState newGameState);

  /**
   * Called if an error occured at any stage during the action execution. This can only be called
   * once per {@link ActionExecution}.
   */
  public void error();

  /**
   * Sets a callback that must be called be called whenever this specific ActionExecution is ready
   * to be finalized. This must be called before prepareExecution.
   * @param callback The callback to invoke.
   */
  public void setReadyToFinalizeCallback(Callback callback);
}
