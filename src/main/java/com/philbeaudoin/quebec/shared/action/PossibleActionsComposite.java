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
public class PossibleActionsComposite implements PossibleActions {

  private final ArrayList<PossibleActions> possibleActionsList = new ArrayList<PossibleActions>();

  @Override
  public int getNbActions() {
    int total = 0;
    for (PossibleActions possibleActions : possibleActionsList) {
      total += possibleActions.getNbActions();
    }
    return total;
  }

  @Override
  public GameStateChange execute(int actionIndex, GameState gameState) {
    int total = 0;
    for (PossibleActions possibleActions : possibleActionsList) {
      int nbActions = possibleActions.getNbActions();
      if (total + nbActions > actionIndex) {
        return possibleActions.execute(actionIndex - total, gameState);
      }
      total += nbActions;
    }
    assert false;
    return null;
  }

  @Override
  public void accept(PossibleActionsVisitor visitor) {
    visitor.visit(this);
  }

  /**
   * Call a given functor on all the elements of the composite.
   * @param functor The functor to call.
   */
  public void callOnEach(AcceptPossibleActions functor) {
    for (PossibleActions possibleActions : possibleActionsList) {
      functor.execute(possibleActions);
    }
  }

  /**
   * Adds a possible actions to this composite.
   * @param possibleActions The possible actions to add.
   */
  public void add(PossibleActions possibleActions) {
    possibleActionsList.add(possibleActions);
  }
}
