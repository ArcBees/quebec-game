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

package com.philbeaudoin.quebec.shared.message;

/**
 * Various locations on the board.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public enum BoardLocation {
  NONE,
  TOP_CENTER,
  CENTER,
  PLAYER_AREAS,
  SCORE,
  BLACK_ARCHITECT_ON_PLAYER_AREA,
  BLACK_PASSIVE_CUBES,
  BLACK_ACTIVE_CUBES,
  RELIGIOUS_LEADER,
  POLITIC_LEADER,
  ECONOMIC_LEADER,
  CULTURAL_TWO_THREE_LEADER,
  CULTURAL_FOUR_FIVE_LEADER,
  CITADEL_LEADER,

  TOP_OF_TARGET_NEAR,
  TOP_RIGHT_OF_TARGET_NEAR,
  RIGHT_OF_TARGET_NEAR,
  BOTTOM_RIGHT_OF_TARGET_NEAR,
  BOTTOM_OF_TARGET_NEAR,
  BOTTOM_LEFT_OF_TARGET_NEAR,
  LEFT_OF_TARGET_NEAR,
  TOP_LEFT_OF_TARGET_NEAR,

  TOP_OF_TARGET,
  TOP_RIGHT_OF_TARGET,
  RIGHT_OF_TARGET,
  BOTTOM_RIGHT_OF_TARGET,
  BOTTOM_OF_TARGET,
  BOTTOM_LEFT_OF_TARGET,
  LEFT_OF_TARGET,
  TOP_LEFT_OF_TARGET
}
