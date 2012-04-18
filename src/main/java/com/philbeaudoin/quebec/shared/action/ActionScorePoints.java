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

import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeNextPlayer;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeScorePoints;

/**
 * A game action where the current player score points.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ActionScorePoints implements GameAction {

  private final int nbPoints;
  private final GameStateChange followup;

  public ActionScorePoints(int nbPoints) {
    this(nbPoints, new GameStateChangeNextPlayer());
  }

  public ActionScorePoints(int nbPoints, GameStateChange followup) {
    this.nbPoints = nbPoints;
    this.followup = followup;
  }

  @Override
  public GameStateChange execute(GameState gameState) {
    GameStateChangeComposite result = new GameStateChangeComposite();
    result.add(new GameStateChangeScorePoints(
        gameState.getCurrentPlayer().getPlayer().getColor(), nbPoints));
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
   * Access the number of points scored by that action.
   * @return The number of points.
   */
  public int getNbPoints() {
    return nbPoints;
  }
}
