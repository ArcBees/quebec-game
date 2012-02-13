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
import com.philbeaudoin.quebec.shared.action.PossibleActions;

/**
 * Board action: yellow, 2 cubes to activate, fill one empty spot on a building for which
 * construction has started.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardActionYellowFillOneSpot extends BoardAction {
  public BoardActionYellowFillOneSpot() {
    super(11, 4, InfluenceType.ECONOMIC, 2, ActionType.YELLOW_FILL_ONE_SPOT);
  }

  public PossibleActions getPossibleActions(GameState gameState) {
    // TODO(beaudoin): Fill-in.
    return null;
  }
}
