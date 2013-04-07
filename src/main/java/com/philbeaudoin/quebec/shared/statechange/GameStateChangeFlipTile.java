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

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.state.GameController;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.state.TileState;

/**
 * A change of the game state obtained by flipping a tile an placing a star token on it.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameStateChangeFlipTile implements GameStateChange {

  private Tile tile;
  private PlayerColor starTokenColor;
  private int nbStars;

  public GameStateChangeFlipTile(Tile tile, PlayerColor starTokenColor, int nbStars) {
    this.tile = tile;
    this.starTokenColor = starTokenColor;
    this.nbStars = nbStars;
    assert starTokenColor.isNormalColor() || nbStars == 0;
    assert nbStars != 0 || starTokenColor == PlayerColor.NONE;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private GameStateChangeFlipTile() {
  }

  @Override
  public void apply(GameController gameController, GameState gameState) {
    TileState tileState = gameState.findTileState(tile);
    tileState.setBuildingFacing(true);
    tileState.setStarToken(this.starTokenColor, nbStars);
  }

  @Override
  public <T> T accept(GameStateChangeVisitor<T> visitor) {
    return visitor.visit(this);
  }

  /**
   * Access the tile to flip.
   * @return The tile to flip.
   */
  public Tile getTile() {
    return tile;
  }

  /**
   * Access the color of the player owning the star token on that tile, can be {@code NONE} if there
   * is no star token.
   * @return The color of the player, or NONE.
   */
  public PlayerColor getStarTokenColor() {
    return starTokenColor;
  }

  /**
   * Access the number of stars on the token, 0 if there are no star token.
   * @return The number of star on the token.
   */
  public int getNbStars() {
    return nbStars;
  }
}
