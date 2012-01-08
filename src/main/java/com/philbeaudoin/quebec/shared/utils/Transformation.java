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

public interface Transformation {

  /**
   * Returns the translation component of the transformation.
   * @param time The time at which to get the transformation.
   * @return The translation component.
   */
  public abstract Vector2d getTranslation(double time);

  /**
   * Returns the scaling factor of that transformation.
   * @param time The time at which to get the transformation.
   * @return The scaling factor.
   */
  public abstract double getScaling(double time);

  /**
   * Returns the clockwise rotation angle of that transformation.
   * @param time The time at which to get the transformation.
   * @return The angle, in radians.
   */
  public abstract double getRotation(double time);

  /**
   * Applies the transformation.
   * @param time The time at which to apply the transformation.
   * @param context The canvas context into which to render.
   */
  public abstract void applies(double time, Context2d context);

  /**
   * Applies the transformation with an extra scaling factor.
   * @param time The time at which to apply the transformation.
   * @param context The canvas context into which to render.
   * @param sizeFactor An extra scaling factor to use.
   */
  public abstract void applies(double time, Context2d context, double sizeFactor);

}
