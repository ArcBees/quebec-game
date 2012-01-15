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

/**
 * A 2-dimensional transform that can be modified.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class MutableTransform extends ConstantTransform {

  public MutableTransform() {
    super();
  }

  public MutableTransform(Transform transform) {
    super(transform);
  }

  public MutableTransform(Vector2d translation, double sizeFactor, double angle) {
    super(translation, sizeFactor, angle);
  }

  /**
   * Sets the translation of the transform.
   * @param x The desired X translation.
   * @param y The desired Y translation.
   */
  public void setTranslation(double x, double y) {
    translation.set(x, y);
  }

  /**
   * Sets the translation component of the transform.
   * @param translation The desired translation.
   */
  public void setTranslation(Vector2d translation) {
    this.translation.set(translation);
  }

  /**
   * Sets the relative size of the transform. A size factor of 1 means no scaling is applied.
   * @param scaling The desired scaling factor.
   */
  public void setScaling(double scaling) {
    this.scaling = scaling;
  }

  /**
   * Sets the rotation angle of the transform.
   * @param rotation The desired rotation angle, in radians.
   */
  public void setRotation(double rotation) {
    this.rotation = rotation;
  }

  /**
   * Copies the specified transform.
   * @param transform The transform to copy.
   */
  public void set(Transform transform) {
    setTranslation(transform.getTranslation(0));
    setScaling(transform.getScaling(0));
    setRotation(transform.getRotation(0));
  }
}
