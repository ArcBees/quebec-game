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
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.state.TileState;

/**
 * An architect destination corresponding to a tile.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ArchitectDestinationTile implements ArchitectDestination {

  private final Tile tile;
  private final PlayerColor architectColor;

  public ArchitectDestinationTile(Tile tile, PlayerColor architectColor) {
    this.tile = tile;
    this.architectColor = architectColor;
  }

  @Override
  public PlayerColor getArchitectColor() {
    return architectColor;
  }

  @Override
  public void removeFrom(GameState gameState) {
    TileState tileState = gameState.findTileState(tile);
    assert tileState != null;
    assert tileState.getArchitect() == architectColor;
    tileState.setArchitect(PlayerColor.NONE);
  }

  @Override
  public void addTo(GameState gameState) {
    TileState tileState = gameState.findTileState(tile);
    assert tileState != null;
    assert tileState.getArchitect() == PlayerColor.NONE;
    tileState.setArchitect(architectColor);
  }

  @Override
  public <T> T accept(ArchitectDestinationVisitor<T> visitor) {
    return visitor.visit(this);
  }

  /**
   * Returns the tile onto which cubes are added or removed by this destination.
   * @return The tile.
   */
  public Tile getTile() {
    return tile;
  }
}
