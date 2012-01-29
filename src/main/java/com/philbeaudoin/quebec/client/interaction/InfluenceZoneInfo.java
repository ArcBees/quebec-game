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

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Various graphical information about various an influence zone. Can also act as a trigger.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InfluenceZoneInfo implements Trigger {

  private final Vector2d spriteCenter;
  private final Vector2d arrowCenter;
  private final Trigger trigger;

  InfluenceZoneInfo(InfluenceType influenceZone) {
    if (influenceZone != InfluenceType.CITADEL) {
      int index = influenceZone.ordinal();
      int quadrantX = ((index + 1) % 4 / 2 == 0) ? 1 : -1;
      int quadrantY = (index / 2 == 0) ? 1 : -1;
      Vector2d corner = new Vector2d(1.0385 + 0.5845 * quadrantX, 0.4985 + 0.4345 * quadrantY);
      trigger = new QuarterCircleTrigger(corner, 0.2, -quadrantX, -quadrantY);
      arrowCenter = new Vector2d(corner.getX() - quadrantX * 0.07, corner.getY() - quadrantY * 0.07);
      spriteCenter = new Vector2d(corner.getX() - quadrantX * 0.09, corner.getY() - quadrantY * 0.09);
    } else {
      arrowCenter = new Vector2d(1.5, 0.47);
      trigger = new CircleTrigger(arrowCenter, 0.138);
      spriteCenter = new Vector2d(1.495, 0.473);
    }
  }

  @Override
  public boolean triggerAt(double x, double y) {
    return trigger.triggerAt(x, y);
  }

  /**
   * Access the point at which the arrow should be pointing.
   * @return The point at which the arrow should be pointing.
   */
  public Vector2d getArrowCenter() {
    return arrowCenter;
  }

  /**
   * Access the point that should be used to translate the sprite.
   * @return The point to use as translation for the sprite.
   */
  public Vector2d getSpriteCenter() {
    return spriteCenter;
  }
}
