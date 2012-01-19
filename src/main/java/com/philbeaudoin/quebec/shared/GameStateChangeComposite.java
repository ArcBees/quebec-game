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

package com.philbeaudoin.quebec.shared;

import java.util.ArrayList;

/**
 * A change of game state obtained by combining multiple different game state changes.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameStateChangeComposite implements GameStateChange {
  private final ArrayList<GameStateChange> changes = new ArrayList<GameStateChange>();

  @Override
  public GameState apply(GameState gameState) {
    GameState result = gameState;
    for (GameStateChange change : changes) {
      result = change.apply(result);
    }
    return result;
  }

  public void callOnEach(AcceptGameStateChange acceptGameStateChange) {
    for (GameStateChange change : changes) {
      acceptGameStateChange.execute(change);
    }
  }

  @Override
  public void accept(GameStateChangeVisitor visitor) {
    visitor.visit(this);
  }

  /**
   * Adds a change to this composite.
   * @param change The change to add.
   */
  public void add(GameStateChangeMoveCubes change) {
    changes.add(change);
  }
}
