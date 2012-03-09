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

/**
 * A specific leader card.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public enum LeaderCard {
  RELIGIOUS,
  POLITIC,
  ECONOMIC,
  CULTURAL_TWO_THREE,
  CULTURAL_FOUR_FIVE,
  CITADEL;

  public static final LeaderCard[] THREE_PLAYERS =
      new LeaderCard[]{POLITIC, ECONOMIC, CULTURAL_TWO_THREE, CITADEL};

  public static final LeaderCard[] FOUR_FIVE_PLAYERS =
      new LeaderCard[]{RELIGIOUS, POLITIC, ECONOMIC, CULTURAL_FOUR_FIVE, CITADEL};

  private static final int[] POSITION_INDEX = {0, 1, 2, 3, 3, 4};

  /**
   * Get the index of the position of this leader card in the leader card array.
   * @return The index.
   */
  public int getPositionIndex() {
    return POSITION_INDEX[ordinal()];
  }

  /**
   * Returns how many points the cultural leader yields when placing a given star token.
   *
   * @param culturalLeader The desired cultural leader.
   * @param nbStars The number of stars on the token.
   * @return The score for placing that token.
   */
  public static int getPointsForCultural(LeaderCard culturalLeader, int nbStars) {
    assert culturalLeader == CULTURAL_TWO_THREE || culturalLeader == CULTURAL_FOUR_FIVE;
    if (nbStars == 0) {
      return 0;
    }
    if (culturalLeader == CULTURAL_TWO_THREE) {
      return nbStars;
    } else {
      return 1 + nbStars;
    }
  }

  /**
   * Checks if this is a cultural leader, either for 3 or 4-5 players.
   * @return True if this is a cultural leader.
   */
  public boolean isCultural() {
    return this == CULTURAL_TWO_THREE || this == CULTURAL_FOUR_FIVE;
  }
}