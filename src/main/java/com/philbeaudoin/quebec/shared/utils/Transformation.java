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
 * An immutable 2-dimension transformation.
 *
 * @author Philippe Beaudoin
 */
public class Transformation {
  protected final MutableVector2d translation;
  protected double scaling;
  protected double rotation;

  public Transformation() {
    this.translation = new MutableVector2d();
    this.scaling = 1.0;
    this.rotation = 0;
  }

  public Transformation(Transformation transformation) {
    this.translation = new MutableVector2d(transformation.translation);
    this.scaling = transformation.scaling;
    this.rotation = transformation.rotation;
  }

  public Transformation(Vector2d translation) {
    this.translation = new MutableVector2d(translation);
    this.scaling = 1.0;
    this.rotation = 0;
  }

  public Transformation(Vector2d translation, double sizeFactor, double angle) {
    this.translation = new MutableVector2d(translation);
    this.scaling = sizeFactor;
    this.rotation = angle;
  }

  /**
   * Returns the translation component of the transformation.
   * @return The translation component.
   */
  public Vector2d getTranslation() {
    return translation;
  }

  /**
   * Returns the scaling factor of that transformation.
   * @return The scaling factor.
   */
  public double getScaling() {
    return scaling;
  }

  /**
   * Returns the clockwise rotation angle of that transformation.
   * @return The angle, in radians.
   */
  public double getRotation() {
    return rotation;
  }

  /**
   * Applies the transformation.
   * @param context The canvas context into which to apply the transformation.
   */
  public void applies(Context2d context) {
    applies(context, 1);
  }

  /**
   * Applies the transformation with an extra scaling factor.
   * @param context The canvas context into which to apply the transformation.
   * @param sizeFactor An extra scaling factor to use.
   */
  public void applies(Context2d context, double sizeFactor) {
    double totalScaling = scaling * sizeFactor;
    context.translate(translation.getX(), translation.getY());
    context.scale(totalScaling, totalScaling);
    context.rotate(rotation);
  }

  /**
   * Multiplies this transformation by {@code other}, yielding a final transformation that consists
   * of first applying {@code other} then {@code this}.
   * @param other The transformation to multiply with.
   * @return The result of the multiplication.
   */
  public Transformation times(Transformation other) {
    double totalRotation = rotation + other.rotation;
    double totalScaling = scaling * other.scaling;
    double cos = Math.cos(rotation);
    double sin = Math.sin(rotation);
    double ox = other.translation.getX();
    double oy = other.translation.getY();
    Vector2d totalTranslation = new Vector2d(translation.getX() + (cos * ox - sin * oy) * scaling,
        translation.getY() + (sin * ox + cos * oy) * scaling);
    return new Transformation(totalTranslation, totalScaling, totalRotation);
  }
}