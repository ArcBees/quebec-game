/**
 * Copyright 2011 Philippe Beaudoin
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

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * A transform that can be applied at any time.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface Transform {

  /**
   * Returns the translation component of the transform.
   * @param time The time at which to get the transform.
   * @return The translation component.
   */
  Vector2d getTranslation(double time);

  /**
   * Returns the scaling factor of that transform.
   * @param time The time at which to get the transform.
   * @return The scaling factor.
   */
  double getScaling(double time);

  /**
   * Returns the clockwise rotation angle of that transform.
   * @param time The time at which to get the transform.
   * @return The angle, in radians.
   */
  double getRotation(double time);

  /**
   * Applies the transform.
   * @param time The time at which to apply the transform.
   * @param context The canvas context into which to render.
   */
  void applies(double time, Context2d context);

  /**
   * Returns the constant transform corresponding to this transform evaluated at a given time.
   * @param time The time at which to evaluate this transform.
   * @return The constant transform at that time.
   */
  ConstantTransform eval(double time);

  /**
   * Checks whether the transform has completed running its animation at the provided time.
   * @param time The time at which to check whether the animation has completed.
   * @return True if the animation has completed at that time.
   */
  boolean isAnimationCompleted(double time);
}
