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

import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.state.BoardAction;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.Tile;

/**
 * A change of the game state that consists of preparing the possible moves for a given board
 * action.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameStateChangePrepareAction implements GameStateChange {

  private final BoardAction boardAction;
  private final Tile triggeringTile;

  public GameStateChangePrepareAction(BoardAction boardAction, Tile triggeringTile) {
    this.boardAction = boardAction;
    this.triggeringTile = triggeringTile;
  }
  @Override
  public void apply(GameState gameState) {
    PossibleActions possibleActions = boardAction.getPossibleActions(gameState, triggeringTile);
    if (possibleActions != null && possibleActions.getNbActions() > 0) {
      gameState.setPossibleActions(possibleActions);
    } else {
      gameState.nextPlayer(true);
    }
  }

  @Override
  public <T> T accept(GameStateChangeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}