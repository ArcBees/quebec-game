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

package com.philbeaudoin.quebec.shared.statechange;

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.PlayerState;

/**
 * An architect destination corresponding to a player zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ArchitectDestinationPlayer implements ArchitectDestination {

  private final PlayerColor playerColor;
  private final boolean neutralArchitect;

  public ArchitectDestinationPlayer(PlayerColor playerColor, boolean neutralArchitect) {
    this.playerColor = playerColor;
    this.neutralArchitect = neutralArchitect;
  }

  @Override
  public PlayerColor getArchitectColor() {
    return neutralArchitect ? PlayerColor.NEUTRAL : playerColor;
  }

  @Override
  public void removeFrom(GameState gameState) {
    PlayerState playerState = gameState.getPlayerState(playerColor);
    assert playerState != null;
    if (neutralArchitect) {
      assert playerState.isHoldingNeutralArchitect();
      playerState.setHoldingNeutralArchitect(false);
    } else {
      assert playerState.isHoldingArchitect();
      playerState.setHoldingArchitect(false);
    }
  }

  @Override
  public void addTo(GameState gameState) {
    PlayerState playerState = gameState.getPlayerState(playerColor);
    assert playerState != null;
    if (neutralArchitect) {
      playerState.setHoldingNeutralArchitect(true);
    } else {
      playerState.setHoldingArchitect(true);
    }
  }

  @Override
  public void accept(ArchitectDestinationVisitor visitor) {
    visitor.visit(this);
  }

  /**
   * Returns the color of the player zone of that destination.
   * @return The zone color.
   */
  public PlayerColor getPlayerColor() {
    return playerColor;
  }

  /**
   * @return True if the destination architect color is the neutral architect.
   */
  public boolean isNeutralArchitect() {
    return neutralArchitect;
  }
}
