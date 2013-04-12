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

package com.philbeaudoin.quebec.shared.player;

import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.action.GameAction;
import com.philbeaudoin.quebec.shared.game.action.PossibleActions;
import com.philbeaudoin.quebec.shared.game.state.GameState;

/**
 * The brain of an artificial intelligence that plays randomly.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class AiBrainRandom implements AiBrain {

  @Override
  public GameAction getMove(GameController gameController, GameState gameState) {
    PossibleActions possibleActions = gameState.getPossibleActions();
    if (possibleActions != null && possibleActions.getNbActions() > 0) {
      int actionIndex = (int) (Math.random() * possibleActions.getNbActions());
      return possibleActions.getAction(actionIndex);
    }
    return null;
  }

  @Override
  public String getSuffix() {
    return "Random AI";
  }
}
