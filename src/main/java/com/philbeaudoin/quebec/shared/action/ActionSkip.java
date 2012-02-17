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

/**
 * The action of skipping a move.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ActionSkip implements GameAction {

  private final GameStateChange followup;

  public ActionSkip() {
    this(new GameStateChangeNextPlayer());
  }

  public ActionSkip(GameStateChange followup) {
    assert followup != null;
    this.followup = followup;
  }

  @Override
  public GameStateChange execute(GameState gameState) {
    GameStateChangeComposite result = new GameStateChangeComposite();
    // Move to next player.
    result.add(followup);
    return result;
  }

  @Override
  public void accept(GameActionVisitor visitor) {
    visitor.visit(this);
  }
}