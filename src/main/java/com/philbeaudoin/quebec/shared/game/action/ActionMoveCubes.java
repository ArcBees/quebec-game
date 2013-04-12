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
import com.philbeaudoin.quebec.shared.player.PlayerState;

/**
 * The action of moving cubes from one influence zone to another.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class ActionMoveCubes implements GameActionOnInfluenceZone {

  private int nbCubes;
  private InfluenceType from;
  private InfluenceType to;
  private GameStateChange followup;

  public ActionMoveCubes(int nbCubes, InfluenceType from, InfluenceType to) {
    this(nbCubes, from, to, new GameStateChangeNextPlayer());
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private ActionMoveCubes() {
  }

  public ActionMoveCubes(int nbCubes, InfluenceType from, InfluenceType to,
      GameStateChange followup) {
    assert followup != null;
    assert from != to;
    this.nbCubes = nbCubes;
    this.from = from;
    this.to = to;
    this.followup = followup;
  }

  @Override
  public GameStateChange execute(GameController gameController, GameState gameState) {
    PlayerState playerState = gameState.getCurrentPlayer();
    PlayerColor activePlayer = playerState.getColor();

    GameStateChangeComposite result = new GameStateChangeComposite();

    CubeDestinationInfluenceZone origin = new CubeDestinationInfluenceZone(from, activePlayer);
    assert origin.getNbCubes(gameState) >= getNbCubes();
    CubeDestinationInfluenceZone destination = new CubeDestinationInfluenceZone(to, activePlayer);
    // Move as much cube as we want from the passive reserve.
    result.add(new GameStateChangeMoveCubes(getNbCubes(), origin, destination));

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

  public InfluenceType getInfluenceZoneFrom() {
    return from;
  }

  public InfluenceType getInfluenceZoneTo() {
    return to;
  }

  /**
   * Returns the number of cubes sent by this action.
   * @return The number of cubes.
   */
  public int getNbCubes() {
    return nbCubes;
  }

  @Override
  public InfluenceType getInfluenceZone() {
    return to;
  }
}
