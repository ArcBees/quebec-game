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

package com.philbeaudoin.quebec.shared.statechange;

import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.LeaderCard;

/**
 * A valid place to take or send leader cards. A destination implies a leader card.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface LeaderDestination {

  /**
   * Access the leader card implied by that destination.
   * @return The leader card implied by that destination.
   */
  LeaderCard getLeaderCard();

  /**
   * Remove leader card from that specific destination in the provided game state.
   * @param gameState The game state from which to remove cubes.
   */
  void removeFrom(GameState gameState);

  /**
   * Add leader card to this specific destination in the provided game state.
   * @param gameState The game state onto which to add cubes.
   */
  void addTo(GameState gameState);

  /**
   * Accepts a visitor.
   * @param visitor The visitor.
   */
  <T> T accept(LeaderDestinationVisitor<T> visitor);
}
