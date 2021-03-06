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

package com.philbeaudoin.quebec.shared.game.state;

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.action.ActionExplicit;
import com.philbeaudoin.quebec.shared.game.action.ActionSendCubesToZone;
import com.philbeaudoin.quebec.shared.game.action.PossibleActions;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.player.PlayerState;

/**
 * Board action: red, 3 cubes to activate, send two cubes to citadel.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardActionRedTwoToCitadel extends BoardAction {
  public BoardActionRedTwoToCitadel() {
    super(1, 2, InfluenceType.POLITIC, 3, ActionType.RED_TWO_TO_CITADEL);
  }

  public PossibleActions getPossibleActions(GameController gameController, GameState gameState, Tile triggeringTile) {
    PlayerState playerState = gameState.getCurrentPlayer();
    int nbCubes = Math.min(2, playerState.getNbTotalCubes());
    PossibleActions result = new PossibleActions(
        new Message.SendPassiveCubesToZone(2, playerState.getColor(),
        InfluenceType.CITADEL));
    result.add(new ActionSendCubesToZone(nbCubes, false, InfluenceType.CITADEL));
    result.add(ActionExplicit.createSkipAction());
    return result;
  }

  @Override
  public Message getDescription() {
    return new Message.MultilineText("actionRed4", 0.7);
  }
}
