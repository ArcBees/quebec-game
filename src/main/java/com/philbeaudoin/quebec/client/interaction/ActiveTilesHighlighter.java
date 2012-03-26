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

import java.util.ArrayList;

import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.state.TileState;

/**
 * Highlights all the active tiles in a game state.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ActiveTilesHighlighter implements Highlighter {

  private final GameStateRenderer gameStateRenderer;
  private final ArrayList<Tile> tiles = new ArrayList<Tile>();

  public ActiveTilesHighlighter(GameStateRenderer gameStateRenderer, GameState gameState) {
    this.gameStateRenderer = gameStateRenderer;
    int century = gameState.getCentury();
    for (TileState tileState : gameState.getTileStates()) {
      if (tileState.isAvailableForArchitect(century)) {
        tiles.add(tileState.getTile());
      }
    }
  }

  @Override
  public void highlight() {
    for (Tile tile : tiles) {
      gameStateRenderer.highlightTile(tile);
    }
  }
}
