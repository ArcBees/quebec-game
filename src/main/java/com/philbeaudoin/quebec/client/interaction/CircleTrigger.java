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

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * A trigger that is triggered when a location is within a given circle.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class CircleTrigger implements Trigger {

  private final double centerX;
  private final double centerY;
  private final double radius2;

  @Inject
  CircleTrigger(@Assisted Vector2d center, @Assisted double radius) {
    this.centerX = center.getX();
    this.centerY = center.getY();
    this.radius2 = radius * radius;
  }

  @Override
  public boolean triggerAt(double x, double y) {
    double dx = x - centerX;
    double dy = y - centerY;
    return dx * dx + dy * dy < radius2;
  }
}
