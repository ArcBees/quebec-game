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

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.game.state.Tile;
import com.philbeaudoin.quebec.shared.game.state.TileState;

/**
 * A change of the game state obtained by increasing the star token on a tile.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class GameStateChangeIncreaseStarToken implements GameStateChange {

  private Tile tile;
  private PlayerColor starTokenColor;
  private int nbStarsAfter;

  public GameStateChangeIncreaseStarToken(Tile tile, PlayerColor starTokenColor, int nbStarsAfter) {
    this.tile = tile;
    this.starTokenColor = starTokenColor;
    this.nbStarsAfter = nbStarsAfter;
    assert starTokenColor.isNormalColor();
    assert nbStarsAfter > 1 && nbStarsAfter <= 3;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private GameStateChangeIncreaseStarToken() {
  }

  @Override
  public void apply(GameController gameController, GameState gameState) {
    TileState tileState = gameState.findTileState(tile);
    tileState.setBuildingFacing(true);
    int nbStars = tileState.getNbStars();
    assert nbStars + 1 == nbStarsAfter;
    tileState.setStarToken(tileState.getStarTokenColor(), nbStarsAfter);
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
   * Access the color of the player owning the star token on that tile, cannot be {@code NONE}.
   * @return The color of the player.
   */
  public PlayerColor getStarTokenColor() {
    return starTokenColor;
  }

  /**
   * Access the number of stars on the token after the increase.
   * @return The number of star on the token.
   */
  public int getNbStarsAfter() {
    return nbStarsAfter;
  }
}
