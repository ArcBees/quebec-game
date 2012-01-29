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

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * An immutable 2-dimension transform that doesn't depend on time.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ConstantTransform implements Transform {
  protected final MutableVector2d translation;
  protected double scaling;
  protected double rotation;

  public ConstantTransform() {
    this.translation = new MutableVector2d();
    this.scaling = 1.0;
    this.rotation = 0;
  }

  public ConstantTransform(Transform transform) {
    this.translation = new MutableVector2d(transform.getTranslation(0));
    this.scaling = transform.getScaling(0);
    this.rotation = transform.getRotation(0);
  }

  public ConstantTransform(Vector2d translation) {
    this.translation = new MutableVector2d(translation);
    this.scaling = 1.0;
    this.rotation = 0;
  }

  public ConstantTransform(Vector2d translation, double sizeFactor, double angle) {
    this.translation = new MutableVector2d(translation);
    this.scaling = sizeFactor;
    this.rotation = angle;
  }

  /**
   * Returns the translation component of the transform.
   * @return The translation component.
   */
  public Vector2d getTranslation() {
    return translation;
  }

  /**
   * Returns the scaling factor of that transform.
   * @return The scaling factor.
   */
  public double getScaling() {
    return scaling;
  }

  /**
   * Returns the clockwise rotation angle of that transform.
   * @return The angle, in radians.
   */
  public double getRotation() {
    return rotation;
  }

  @Override
  public Vector2d getTranslation(double time) {
    return translation;
  }

  @Override
  public double getScaling(double time) {
    return scaling;
  }

  @Override
  public double getRotation(double time) {
    return rotation;
  }

  @Override
  public void applies(double time, Context2d context) {
    context.translate(translation.x, translation.y);
    context.scale(scaling, scaling);
    context.rotate(rotation);
  }

  @Override
  public ConstantTransform eval(double time) {
    return this;
  }

  /**
   * Multiplies this transform by {@code other}, yielding a final transform that consists
   * of first applying {@code other} then {@code this}.
   * @param other The transform to multiply with.
   * @return The result of the multiplication.
   */
  public ConstantTransform times(Transform other) {
    Vector2d otherTranslation = other.getTranslation(0);
    double otherScaling = other.getScaling(0);
    double otherRotation = other.getRotation(0);
    double totalScaling = scaling * otherScaling;
    double totalRotation = rotation + otherRotation;
    double cos = Math.cos(rotation);
    double sin = Math.sin(rotation);
    double ox = otherTranslation.x;
    double oy = otherTranslation.y;
    Vector2d totalTranslation = new Vector2d(translation.getX() + (cos * ox - sin * oy) * scaling,
        translation.getY() + (sin * ox + cos * oy) * scaling);
    return new ConstantTransform(totalTranslation, totalScaling, totalRotation);
  }

  /**
   * Inverse this transform.
   * @return The inverted transform.
   */
  public ConstantTransform inverse() {
    double cos = Math.cos(rotation);
    double sin = Math.sin(rotation);
    double tx = translation.x / scaling;
    double ty = translation.y / scaling;
    return new ConstantTransform(new Vector2d(-cos * tx - sin * ty, sin * tx - cos * ty),
        1.0 / scaling, -rotation);
  }

  @Override
  public boolean isAnimationCompleted(double time) {
    return true;
  }
}