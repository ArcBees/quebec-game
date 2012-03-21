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
import com.philbeaudoin.quebec.shared.action.ActionSelectBoardAction;
import com.philbeaudoin.quebec.shared.action.ActionExplicit;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.message.Message;

/**
 * Board action: red, 1 cube to activate, perform any red action.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardActionRedAny extends BoardAction {
  public BoardActionRedAny() {
    super(14, 7, InfluenceType.POLITIC, 1, ActionType.RED_ANY);
  }

  public PossibleActions getPossibleActions(GameState gameState, Tile triggeringTile) {
    PossibleActions result = new PossibleActions(new Message.Text("selectActionToExecute"));
    result.add(new ActionSelectBoardAction(new BoardActionRedTwoToCitadel(), triggeringTile));
    result.add(new ActionSelectBoardAction(new BoardActionRedTwoToPurpleOrYellow(),
        triggeringTile));
    result.add(new ActionSelectBoardAction(new BoardActionRedTwoToRedOrBlue(), triggeringTile));
    result.add(ActionExplicit.createSkipAction());
    return result;
  }
}
