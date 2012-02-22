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

import com.philbeaudoin.quebec.shared.state.GameState;

/**
 * A change of the game state obtained by switching to the next player.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameStateChangeNextPlayer implements GameStateChange {

  private final boolean prepareActions;

  /**
   * Switch to the next player and prepare the possible actions.
   */
  public GameStateChangeNextPlayer() {
    this(true);
  }

  /**
   * Switch to the next player and possibly prepare the possible actions.
   * @param prepareActions True to prepare the actions, false if the possible actions are
   *     prepared manually.
   */
  public GameStateChangeNextPlayer(boolean prepareActions) {
    this.prepareActions = prepareActions;
  }

  @Override
  public void apply(GameState gameState) {
    gameState.nextPlayer(prepareActions);
  }

  @Override
  public <T> T accept(GameStateChangeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
