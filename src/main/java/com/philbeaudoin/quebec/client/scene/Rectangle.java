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

package com.philbeaudoin.quebec.client.scene;

import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;

/**
 * Scene tree node drawing a rectangle with a vertical color gradient and optionally a contour
 * stroke.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class Rectangle extends SceneNodeImpl {

  private final double x0;
  private final double y0;
  private final double x1;
  private final double y1;
  private final double w;
  private final double h;
  private final String color0;
  private final String color1;
  private final String strokeColor;
  private final double strokeWidth;
  private final double ox0;
  private final double oy0;
  private final double ow;
  private final double oh;

  /**
   * Creates a scene tree node drawing a rectangle with a vertical color gradient and
   * optionally a contour stroke.
   * @param x0 Top-left X coordinate.
   * @param y0 Top-left Y coordinate.
   * @param x1 Bottom-right X coordinate.
   * @param y1 Bottom-right Y coordinate.
   * @param color0 Top CSS color of the gradient.
   * @param color1 Bottom CSS color of the gradient.
   * @param strokeColor CSS color of the contour stroke, if {@code null} contour is omitted.
   * @param strokeWidth The width of the contour stroke, if 0 contour is omitted.
   */
  public Rectangle(double x0, double y0, double x1, double y1, String color0, String color1,
      String strokeColor, double strokeWidth) {
    this(x0, y0, x1, y1, color0, color1, strokeColor, strokeWidth, true);
  }

  private Rectangle(double x0, double y0, double x1, double y1, String color0, String color1,
      String strokeColor, double strokeWidth, boolean visible) {
    super(new ConstantTransform(), visible);
    this.x0 = x0;
    this.y0 = y0;
    this.x1 = x1;
    this.y1 = y1;
    this.w = x1 - x0;
    this.h = y1 - y0;
    this.color0 = color0;
    this.color1 = color1;
    this.strokeColor = strokeColor;
    this.strokeWidth = strokeWidth * 0.001;
    double offset = this.strokeWidth / 2.0;
    this.ox0 = x0 + offset;
    this.oy0 = y0 + offset;
    this.ow = w - this.strokeWidth;
    this.oh = h - this.strokeWidth;
  }

  @Override
  public void drawUntransformed(double time, Context2d context) {
    CanvasGradient gradient = context.createLinearGradient(x0, y0, x0, y1);
    gradient.addColorStop(0, color0);
    gradient.addColorStop(1, color1);
    context.setFillStyle(gradient);
    context.fillRect(x0, y0, w, h);
    if (strokeColor != null && strokeWidth > 0) {
      context.setStrokeStyle(strokeColor);
      context.setLineWidth(strokeWidth);
      context.strokeRect(ox0, oy0, ow, oh);
    }
  }

  @Override
  public SceneNode deepClone() {
    return new Rectangle(x0, y0, x1, y1, color0, color1, strokeColor, strokeWidth, isVisible());
  }

}
