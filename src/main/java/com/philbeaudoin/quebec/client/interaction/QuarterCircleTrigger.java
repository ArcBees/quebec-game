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

package com.philbeaudoin.quebec.client.interaction;

import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * A trigger that is triggered when a location is within a given quarter of a circle.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class QuarterCircleTrigger implements Trigger {

  private final CircleTrigger circleTrigger;
  private final int quadrantX;
  private final int quadrantY;

  QuarterCircleTrigger(Vector2d center, double radius, int quadrantX, int quadrantY) {
    circleTrigger = new CircleTrigger(center, radius);
    this.quadrantX = quadrantX;
    this.quadrantY = quadrantY;
  }

  @Override
  public boolean triggerAt(double x, double y) {
    if ((x - circleTrigger.getCenterX()) * quadrantX >= 0 &&
        (y - circleTrigger.getCenterY()) * quadrantY >= 0) {
      return circleTrigger.triggerAt(x, y);
    } else {
      return false;
    }
  }

}
