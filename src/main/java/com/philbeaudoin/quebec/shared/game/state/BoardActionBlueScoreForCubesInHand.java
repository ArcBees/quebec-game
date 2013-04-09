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
import com.philbeaudoin.quebec.shared.game.action.ActionScorePoints;
import com.philbeaudoin.quebec.shared.game.action.PossibleActions;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.player.PlayerState;

/**
 * Board action: blue, 2 cubes to activate, score based on the number of cubes in hand.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardActionBlueScoreForCubesInHand extends BoardAction {
  public BoardActionBlueScoreForCubesInHand() {
    super(12, 1, InfluenceType.CULTURAL, 2, ActionType.BLUE_SCORE_FOR_CUBES_IN_HAND);
  }

  public PossibleActions getPossibleActions(GameController gameController, GameState gameState, Tile triggeringTile) {
    PlayerState playerState = gameState.getCurrentPlayer();
    int nbPoints = 0;
    int nbActiveCubes = playerState.getNbActiveCubes();
    if (nbActiveCubes >= 3) {
      nbPoints = 4;
    } else if (nbActiveCubes == 2) {
      nbPoints = 2;
    } else if (nbActiveCubes == 1) {
      nbPoints = 1;
    }
    PossibleActions result = new PossibleActions();
    result.add(new ActionScorePoints(nbPoints));
    return result;
  }

  @Override
  public Message getDescription() {
    return new Message.MultilineText("actionBlue2", 0.7);
  }
}
