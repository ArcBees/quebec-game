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

package com.philbeaudoin.quebec.shared.action;

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.PlayerState;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationInfluenceZone;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeNextPlayer;

/**
 * The action of sending one worker to an influence zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ActionSendOneWorker implements GameActionOnInfluenceZone {

  private final InfluenceType influenceZone;

  public ActionSendOneWorker(InfluenceType influenceZone) {
    this.influenceZone = influenceZone;
  }

  @Override
  public GameStateChange execute(GameState gameState) {
    GameStateChangeComposite result = new GameStateChangeComposite();
    PlayerState playerState = gameState.getCurrentPlayer();
    PlayerColor activePlayer = playerState.getPlayer().getColor();

    assert playerState.getNbActiveCubes() >= 1;

    // Move the cubes.
    result.add(new GameStateChangeMoveCubes(1,
        new CubeDestinationPlayer(activePlayer, true),
        new CubeDestinationInfluenceZone(influenceZone, activePlayer)));

    // Move to next player.
    result.add(new GameStateChangeNextPlayer());

    return result;
  }

  @Override
  public void accept(GameActionVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public InfluenceType getInfluenceZone() {
    return influenceZone;
  }
}
