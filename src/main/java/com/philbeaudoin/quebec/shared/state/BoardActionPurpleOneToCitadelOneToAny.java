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
import com.philbeaudoin.quebec.shared.action.PossibleActions;
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

  public PossibleActions getPossibleActions(GameState gameState) {
    int totalCubes = gameState.getCurrentPlayer().getNbTotalCubes();
    if (totalCubes == 0) {
      return null;
    }

    PossibleActions sendAnywhere = null;
    if (totalCubes >= 2) {
      sendAnywhere = new PossibleActions();
      sendAnywhere.add(new ActionSendCubesToZone(1, InfluenceType.RELIGIOUS));
      sendAnywhere.add(new ActionSendCubesToZone(1, InfluenceType.POLITIC));
      sendAnywhere.add(new ActionSendCubesToZone(1, InfluenceType.ECONOMIC));
      sendAnywhere.add(new ActionSendCubesToZone(1, InfluenceType.CULTURAL));
    }

    PossibleActions result = new PossibleActions();
    if (sendAnywhere != null) {
      result.add(new ActionSendCubesToZone(1, InfluenceType.CITADEL,
          new GameStateChangeQueuePossibleActions(sendAnywhere)));
    } else {
      result.add(new ActionSendCubesToZone(1, InfluenceType.CITADEL));
    }
    return result;
  }
}
