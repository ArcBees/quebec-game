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
import com.philbeaudoin.quebec.shared.game.action.PossibleActions;
import com.philbeaudoin.quebec.shared.game.state.GameState;

/**
 * A change of the game state that consists of preparing the possible moves for a given board
 * action.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameStateChangeQueuePossibleActions implements GameStateChange {

  private PossibleActions possibleActions;

  public GameStateChangeQueuePossibleActions(PossibleActions possibleActions) {
    assert possibleActions != null;
    this.possibleActions = possibleActions;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private GameStateChangeQueuePossibleActions() {
  }

  @Override
  public void apply(GameController gameController, GameState gameState) {
    gameState.setPossibleActions(possibleActions);
  }

  @Override
  public <T> T accept(GameStateChangeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}