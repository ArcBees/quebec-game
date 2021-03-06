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

import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeNextPlayer;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeScorePoints;

/**
 * A game action where the current player score points.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class ActionScorePoints implements GameAction {

  private int nbPoints;
  private GameStateChange followup;

  public ActionScorePoints(int nbPoints) {
    this(nbPoints, new GameStateChangeNextPlayer());
  }

  public ActionScorePoints(int nbPoints, GameStateChange followup) {
    this.nbPoints = nbPoints;
    this.followup = followup;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private ActionScorePoints() {
  }

  @Override
  public GameStateChange execute(GameController gameController, GameState gameState) {
    GameStateChangeComposite result = new GameStateChangeComposite();
    result.add(new GameStateChangeScorePoints(
        gameState.getCurrentPlayer().getColor(), nbPoints));
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
