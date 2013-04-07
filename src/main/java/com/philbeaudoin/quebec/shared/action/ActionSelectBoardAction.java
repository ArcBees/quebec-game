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
import com.philbeaudoin.quebec.shared.state.GameController;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangePrepareAction;

/**
 * The action of selecting a board action to execute.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ActionSelectBoardAction implements GameActionOnBoardAction {

  private BoardAction boardAction;
  private Tile triggeringTile;

  public ActionSelectBoardAction(BoardAction boardAction, Tile triggeringTile) {
    this.boardAction = boardAction;
    this.triggeringTile = triggeringTile;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private ActionSelectBoardAction() {
  }

  @Override
  public GameStateChange execute(GameController gameController, GameState gameState) {
    return new GameStateChangePrepareAction(boardAction, triggeringTile);
  }

  @Override
  public boolean isAutomatic() {
    return false;
  }

  @Override
  public void accept(GameActionVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public BoardAction getBoardAction() {
    return boardAction;
  }
}
