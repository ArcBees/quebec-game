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

package com.philbeaudoin.quebec.shared;

import com.philbeaudoin.quebec.shared.statechange.CubeDestination;

/**
 * Information on a specific zone scoring and cascade for a given zone, for each player.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ZoneScoringInformation {
  private final int scoringZoneIndex;
  private final InfluenceType zoneToScore;
  private final int scorePerPlayer[] = new int[5];
  private final int cascadePerPlayer[] = new int[5];
  private final CubeDestination[] origin = new CubeDestination[5];
  private final CubeDestination[] destination = new CubeDestination[5];

  public ZoneScoringInformation(int scoringZoneIndex, InfluenceType zoneToScore) {
    this.scoringZoneIndex = scoringZoneIndex;
    this.zoneToScore = zoneToScore;
  }

  public void setScore(PlayerColor playerColor, int score) {
    scorePerPlayer[playerColor.normalColorIndex()] = score;
  }

  public void setCubesToCascade(PlayerColor playerColor, int nbCubesToCascade) {
    cascadePerPlayer[playerColor.normalColorIndex()] = nbCubesToCascade;
  }

  public void setOrigin(PlayerColor playerColor, CubeDestination cubeDestination) {
    origin[playerColor.normalColorIndex()] = cubeDestination;
  }

  public void setDestination(PlayerColor playerColor, CubeDestination cubeDestination) {
    destination[playerColor.normalColorIndex()] = cubeDestination;
  }

  public int getScoringZoneIndex() {
    return scoringZoneIndex;
  }

  public InfluenceType getZoneToScore() {
    return zoneToScore;
  }

  public int getScore(PlayerColor playerColor) {
    return scorePerPlayer[playerColor.normalColorIndex()];
  }

  public int getCubesToCascade(PlayerColor playerColor) {
    return cascadePerPlayer[playerColor.normalColorIndex()];
  }

  public CubeDestination getOrigin(PlayerColor playerColor) {
    return origin[playerColor.normalColorIndex()];
  }

  public CubeDestination getDestination(PlayerColor playerColor) {
    return destination[playerColor.normalColorIndex()];
  }
}