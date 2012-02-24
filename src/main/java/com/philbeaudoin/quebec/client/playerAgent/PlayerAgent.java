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

package com.philbeaudoin.quebec.client.playerAgent;

import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.shared.state.GameState;

/**
 * The agent of a {@link com.philbeaudoin.quebec.shared.player.Player Player} class, can be used to
 * represent client-side aspects of a player.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface PlayerAgent {
  /**
   * Render the interactions that this player agent needs to setup given the current game state.
   * This method can trigger a move right away if desired.
   * @param gameState The game state.
   * @param gameStateRenderer The game state renderer into which to render the interactions.
   */
  void renderInteractions(GameState gameState, GameStateRenderer gameStateRenderer);
}
