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

package com.philbeaudoin.quebec.shared.action;

import java.util.ArrayList;

import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;

/**
 * A set of possible actions that can be taken in the current state of the game.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class PossibleActions {

  private final ArrayList<GameAction> gameActions = new ArrayList<GameAction>();

  /**
   * The number of actions that can be taken.
   * @return The number of actions.
   */
  public int getNbActions() {
    return gameActions.size();
  }

  /**
   * Apply a given action to a given game state and return the game state change resulting from it.
   * The game state itself is not modified.
   * @param actionIndex The index of the action to execute, must be lower than the value returned by
   *     {@link #getNbActions()}.
   * @param gameState The state of the game to which to apply the action, it is not modified.
   * @return The change to the game state resulting from the application of that action.
   */
  public GameStateChange execute(int actionIndex, GameState gameState) {
    assert actionIndex > 0 && actionIndex < gameActions.size();
    return gameActions.get(actionIndex).execute(gameState);
  }

  /**
   * Accepts a visitor.
   * @param visitor The visitor.
   */
  public void accept(GameActionVisitor visitor) {
    for (GameAction gameAction : gameActions) {
      gameAction.accept(visitor);
    }
  }

  /**
   * Adds an action to this list of possible actions.
   * @param gameAction The action to add.
   */
  public void add(GameAction gameAction) {
    gameActions.add(gameAction);
  }
}
