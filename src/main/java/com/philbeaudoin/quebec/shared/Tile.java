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

package com.philbeaudoin.quebec.shared;

import java.util.ArrayList;

import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Information on a tile in play.
 *
 * @author Philippe Beaudoin
 */
public class Tile {

  private final TileInfo tileInfo;
  private final Vector2d location;
  /* The color of the cubes at each of the three building spots. */
  private final ArrayList<PlayerColor> colorInSpot = new ArrayList<PlayerColor>(3);

  private boolean buildingFacing;

  Tile(TileInfo tileInfo, Vector2d location) {
    this.tileInfo = tileInfo;
    this.location = location;
    this.buildingFacing = false;
  }

  /**
   * @return The information on that tile.
   */
  public TileInfo getTileInfo() {
    return tileInfo;
  }

  public Vector2d getLocation() {
    return location;
  }

  /**
   * @param spot The index of the spot for which to return the player color.
   * @return The color of the player in the specified building spot or {@code PlayerColor.NONE} if
   *     the spot is empty.
   */
  public PlayerColor colorInSpot(int spot) {
    assert spot < 3;
    if (colorInSpot.size() <= spot) {
      return PlayerColor.NONE;
    }
    return colorInSpot.get(spot);
  }

  /**
   * @return {@code true} if the tile is showing its building face, {@code false} otherwise.
   */
  public boolean isBuildingFacing() {
    return buildingFacing;
  }

  /**
   * @return The number of cube each building spot holds.
   */
  public int cubesPerSpot() {
    BoardActionInfo info =
        Board.actionInfoForTileLocation(location.getColumn(), location.getLine());
    return info.getCubesPerSpot();
  }
}
