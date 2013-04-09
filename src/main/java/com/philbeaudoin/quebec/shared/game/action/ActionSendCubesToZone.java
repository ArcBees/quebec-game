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

package com.philbeaudoin.quebec.shared.game.action;

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeNextPlayer;
import com.philbeaudoin.quebec.shared.location.CubeDestinationInfluenceZone;
import com.philbeaudoin.quebec.shared.location.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.player.PlayerState;

/**
 * The action of sending active or passive worker cubes to a given influence zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ActionSendCubesToZone implements GameActionOnInfluenceZone {

  private int nbCubes;
  private boolean fromActive;
  private InfluenceType to;
  private GameStateChange followup;

  public ActionSendCubesToZone(int nbCubes, boolean fromActive, InfluenceType to) {
    this(nbCubes, fromActive, to, new GameStateChangeNextPlayer());
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private ActionSendCubesToZone() {
  }

  public ActionSendCubesToZone(int nbCubes, boolean fromActive, InfluenceType to,
      GameStateChange followup) {
    assert followup != null;
    this.nbCubes = nbCubes;
    this.fromActive = fromActive;
    this.to = to;
    this.followup = followup;
  }

  @Override
  public GameStateChange execute(GameController gameController, GameState gameState) {
    PlayerState playerState = gameState.getCurrentPlayer();
    PlayerColor activePlayer = playerState.getColor();

    GameStateChangeComposite result = new GameStateChangeComposite();

    int nbMoved = 0;
    CubeDestinationInfluenceZone destination = new CubeDestinationInfluenceZone(to, activePlayer);
    if (!fromActive) {
      assert nbCubes <= playerState.getNbTotalCubes();
      // Move as much cube as we can from the passive reserve.
      nbMoved = Math.min(nbCubes, playerState.getNbPassiveCubes());
      if (nbMoved > 0) {
        result.add(new GameStateChangeMoveCubes(nbMoved,
            new CubeDestinationPlayer(activePlayer, false), destination));
      }
    }

    // Move the balance from the active reserve.
    int nbToMove = nbCubes - nbMoved;
    if (nbToMove > 0) {
      assert nbToMove <= playerState.getNbActiveCubes();
      result.add(new GameStateChangeMoveCubes(nbToMove,
          new CubeDestinationPlayer(activePlayer, true), destination));
    }

    // Execute follow-up move.
    result.add(followup);

    return result;
  }

  @Override
  public boolean isAutomatic() {
    return false;
  }

  @Override
  public void accept(GameActionVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public InfluenceType getInfluenceZone() {
    return to;
  }

  /**
   * Returns the number of cubes sent by this action.
   * @return The number of cubes.
   */
  public int getNbCubes() {
    return nbCubes;
  }

  /**
   * Returns whether the cubes come from the active or the passive reserve, with overflow coming
   * from the active reserve.
   * @return True if they come from the active reserve.
   */
  public boolean areCubesFromActive() {
    return fromActive;
  }
}
