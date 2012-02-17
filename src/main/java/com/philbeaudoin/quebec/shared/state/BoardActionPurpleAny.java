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
 * Board action: purple, 1 cube to activate, perform any purple action.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardActionPurpleAny extends BoardAction {
  public BoardActionPurpleAny() {
    super(1, 6, InfluenceType.RELIGIOUS, 1, ActionType.PURPLE_ANY);
  }

  public PossibleActions getPossibleActions(GameState gameState) {
    PossibleActions result = new PossibleActions(new Message.SelectAction());
    result.add(new ActionSelectBoadAction(new BoardActionPurpleOnePointOneToAnyActivateOne()));
    result.add(new ActionSelectBoadAction(new BoardActionPurpleOneToAnyMoveTwo()));
    result.add(new ActionSelectBoadAction(new BoardActionPurpleOneToCitadelOneToAny()));
    result.add(new ActionSkip());
    return result;
  }
}
