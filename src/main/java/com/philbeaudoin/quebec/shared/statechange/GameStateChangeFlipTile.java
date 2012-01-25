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

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.state.TileState;

/**
 * A change of the game state obtained by flipping a tile an placing a star token on it.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameStateChangeFlipTile implements GameStateChange {

  private final Tile tile;
  private final PlayerColor playerColor;
  private final int nbStars;

  public GameStateChangeFlipTile(Tile tile, PlayerColor playerColor, int nbStars) {
    this.tile = tile;
    this.playerColor = playerColor;
    this.nbStars = nbStars;
  }

  @Override
  public void apply(GameState gameState) {
    TileState tileState = gameState.getTileState(tile);
    tileState.setBuildingFacing(true);
    tileState.setStarToken(playerColor, nbStars);
  }

  @Override
  public void accept(GameStateChangeVisitor visitor) {
    visitor.visit(this);
  }
}
