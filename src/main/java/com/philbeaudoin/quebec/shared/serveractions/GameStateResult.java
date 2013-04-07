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

package com.philbeaudoin.quebec.shared.serveractions;

import com.gwtplatform.dispatch.shared.Result;
import com.philbeaudoin.quebec.shared.state.GameState;

/**
 * Returns the complete state of a given game.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameStateResult implements Result {

  private GameState gameState;

  public GameStateResult(final GameState gameState) {
    this.gameState = gameState;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private GameStateResult() {
  }

  public GameState getGameState() {
    return gameState;
  }
}
