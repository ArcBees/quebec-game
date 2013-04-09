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

package com.philbeaudoin.quebec.shared.game.action;

import com.philbeaudoin.quebec.shared.InfluenceType;

/**
 * A possible action that has a specific influence zone as a destination.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface GameActionOnInfluenceZone extends GameAction, HasInfluenceZone {
  /**
   * Access the influence zone on which this action operates.
   * @return The influence zone.
   */
  InfluenceType getInfluenceZone();
}
