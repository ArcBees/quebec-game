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
import com.philbeaudoin.quebec.shared.game.action.PossibleActions;
import com.philbeaudoin.quebec.shared.message.Message;

/**
 * Board action: yellow, 2 cubes to activate, move your own architect.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardActionYellowMoveArchitect extends BoardAction {
  public BoardActionYellowMoveArchitect() {
    super(3, 4, InfluenceType.ECONOMIC, 2, ActionType.YELLOW_MOVE_ARCHITECT);
  }

  public PossibleActions getPossibleActions(GameController gameController, GameState gameState, Tile triggeringTile) {
    PossibleActions result = new PossibleActions(new Message.Text("moveYourArchitect"));
    gameController.getPossibleMoveArchitectActions(gameState, result);
    result.add(ActionExplicit.createSkipAction());
    return result;
  }

  @Override
  public Message getDescription() {
    return new Message.MultilineText("actionYellow3", 0.7);
  }
}
