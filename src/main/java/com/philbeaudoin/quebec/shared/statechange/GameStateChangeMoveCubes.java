/**
 * Copyright 2011 Philippe Beaudoin
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
 * A change of the game state obtained by moving cubes from one location to another.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameStateChangeMoveCubes implements GameStateChange {

  private final int nbCubes;
  private final CubeDestination from;
  private final CubeDestination to;

  public GameStateChangeMoveCubes(int nbCubes, CubeDestination from, CubeDestination to) {
    assert from.getPlayerColor() == to.getPlayerColor();
    this.nbCubes = nbCubes;
    this.from = from;
    this.to = to;
  }

  @Override
  public void apply(GameState gameState) {
    assert from.getNbCubes(gameState) >= nbCubes;
    from.removeFrom(nbCubes, gameState);
    to.addTo(nbCubes, gameState);
  }

  @Override
  public void accept(GameStateChangeVisitor visitor) {
    visitor.visit(this);
  }

  /**
   * Access the number of cubes being moved.
   * @return The number of cubes being moved.
   */
  public int getNbCubes() {
    return nbCubes;
  }

  /**
   * Access the cube destination from which the cubes start.
   * @return The origin cube destination.
   */
  public CubeDestination getFrom() {
    return from;
  }

  /**
   * Access the cube destination to which the cubes go.
   * @return The final cube destination.
   */
  public CubeDestination getTo() {
    return to;
  }
}
