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
  CULTURAL,
  CITADEL;

  private static final int pointsForCultural[][] = {
    {1, 2, 3},
    {2, 3, 4}
  };

  /**
   * Returns how many points the cultural leader yields when placing a given star token.
   *
   * @param nbPlayers The number of players in the game.
   * @param nbStars The number of stars on the token.
   * @return The score for placing that token.
   */
  public static int getPointsForCultural(int nbPlayers, int nbStars) {
    assert nbPlayers >= 2 && nbPlayers <= 5 && nbStars >= 0 && nbStars <= 3;
    if (nbStars == 0) {
      return 0;
    }
    return pointsForCultural[(nbPlayers - 2) / 2][nbStars - 1];
  }
}