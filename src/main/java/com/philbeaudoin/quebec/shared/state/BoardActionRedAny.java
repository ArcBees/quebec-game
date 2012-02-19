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
import com.philbeaudoin.quebec.shared.action.ActionSelectBoadAction;
import com.philbeaudoin.quebec.shared.action.ActionSkip;
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

  public PossibleActions getPossibleActions(GameState gameState) {
    PossibleActions result = new PossibleActions(new Message.SelectAction());
    result.add(new ActionSelectBoadAction(new BoardActionRedTwoToCitadel()));
    result.add(new ActionSelectBoadAction(new BoardActionRedTwoToPurpleOrYellow()));
    result.add(new ActionSelectBoadAction(new BoardActionRedTwoToRedOrBlue()));
    result.add(new ActionSkip());
    return result;
  }
}