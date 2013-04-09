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
import com.philbeaudoin.quebec.shared.game.action.ActionExplicit;
import com.philbeaudoin.quebec.shared.game.action.ActionSendWorkers;
import com.philbeaudoin.quebec.shared.game.action.PossibleActions;
import com.philbeaudoin.quebec.shared.message.Message;

/**
 * Board action: yellow, 2 cubes to activate, fill one empty spot on a building for which
 * construction has started.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardActionYellowFillOneSpot extends BoardAction {
  public BoardActionYellowFillOneSpot() {
    super(11, 4, InfluenceType.ECONOMIC, 2, ActionType.YELLOW_FILL_ONE_SPOT);
  }

  public PossibleActions getPossibleActions(GameController gameController, GameState gameState, Tile triggeringTile) {

    PossibleActions result = new PossibleActions(new Message.Text("selectSpotToFill"));
    result.add(ActionExplicit.createSkipAction());

    int nbCubes = gameState.getCurrentPlayer().getNbTotalCubes();

    // Mark moving architect or sending workers as a possible action.
    for (TileState tileState : gameState.getTileStates()) {
      if (tileState.getTile() != triggeringTile &&
          tileState.getArchitect().isArchitectColor() &&
          nbCubes >= tileState.getCubesPerSpot() &&
          tileState.getColorInSpot(2) == PlayerColor.NONE) {
        result.add(new ActionSendWorkers(false, tileState.getTile()));
      }
    }

    return result;
  }

  @Override
  public Message getDescription() {
    return new Message.MultilineText("actionYellow2", 0.7);
  }
}
