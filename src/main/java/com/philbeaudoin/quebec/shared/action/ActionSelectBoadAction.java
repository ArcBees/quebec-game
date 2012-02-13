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

import com.philbeaudoin.quebec.shared.state.BoardAction;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangePrepareAction;

/**
 * The action of selecting a board action to execute.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ActionSelectBoadAction implements PossibleActions, GameActionOnBoardAction {

  private final BoardAction boardAction;

  public ActionSelectBoadAction(BoardAction boardAction) {
    this.boardAction = boardAction;
  }

  @Override
  public int getNbActions() {
    return 1;
  }

  @Override
  public GameStateChange execute(int actionIndex, GameState gameState) {
    assert actionIndex == 0;
    return execute(gameState);
  }

  @Override
  public GameStateChange execute(GameState gameState) {
    return new GameStateChangePrepareAction(boardAction);
  }

  @Override
  public void accept(PossibleActionsVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public BoardAction getBoardAction() {
    return boardAction;
  }
}
