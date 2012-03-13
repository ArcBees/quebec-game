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

import java.util.ArrayList;

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionIncreaseStar;
import com.philbeaudoin.quebec.shared.action.ActionExplicit;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.message.Message;

/**
 * Board action: blue, 3 cubes to activate, add a star to one of your building below 3 stars.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardActionBlueAddStar extends BoardAction {
  public BoardActionBlueAddStar() {
    super(16, 5, InfluenceType.CULTURAL, 3, ActionType.BLUE_ADD_STAR);
  }

  public PossibleActions getPossibleActions(GameState gameState, Tile triggeringTile) {
    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    ArrayList<TileState> tileStates = gameState.getTileStates();

    PossibleActions result = new PossibleActions(new Message.SelectStarTokenToIncrease());
    for (TileState tileState : tileStates) {
      if (tileState.getStarTokenColor() == playerColor && tileState.getNbStars() > 0 &&
          tileState.getNbStars() < 3) {
        result.add(new ActionIncreaseStar(tileState.getTile()));
      }
    }
    result.add(ActionExplicit.createSkipAction());
    return result;
  }
}
