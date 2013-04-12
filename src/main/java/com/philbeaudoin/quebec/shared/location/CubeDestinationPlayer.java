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

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.player.PlayerState;

/**
 * A cube destination corresponding to the personal reserve of a given player, either active or
 * passive.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class CubeDestinationPlayer implements CubeDestination {

  private PlayerColor playerColor;
  private boolean active;

  public CubeDestinationPlayer(PlayerColor playerColor, boolean active) {
    this.playerColor = playerColor;
    this.active = active;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private CubeDestinationPlayer() {
  }

  @Override
  public int getNbCubes(GameState gameState) {
    PlayerState playerState = gameState.getPlayerState(playerColor);
    assert playerState != null;
    if (active) {
      return playerState.getNbActiveCubes();
    } else {
      return playerState.getNbPassiveCubes();
    }
  }

  @Override
  public PlayerColor getPlayerColor() {
    return playerColor;
  }

  @Override
  public void removeFrom(int nbCubes, GameState gameState) {
    PlayerState playerState = gameState.getPlayerState(playerColor);
    assert playerState != null;
    if (active) {
      int nbActive = playerState.getNbActiveCubes();
      assert nbCubes <= nbActive;
      playerState.setNbActiveCubes(nbActive - nbCubes);
    } else {
      int nbPassive = playerState.getNbPassiveCubes();
      assert nbCubes <= nbPassive;
      playerState.setNbPassiveCubes(nbPassive - nbCubes);
    }
  }

  @Override
  public void addTo(int nbCubes, GameState gameState) {
    PlayerState playerState = gameState.getPlayerState(playerColor);
    assert playerState != null;
    if (active) {
      int nbActive = playerState.getNbActiveCubes();
      playerState.setNbActiveCubes(nbActive + nbCubes);
    } else {
      int nbPassive = playerState.getNbPassiveCubes();
      playerState.setNbPassiveCubes(nbPassive + nbCubes);
    }
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
   * Returns whether or not this destination concerns the active or passive reserve of the player.
   * @return true if it concerns the active reserve, false for the passive reserve.
   */
  public boolean isActive() {
    return active;
  }
}
