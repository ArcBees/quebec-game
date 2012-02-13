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
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionSendCubesToZone;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.action.PossibleActionsComposite;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationInfluenceZone;

/**
 * Board action: red, 3 cubes to activate, send two cubes to citadel.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardActionRedTwoToCitadel extends BoardAction {
  public BoardActionRedTwoToCitadel() {
    super(1, 2, InfluenceType.POLITIC, 3, ActionType.RED_TWO_TO_CITADEL);
  }

  public PossibleActions getPossibleActions(GameState gameState) {
    PlayerState playerState = gameState.getCurrentPlayer();
    PlayerColor playerColor = playerState.getPlayer().getColor();
    int nbCubes = Math.min(2, playerState.getNbTotalCubes());
    PossibleActionsComposite result = new PossibleActionsComposite();
    result.add(new ActionSendCubesToZone(nbCubes,
        new CubeDestinationInfluenceZone(InfluenceType.CITADEL, playerColor)));
    return result;
  }
}
