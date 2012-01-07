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
 * State of a tile in play.
 *
 * @author Philippe Beaudoin
 */
public class TileState {

  private final Tile tile;
  private final Vector2d location;
  private PlayerColor architect = PlayerColor.NONE;
  /* The color of the cubes at each of the three building spots. */
  private final ArrayList<PlayerColor> colorInSpot = new ArrayList<PlayerColor>(3);

  private boolean buildingFacing;

  TileState(Tile tile, Vector2d location) {
    this.tile = tile;
    this.location = location;
    this.buildingFacing = false;
    clearCubes();
  }

  /**
   * @return The information on that tile.
   */
  public Tile getTile() {
    return tile;
  }

  public Vector2d getLocation() {
    return location;
  }

  /**
   * Retrieves the color of the architect sitting on the tile.
   * @return The player color of the architect, or {@code PlayerColor.NONE} if none.
   */
  public PlayerColor getArchitect() {
    return architect;
  }

  /**
   * Sets the color of the architect sitting on the tile.
   * param architect The player color of the architect, or {@code PlayerColor.NONE} if none.
   */
  public void setArchitect(PlayerColor architect) {
    this.architect = architect;
  }

  /**
   * Retrieves the color of the cubes in a given spot of the tile.
   * @param spot The index of the spot for which to return the player color.
   * @return The color of the player in the specified building spot or {@code PlayerColor.NONE} if
   *     the spot is empty.
   */
  public PlayerColor getColorInSpot(int spot) {
    assert spot < 3;
    return colorInSpot.get(spot);
  }

  /**
   * Sets the color of the cubes in a given spot of the tile.
   * @param spot The index of the spot in which to set a player color.
   * @param playerColor The color of the player in the specified building spot. Use
   *     {@code PlayerColor.NONE} if the spot is empty.
   */
  public void setColorInSpot(int spot, PlayerColor playerColor) {
    assert playerColor != PlayerColor.NONE && playerColor != PlayerColor.NEUTRAL;
    assert spot < 3;
    colorInSpot.set(spot, playerColor);
  }

  /**
   * Remove all the cubes in the three spots.
   */
  public void clearCubes() {
    colorInSpot.clear();
    while (colorInSpot.size() <= 3) {
      colorInSpot.add(PlayerColor.NONE);
    }
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
    BoardAction boardAction =
        Board.actionForTileLocation(location.getColumn(), location.getLine());
    return boardAction.getCubesPerSpot();
  }
}
