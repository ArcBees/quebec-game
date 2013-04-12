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

import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.state.GameState;

/**
 * A decorator that can mark any {@Link GameStateChange} as instantaneous, meaning that they
 * should not generate an animation.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class GameStateChangeInstantaneousDecorator implements GameStateChange {

  private GameStateChange decorated;

  public GameStateChangeInstantaneousDecorator(GameStateChange decorated) {
    this.decorated = decorated;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private GameStateChangeInstantaneousDecorator() {
  }

  /**
   * Access the decorated {@link GameStateChange}.
   * @return The decorated game state change.
   */
  public GameStateChange getDecorated() {
    return decorated;
  }

  @Override
  public void apply(GameController gameController, GameState gameState) {
    decorated.apply(gameController, gameState);
  }

  @Override
  public <T> T accept(GameStateChangeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
