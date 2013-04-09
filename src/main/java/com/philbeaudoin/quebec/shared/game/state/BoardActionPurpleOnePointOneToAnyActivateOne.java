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
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.action.ActionActivateCubes;
import com.philbeaudoin.quebec.shared.game.action.ActionExplicit;
import com.philbeaudoin.quebec.shared.game.action.ActionScorePoints;
import com.philbeaudoin.quebec.shared.game.action.ActionSendCubesToZone;
import com.philbeaudoin.quebec.shared.game.action.PossibleActions;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeQueuePossibleActions;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.player.PlayerState;

/**
 * Board action: purple, 2 cubes to activate, score one point, send one passive cube to any zone,
 * activate one cube.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardActionPurpleOnePointOneToAnyActivateOne extends BoardAction {
  public BoardActionPurpleOnePointOneToAnyActivateOne() {
    super(14, 3, InfluenceType.RELIGIOUS, 2, ActionType.PURPLE_ONE_POINT_ONE_TO_ANY_ACTIVATE_ONE);
  }

  public PossibleActions getPossibleActions(GameController gameController, GameState gameState, Tile triggeringTile) {
    PlayerState playerState = gameState.getCurrentPlayer();
    PlayerColor playerColor = playerState.getColor();
    int totalCubes = playerState.getNbTotalCubes();

    PossibleActions sendAnywhere = new PossibleActions(
        new Message.SendPassiveCubesToAnyZoneOrCitadel(1, playerColor));
    sendAnywhere.add(new ActionSendCubesToZone(1, false, InfluenceType.RELIGIOUS));
    sendAnywhere.add(new ActionSendCubesToZone(1, false, InfluenceType.POLITIC));
    sendAnywhere.add(new ActionSendCubesToZone(1, false, InfluenceType.ECONOMIC));
    sendAnywhere.add(new ActionSendCubesToZone(1, false, InfluenceType.CULTURAL));
    sendAnywhere.add(new ActionSendCubesToZone(1, false, InfluenceType.CITADEL));
    sendAnywhere.add(ActionExplicit.createSkipAction());

    PossibleActions activateOrSendAnywhere;
    if (playerState.getNbPassiveCubes() > 0) {
      GameStateChange activateFollowup = new GameStateChangeQueuePossibleActions(sendAnywhere);
      activateOrSendAnywhere = new PossibleActions();
      activateOrSendAnywhere.add(new ActionActivateCubes(1, activateFollowup));
      activateOrSendAnywhere.add(new ActionExplicit(new Message.Text("skip"), activateFollowup));
    } else {
      activateOrSendAnywhere = sendAnywhere;
    }

    PossibleActions result = new PossibleActions();
    if (totalCubes >= 1) {
      result.add(new ActionScorePoints(1,
          new GameStateChangeQueuePossibleActions(activateOrSendAnywhere)));
    } else {
      result.add(new ActionScorePoints(1));
    }
    return result;
  }

  @Override
  public Message getDescription() {
    return new Message.MultilineText("actionPurple3", 0.7);
  }
}
