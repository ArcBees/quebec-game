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

package com.philbeaudoin.quebec.shared.location;

import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Identifies a location relative to another one.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class LocationRelative implements Location {

  private final Location target;
  private final Vector2d relativePosition;

  /**
  * Creates a location relative to a given target. The relative position is continuous, but you can
  * uses this scale to guide you:
  * <ul>
  * <li>-3 : left (or above) the target, far.
  * <li>-2 : left (or above) the target, normal.
  * <li>-1 : left (or above) the target, near.
  * <li> 0 : on target.
  * <li> 1 : right (or below) the target, near.
  * <li> 2 : right (or below) the target, normal.
  * <li> 3 : right (or below) the target, far.
  * </ul>
  * @param target The location relative to which we position ourselves.
  * @param relativePosition The position relative to this target.
  */
  public LocationRelative(Location target, Vector2d relativePosition) {
    this.target = target;
    this.relativePosition = relativePosition;
  }

  /**
   * Accepts a location visitor.
   * @param visitor The visitor.
   */
  public <T> T accept(LocationVisitor<T> visitor) {
    return visitor.visit(this);
  }

  public Location getTarget() {
    return target;
  }

  public Vector2d getRelativePosition() {
    return relativePosition;
  }
}
