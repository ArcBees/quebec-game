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

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeNextPlayer;
import com.philbeaudoin.quebec.shared.location.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.player.PlayerState;

/**
 * The action of sending passive worker cubes to a given influence zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class ActionActivateCubes implements GameAction {

  private int nbCubes;
  private GameStateChange followup;

  public ActionActivateCubes(int nbCubes) {
    this(nbCubes, new GameStateChangeNextPlayer());
  }

  public ActionActivateCubes(int nbCubes, GameStateChange followup) {
    assert followup != null;
    this.nbCubes = nbCubes;
    this.followup = followup;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private ActionActivateCubes() {
  }

  @Override
  public GameStateChange execute(GameController gameController, GameState gameState) {
    PlayerState playerState = gameState.getCurrentPlayer();
    PlayerColor activePlayer = playerState.getColor();
    assert playerState.getNbPassiveCubes() >= nbCubes;

    GameStateChangeComposite result = new GameStateChangeComposite();
    result.add(new GameStateChangeMoveCubes(nbCubes,
        new CubeDestinationPlayer(activePlayer, false),
        new CubeDestinationPlayer(activePlayer, true)));

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

  /**
   * Returns the number of cubes sent by this action.
   * @return The number of cubes.
   */
  public int getNbCubes() {
    return nbCubes;
  }
}
