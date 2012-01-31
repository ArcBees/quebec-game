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

import java.util.List;

import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.LeaderCard;

/**
 * A leader card destination corresponding to the game board.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class LeaderDestinationBoard implements LeaderDestination {

  private final LeaderCard leaderCard;

  public LeaderDestinationBoard(LeaderCard leaderCard) {
    this.leaderCard = leaderCard;
  }

  @Override
  public LeaderCard getLeaderCard() {
    return leaderCard;
  }

  @Override
  public void removeFrom(GameState gameState) {
    List<LeaderCard> leaderCards = gameState.getAvailableLeaderCards();
    boolean removed = leaderCards.remove(leaderCard);
    assert removed;
  }

  @Override
  public void addTo(GameState gameState) {
    List<LeaderCard> leaderCards = gameState.getAvailableLeaderCards();
    assert !leaderCards.contains(leaderCard);
    leaderCards.add(leaderCard);
  }

  @Override
  public void accept(LeaderDestinationVisitor visitor) {
    visitor.visit(this);
  }
}
