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
import com.philbeaudoin.quebec.shared.action.ActionExplicit;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.player.PlayerState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeQueuePossibleActions;

/**
 * Board action: purple, 2 cubes to activate, send one passive cube to citadel and one passive cube
 * to any zone (but not the citadel).
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardActionPurpleOneToCitadelOneToAny extends BoardAction {
  public BoardActionPurpleOneToCitadelOneToAny() {
    super(6, 3, InfluenceType.RELIGIOUS, 2, ActionType.PURPLE_ONE_TO_CITADEL_ONE_TO_ANY);
  }

  public PossibleActions getPossibleActions(GameController gameController, GameState gameState, Tile triggeringTile) {
    PlayerState playerState = gameState.getCurrentPlayer();
    PlayerColor playerColor = playerState.getColor();
    int totalCubes = playerState.getNbTotalCubes();

    PossibleActions sendAnywhere = new PossibleActions(
        new Message.SendPassiveCubesToAnyZone(1, playerColor));
    sendAnywhere.add(new ActionSendCubesToZone(1, false, InfluenceType.RELIGIOUS));
    sendAnywhere.add(new ActionSendCubesToZone(1, false, InfluenceType.POLITIC));
    sendAnywhere.add(new ActionSendCubesToZone(1, false, InfluenceType.ECONOMIC));
    sendAnywhere.add(new ActionSendCubesToZone(1, false, InfluenceType.CULTURAL));
    sendAnywhere.add(ActionExplicit.createSkipAction());

    GameStateChange sendAnywhereFollowup = new GameStateChangeQueuePossibleActions(sendAnywhere);
    PossibleActions result = new PossibleActions(new Message.SendPassiveCubesToZone(1,
        playerColor, InfluenceType.CITADEL));
    if (totalCubes >= 2) {
      result.add(new ActionSendCubesToZone(1, false, InfluenceType.CITADEL, sendAnywhereFollowup));
      result.add(new ActionExplicit(new Message.Text("skip"), sendAnywhereFollowup));
    } else if (totalCubes >= 1) {
      result.add(new ActionSendCubesToZone(1, false, InfluenceType.CITADEL));
      result.add(new ActionExplicit(new Message.Text("skip"), sendAnywhereFollowup));
    } else {
      result.add(ActionExplicit.createSkipAction());
    }
    return result;
  }

  @Override
  public Message getDescription() {
    return new Message.MultilineText("actionPurple2", 0.7);
  }
}
