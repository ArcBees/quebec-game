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

/**
 * Information on a tile that can be placed on the board.
 *
 * @author Philippe Beaudoin
 */
public class TileInfo {
  private final InfluenceType influenceType;
  private final int century;
  private final int buildingIndex;

  TileInfo(InfluenceType influenceType, int century, int buildingIndex) {
    this.influenceType = influenceType;
    this.century = century;
    this.buildingIndex = buildingIndex;
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
   * @return The index of the building that is printed on the backside of the tileß.
   */
  public int getBuildingIndex() {
    return buildingIndex;
  }
}
