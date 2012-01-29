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

package com.philbeaudoin.quebec.shared.utils;

/**
 * The interface for a transform that can be used to generate animations.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public abstract class AnimTransform implements Transform {

  protected double t0;
  protected double t1;

  public AnimTransform(double t0, double t1) {
    this.t0 = t0;
    this.t1 = t1;
  }

  /**
   * Sets the starting and ending time of the animation.
   * @param t0 The starting time.
   * @param t1 The ending time.
   */
  public void setTimes(double t0, double t1) {
    this.t0 = t0;
    this.t1 = t1;
  }

  @Override
  public boolean isAnimationCompleted(double time) {
    return time >= t1;
  }
}
