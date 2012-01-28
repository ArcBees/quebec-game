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

package com.philbeaudoin.quebec.shared.state;

import java.util.ArrayList;

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * State of a tile in play.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class TileState {

  private final Tile tile;
  private final Vector2d location;
  private PlayerColor architect = PlayerColor.NONE;
  private boolean buildingFacing;
  private PlayerColor starTokenColor;
  private int nbStars;

  /* The color of the cubes at each of the three building spots. */
  private final ArrayList<PlayerColor> colorInSpot = new ArrayList<PlayerColor>(3);

  TileState(Tile tile, Vector2d location) {
    this.tile = tile;
    this.location = location;
    buildingFacing = false;
    starTokenColor = PlayerColor.NONE;
    nbStars = 0;
    clearCubes();
  }

  /**
   * Copy constructor performing a deep copy of every mutable object contained in this one.
   * @param other The tile state to copy.
   */
  public TileState(TileState other) {
    this.tile = other.tile;
    this.location = other.location;
    this.architect = other.architect;
    this.buildingFacing = other.buildingFacing;
    this.starTokenColor = other.starTokenColor;
    this.nbStars = other.nbStars;
    for (PlayerColor color : other.colorInSpot) {
      colorInSpot.add(color);
    }
  }

  /**
   * @return The information on that tile.
   */
  public Tile getTile() {
    return tile;
  }

  /**
   * @return The location of the tile.
   */
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
    assert playerColor.isNormalColor();
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
   * Sets whether this tile is showing its building side or not.
   * @param buildingFacing true to show the building side.
   */
  public void setBuildingFacing(boolean buildingFacing) {
    this.buildingFacing = buildingFacing;
  }

  /**
   * @return The number of cube each building spot holds.
   */
  public int getCubesPerSpot() {
    BoardAction boardAction =
        Board.actionForTileLocation(location.getColumn(), location.getLine());
    return boardAction.getCubesPerSpot();
  }

  /**
   * @return The color of the star token, or NONE if there is no star token on the tile.
   */
  public PlayerColor getStarTokenColor() {
    return starTokenColor;
  }

  /**
   * @return The number of stars on the star token, or 0 if none.
   */
  public int getNbStars() {
    return nbStars;
  }

  /**
   * Sets the color and number of stars of the star token. For a normal color, the number of stars
   * must be 1, 2 or 3 unless. To remove the star token use NONE and 0 stars.
   * @param starTokenColor The color of the star token, or NONE if no star token. Cannot be NEUTRAL.
   * @param nbStars The number of stars on the star token.
   */
  public void setStarToken(PlayerColor starTokenColor, int nbStars) {
    assert starTokenColor != PlayerColor.NEUTRAL;
    assert starTokenColor.isNormalColor() && nbStars > 0 ||
        starTokenColor == PlayerColor.NONE && nbStars == 0;
    this.starTokenColor = starTokenColor;
    this.nbStars = nbStars;
  }

}
