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

package com.philbeaudoin.quebec.shared.location;

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.game.state.GameState;

/**
 * A cube destination corresponding to an given player color within an influence zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class CubeDestinationInfluenceZone implements CubeDestination {

  private InfluenceType influenceType;
  private PlayerColor playerColor;

  public CubeDestinationInfluenceZone(InfluenceType influenceType,
      PlayerColor playerColor) {
    this.influenceType = influenceType;
    this.playerColor = playerColor;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private CubeDestinationInfluenceZone() {
  }

  @Override
  public int getNbCubes(GameState gameState) {
    return gameState.getPlayerCubesInInfluenceZone(influenceType, playerColor);
  }

  @Override
  public PlayerColor getPlayerColor() {
    return playerColor;
  }

  @Override
  public void removeFrom(int nbCubes, GameState gameState) {
    int nbCubesInZone = gameState.getPlayerCubesInInfluenceZone(influenceType, playerColor);
    assert nbCubesInZone >= nbCubes;
    gameState.setPlayerCubesInInfluenceZone(influenceType, playerColor, nbCubesInZone - nbCubes);
  }

  @Override
  public void addTo(int nbCubes, GameState gameState) {
    int nbCubesInZone = gameState.getPlayerCubesInInfluenceZone(influenceType, playerColor);
    gameState.setPlayerCubesInInfluenceZone(influenceType, playerColor, nbCubesInZone + nbCubes);
  }

  @Override
  public <T> T accept(CubeDestinationVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public <T> T accept(LocationVisitor<T> visitor) {
    return visitor.visit(this);
  }

  /**
   * Access the influence zone of this destination.
   * @return The influence type of that zone.
   */
  public InfluenceType getInfluenceType() {
    return influenceType;
  }
}
