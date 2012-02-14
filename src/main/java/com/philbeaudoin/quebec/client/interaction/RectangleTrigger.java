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
 * A trigger that is triggered when a location is within a given rectangle.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class RectangleTrigger implements Trigger {

  private final double minX;
  private final double minY;
  private final double maxX;
  private final double maxY;

  RectangleTrigger(Vector2d center, double width, double height) {
    minX = center.getX() - width / 2.0;
    minY = center.getY() - height / 2.0;
    maxX = minX + width;
    maxY = minY + height;
  }

  @Override
  public boolean triggerAt(double x, double y) {
    return minX <= x && x <= maxX && minY <= y && y <= maxY;
  }
}
