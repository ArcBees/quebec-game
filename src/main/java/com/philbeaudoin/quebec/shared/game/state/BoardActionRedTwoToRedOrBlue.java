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
 * Board action: red, 2 cubes to activate, send two cubes to politic or cultural influence zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardActionRedTwoToRedOrBlue extends BoardAction {
  public BoardActionRedTwoToRedOrBlue() {
    super(5, 6, InfluenceType.POLITIC, 2, ActionType.RED_TWO_TO_RED_OR_BLUE);
  }

  public PossibleActions getPossibleActions(GameController gameController, GameState gameState, Tile triggeringTile) {
    PlayerState playerState = gameState.getCurrentPlayer();
    int nbCubes = Math.min(2, playerState.getNbTotalCubes());
    PossibleActions result = new PossibleActions(
        new Message.SendPassiveCubesToOneOfTwoZones(2, playerState.getColor(),
        InfluenceType.POLITIC, InfluenceType.CULTURAL));
    result.add(new ActionSendCubesToZone(nbCubes, false, InfluenceType.POLITIC));
    result.add(new ActionSendCubesToZone(nbCubes, false, InfluenceType.CULTURAL));
    result.add(ActionExplicit.createSkipAction());
    return result;
  }

  @Override
  public Message getDescription() {
    return new Message.MultilineText("actionRed2", 0.7);
  }
}
