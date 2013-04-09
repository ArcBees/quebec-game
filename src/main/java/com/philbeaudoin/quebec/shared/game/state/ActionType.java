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

/**
 * The various possible types of actions in the circles on the board.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public enum ActionType {
  PURPLE_ANY,
  PURPLE_ONE_TO_CITADEL_ONE_TO_ANY,
  PURPLE_ONE_POINT_ONE_TO_ANY_ACTIVATE_ONE,
  PURPLE_ONE_TO_ANY_MOVE_TWO,
  RED_ANY,
  RED_TWO_TO_PURPLE_OR_YELLOW,
  RED_TWO_TO_RED_OR_BLUE,
  RED_TWO_TO_CITADEL,
  YELLOW_ANY,
  YELLOW_MOVE_ARCHITECT,
  YELLOW_FILL_ONE_SPOT,
  YELLOW_ACTIVATE_THREE,
  BLUE_ANY,
  BLUE_SCORE_FOR_CUBES_IN_HAND,
  BLUE_SCORE_FOR_ZONES,
  BLUE_ADD_STAR;
}
