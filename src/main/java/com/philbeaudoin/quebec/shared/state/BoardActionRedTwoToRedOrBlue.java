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
import com.philbeaudoin.quebec.shared.action.ActionSendCubesToZone;
import com.philbeaudoin.quebec.shared.action.ActionSkip;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.message.Message;

/**
 * Board action: red, 2 cubes to activate, send two cubes to politic or cultural influence zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardActionRedTwoToRedOrBlue extends BoardAction {
  public BoardActionRedTwoToRedOrBlue() {
    super(5, 6, InfluenceType.POLITIC, 2, ActionType.RED_TWO_TO_RED_OR_BLUE);
  }

  public PossibleActions getPossibleActions(GameState gameState) {
    PlayerState playerState = gameState.getCurrentPlayer();
    int nbCubes = Math.min(2, playerState.getNbTotalCubes());
    PossibleActions result = new PossibleActions(
        new Message.SendPassiveCubesToOneOfTwoZones(2, playerState.getPlayer().getColor(),
        InfluenceType.POLITIC, InfluenceType.CULTURAL));
    result.add(new ActionSendCubesToZone(nbCubes, false, InfluenceType.POLITIC));
    result.add(new ActionSendCubesToZone(nbCubes, false, InfluenceType.CULTURAL));
    result.add(new ActionSkip());
    return result;
  }
}
