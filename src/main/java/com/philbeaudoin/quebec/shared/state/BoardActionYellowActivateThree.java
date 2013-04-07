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

package com.philbeaudoin.quebec.shared.state;

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.action.ActionActivateCubes;
import com.philbeaudoin.quebec.shared.action.ActionExplicit;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.player.PlayerState;

/**
 * Board action: yellow, 3 cube to activate, activate three cubes.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardActionYellowActivateThree extends BoardAction {
  public BoardActionYellowActivateThree() {
    super(7, 0, InfluenceType.ECONOMIC, 3, ActionType.YELLOW_ACTIVATE_THREE);
  }

  public PossibleActions getPossibleActions(GameController gameController, GameState gameState, Tile triggeringTile) {
    PlayerState playerState = gameState.getCurrentPlayer();
    int nbCubes = Math.min(3, playerState.getNbPassiveCubes());
    PossibleActions result = new PossibleActions();
    if (nbCubes > 0) {
      result.add(new ActionActivateCubes(nbCubes));
    }
    result.add(ActionExplicit.createSkipAction());
    return result;
  }

  @Override
  public Message getDescription() {
    return new Message.MultilineText("actionYellow4", 0.7);
  }
}
