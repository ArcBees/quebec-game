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
 * A valid place to take or send cubes. The destination implies a color of cubes.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface CubeDestination extends Location {

  /**
   * @return The number of cubes available in that destination.
   * @param gameState The game state into which to check.
   */
  int getNbCubes(GameState gameState);

  /**
   * @return The color of the player associated with that destination.
   */
  PlayerColor getPlayerColor();

  /**
   * Remove cubes from that specific destination in the provided game state.
   * @param nbCubes The number of cubes to add.
   * @param gameState The game state from which to remove cubes.
   */
  void removeFrom(int nbCubes, GameState gameState);

  /**
   * Add cubes to this specific destination in the provided game state.
   * @param nbCubes The number of cubes to add.
   * @param gameState The game state onto which to add cubes.
   */
  void addTo(int nbCubes, GameState gameState);

  /**
   * Accepts a visitor.
   * @param visitor The visitor.
   */
  <T> T accept(CubeDestinationVisitor<T> visitor);
}
