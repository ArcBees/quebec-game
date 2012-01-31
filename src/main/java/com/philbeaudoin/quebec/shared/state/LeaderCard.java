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

/**
 * A specific leader card. This information never changes during the game.
 * TODO: Refactor into an enum.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class LeaderCard {
  private final InfluenceType influenceType;

  public LeaderCard(InfluenceType influenceType) {
    this.influenceType = influenceType;
  }

  /**
   * Access the influence type of that leader card.
   * @return The influence type.
   */
  public InfluenceType getInfluenceType() {
    return influenceType;
  }
}