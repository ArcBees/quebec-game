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

import com.google.gwt.canvas.dom.client.Context2d;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Scene tree node drawing a triangular callout pointing from one point to another.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class Callout extends SceneNodeImpl {
  private static final double BASE_SIZE = 0.03;

  private final Vector2d from;
  private final Vector2d to;
  private final Vector2d p1;
  private final Vector2d p3;

  /**
   * Creates a scene tree node drawing an arrow pointing from one point to another.
   * @param from The starting point of the arrow.
   * @param to The ending point of the arrow.
   */
  public Callout(Vector2d from, Vector2d to) {
    this(from, to, true);
  }

  private Callout(Vector2d from, Vector2d to, boolean visible) {
    super(new ConstantTransform(), visible);
    this.from = from;
    this.to = to;
    double px = to.getY() - from.getY();
    double py = to.getX() - from.getX();
    double factor = 0.5 * BASE_SIZE / Math.sqrt(px * px + py * py);
    px *= factor;
    py *= factor;
    p1 = new Vector2d(from.getX() + px, from.getY() + py);
    p3 = new Vector2d(from.getX() - px, from.getY() - py);
  }

  @Override
  public void drawUntransformed(double time, Context2d context) {
    context.beginPath();
    context.moveTo(p1.getX(), p1.getY());
    context.lineTo(to.getX(), to.getY());
    context.lineTo(p3.getX(), p3.getY());
    context.lineTo(p1.getX(), p1.getY());
    context.setLineWidth(0.002);
    context.setStrokeStyle("#000");
    context.stroke();
    context.setFillStyle("#aaa");
    context.fill();
  }

  @Override
  public SceneNode deepClone() {
    return new Callout(from, to, isVisible());
  }
}