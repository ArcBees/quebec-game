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

package com.philbeaudoin.quebec.shared.game.state;

import java.io.Serializable;

import com.philbeaudoin.quebec.shared.InfluenceType;

/**
 * Information on a tile that can be placed on the board. This information never changes during the
 * game, it should be unmutable. See also {@link TileState}.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class Tile implements Serializable {
  private InfluenceType influenceType;
  private int century;
  private int buildingIndex;

  Tile(InfluenceType influenceType, int century, int buildingIndex) {
    this.influenceType = influenceType;
    this.century = century;
    this.buildingIndex = buildingIndex;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private Tile() {
  }

  /**
   * @return The type of influence (color) of that tile.
   */
  public InfluenceType getInfluenceType() {
    return influenceType;
  }

  /**
   * @return The century in which that tile is built.
   */
  public int getCentury() {
    return century;
  }

  /**
   * @return The index of the building that is printed on the backside of the tile.
   */
  public int getBuildingIndex() {
    return buildingIndex;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Tile other = (Tile) obj;
    if (buildingIndex != other.buildingIndex)
      return false;
    if (century != other.century)
      return false;
    if (influenceType != other.influenceType)
      return false;
    return true;
  }
}
