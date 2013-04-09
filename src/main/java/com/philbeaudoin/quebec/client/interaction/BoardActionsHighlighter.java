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

package com.philbeaudoin.quebec.client.interaction;

import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.shared.game.state.Board;
import com.philbeaudoin.quebec.shared.game.state.BoardAction;

/**
 * Highlights all the board actions.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardActionsHighlighter implements Highlighter {

  private final GameStateRenderer gameStateRenderer;

  public BoardActionsHighlighter(GameStateRenderer gameStateRenderer) {
    this.gameStateRenderer = gameStateRenderer;
  }

  @Override
  public void highlight() {
    for (BoardAction boardAction : Board.getAllActions()) {
      gameStateRenderer.highlightBoardAction(boardAction);
    }
  }
}
