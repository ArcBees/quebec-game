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
 * The action of sending passive worker cubes to a given influence zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ActionSendCubesToZone implements GameActionOnInfluenceZone {

  private final int nbCubes;
  private final InfluenceType to;
  private final GameStateChange followup;

  public ActionSendCubesToZone(int nbCubes, InfluenceType to) {
    this(nbCubes, to, new GameStateChangeNextPlayer());
  }

  public ActionSendCubesToZone(int nbCubes, InfluenceType to, GameStateChange followup) {
    assert followup != null;
    this.nbCubes = nbCubes;
    this.to = to;
    this.followup = followup;
  }

  @Override
  public GameStateChange execute(GameState gameState) {
    PlayerState playerState = gameState.getCurrentPlayer();
    PlayerColor activePlayer = playerState.getPlayer().getColor();

    assert nbCubes <= playerState.getNbTotalCubes();

    GameStateChangeComposite result = new GameStateChangeComposite();

    // Move as much cube as we want from the passive reserve.
    int nbMoved = Math.min(nbCubes, playerState.getNbPassiveCubes());
    CubeDestinationInfluenceZone destination = new CubeDestinationInfluenceZone(to, activePlayer);
    if (nbMoved > 0) {
      result.add(new GameStateChangeMoveCubes(nbMoved,
          new CubeDestinationPlayer(activePlayer, false), destination));
    }
    // Move the balance from the active reserve.
    nbMoved = nbCubes - nbMoved;
    if (nbMoved > 0) {
      result.add(new GameStateChangeMoveCubes(nbMoved,
          new CubeDestinationPlayer(activePlayer, true), destination));
    }

    // Move to next player.
    result.add(followup);

    return result;
  }

  @Override
  public void accept(GameActionVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public InfluenceType getInfluenceZone() {
    return to;
  }
}
