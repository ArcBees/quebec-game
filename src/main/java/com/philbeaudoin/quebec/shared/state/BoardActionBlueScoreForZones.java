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
import com.philbeaudoin.quebec.shared.action.ActionScorePoints;
import com.philbeaudoin.quebec.shared.action.PossibleActions;

/**
 * Board action: blue, 2 cubes to activate, score based on the number of influence zones in which
 * the player has at least one cube.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardActionBlueScoreForZones extends BoardAction {
  public BoardActionBlueScoreForZones() {
    super(8, 5, InfluenceType.CULTURAL, 2, ActionType.BLUE_SCORE_FOR_ZONES);
  }

  public PossibleActions getPossibleActions(GameState gameState, Tile triggeringTile) {
    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    int nbZonesWithAtLeastOneCube = 0;
    for (InfluenceType influenceType : InfluenceType.values()) {
      if (gameState.getPlayerCubesInInfluenceZone(influenceType, playerColor) > 0) {
        nbZonesWithAtLeastOneCube++;
      }
    }
    int nbPoints = 0;
    if (nbZonesWithAtLeastOneCube >= 3) {
      nbPoints = 4;
    } else if (nbZonesWithAtLeastOneCube == 2) {
      nbPoints = 2;
    } else if (nbZonesWithAtLeastOneCube == 1) {
      nbPoints = 1;
    }
    PossibleActions result = new PossibleActions();
    result.add(new ActionScorePoints(nbPoints));
    return result;
  }
}
