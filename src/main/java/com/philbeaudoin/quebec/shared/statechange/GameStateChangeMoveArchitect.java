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

import com.philbeaudoin.quebec.shared.location.ArchitectDestination;
import com.philbeaudoin.quebec.shared.state.GameController;
import com.philbeaudoin.quebec.shared.state.GameState;

/**
 * A change of the game state obtained by moving an architect from one location to another.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameStateChangeMoveArchitect implements GameStateChange {

  private ArchitectDestination from;
  private ArchitectDestination to;

  public GameStateChangeMoveArchitect(ArchitectDestination from, ArchitectDestination to) {
    assert from.getArchitectColor() == to.getArchitectColor();
    this.from = from;
    this.to = to;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private GameStateChangeMoveArchitect() {
  }

  @Override
  public void apply(GameController gameController, GameState gameState) {
    from.removeFrom(gameState);
    to.addTo(gameState);
  }

  @Override
  public <T> T accept(GameStateChangeVisitor<T> visitor) {
    return visitor.visit(this);
  }

  /**
   * Access the architect destination from which the architect start.
   * @return The origin cube destination.
   */
  public ArchitectDestination getFrom() {
    return from;
  }

  /**
   * Access the architect destination to which the architect go.
   * @return The final cube destination.
   */
  public ArchitectDestination getTo() {
    return to;
  }
}
