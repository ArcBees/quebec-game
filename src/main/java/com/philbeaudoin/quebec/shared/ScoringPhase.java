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

/**
 * The various scoring phases.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public enum ScoringPhase {
  SCORE_ZONE_1,
  SCORE_ZONE_2,
  SCORE_ZONE_3,
  SCORE_ZONE_4,
  SCORE_ZONE_5,
  SCORE_INCOMPLETE_BUILDINGS,
  SCORE_ACTIVE_CUBES,
  SCORE_BUILDINGS,
  FINISH_GAME,
  PREPARE_NEXT_CENTURY,
  INIT_SCORING;

  public static final ScoringPhase[] ZONE_SCORING_PHASES =
      new ScoringPhase[]{SCORE_ZONE_1, SCORE_ZONE_2, SCORE_ZONE_3, SCORE_ZONE_4, SCORE_ZONE_5};

  /**
   * Checks whether this scoring phase is an influence zone scoring phase.
   * @return True if it's an influence zone scoring phase, false otherwise.
   */
  public boolean isZoneScoringPhase() {
    return ordinal() <= 4;
  }

  /**
   * Returns the index of the zone to score, 0 being the first zone of the century and 4 being
   * the last one. Returns -1 if this is not a zone scoring phase.
   * @return The index of the zone to score, or -1 if this is not a zone scoring phase.
   */
  public int scoringZoneIndex() {
    if (isZoneScoringPhase()) {
      return ordinal();
    }
    return -1;
  }

  /**
   * Finds the next scoring phase given a specific century.
   * @param century The century for which to find the next scoring phase.
   * @return The next scoring phase.
   */
  public ScoringPhase nextScoringPhase(int century) {
    assert this != FINISH_GAME && this != PREPARE_NEXT_CENTURY;
    // TODO(beaudoin): Tried to use values()[ordinal() + 1] here but ran into GWT Issue 7607.
    switch(this) {
    case SCORE_ZONE_1:
      return SCORE_ZONE_2;
    case SCORE_ZONE_2:
      return SCORE_ZONE_3;
    case SCORE_ZONE_3:
      return SCORE_ZONE_4;
    case SCORE_ZONE_4:
      return SCORE_ZONE_5;
    case SCORE_ZONE_5:
      if (century == 3) {
        return SCORE_INCOMPLETE_BUILDINGS;
      } else {
        return PREPARE_NEXT_CENTURY;
      }
    case SCORE_INCOMPLETE_BUILDINGS:
      return SCORE_ACTIVE_CUBES;
    case SCORE_ACTIVE_CUBES:
      return SCORE_BUILDINGS;
    case SCORE_BUILDINGS:
      return FINISH_GAME;
    case INIT_SCORING:
      return SCORE_ZONE_1;
    default:
      assert false;
      return null;
    }
  }
}
