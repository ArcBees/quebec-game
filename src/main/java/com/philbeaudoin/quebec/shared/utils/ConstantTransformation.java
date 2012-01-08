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
 * An immutable 2-dimension transformation that doesn't depend on time.
 *
 * @author beaudoin
 */
public class ConstantTransformation implements Transformation {
  protected final MutableVector2d translation;
  protected double scaling;
  protected double rotation;

  public ConstantTransformation() {
    this.translation = new MutableVector2d();
    this.scaling = 1.0;
    this.rotation = 0;
  }

  public ConstantTransformation(Transformation transformation) {
    this.translation = new MutableVector2d(transformation.getTranslation(0));
    this.scaling = transformation.getScaling(0);
    this.rotation = transformation.getRotation(0);
  }

  public ConstantTransformation(Vector2d translation) {
    this.translation = new MutableVector2d(translation);
    this.scaling = 1.0;
    this.rotation = 0;
  }

  public ConstantTransformation(Vector2d translation, double sizeFactor, double angle) {
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
    applies(time, context, 1);
  }

  @Override
  public void applies(double time, Context2d context, double sizeFactor) {
    double totalScaling = scaling * sizeFactor;
    context.translate(translation.x, translation.y);
    context.scale(totalScaling, totalScaling);
    context.rotate(rotation);
  }
}