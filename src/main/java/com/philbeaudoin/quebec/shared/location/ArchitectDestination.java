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

package com.philbeaudoin.quebec.shared.location;

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.game.state.GameState;

/**
 * A valid place to take or send an architect. The destination implies a color of architect.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface ArchitectDestination extends Location {

  /**
   * @return The color of the architect at that destination.
   */
  PlayerColor getArchitectColor();

  /**
   * Remove the architect from that specific destination in the provided game state.
   * @param gameState The game state from which to remove the architect.
   */
  void removeFrom(GameState gameState);

  /**
   * Add the architect to this specific destination in the provided game state.
   * @param gameState The game state onto which to add the architect.
   */
  void addTo(GameState gameState);

  /**
   * Accepts a visitor.
   * @param visitor The visitor.
   */
  <T> T accept(ArchitectDestinationVisitor<T> visitor);
}
