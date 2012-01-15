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

package com.philbeaudoin.quebec.client.scene;

import com.google.gwt.canvas.dom.client.Context2d;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Scene tree node drawing an arrow pointing from one point to another.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class Arrow extends SceneNodeImpl {
  private static final double CONTROL_POINT_DIST = 0.1;
  private static final double CONTROL_POINT_RATIO_1 = 0.33;
  private static final double CONTROL_POINT_RATIO_2 = 0.67;
  private static final double ARROW_DEPTH = 0.02;
  private static final double ARROW_SMALL_WIDTH = 0.005;
  private static final double ARROW_LARGE_WIDTH = 0.02;

  private final Vector2d from;
  private final Vector2d to;
  private final Vector2d p1;
  private final Vector2d p2;
  private final Vector2d p3;
  private final Vector2d p4;
  private final Vector2d p5;
  private final Vector2d p6;

  /**
   * Creates a scene tree node drawing an arrow pointing from one point to another.
   * @param from The starting point of the arrow.
   * @param to The ending point of the arrow.
   */
  public Arrow(Vector2d from, Vector2d to) {
    super();
    this.from = from;
    this.to = to;
    double dx = to.getX() - from.getX();
    double dy = to.getY() - from.getY();
    double px = dy;
    double py = -dx;
    if (py > 0) {
      px *= -1;
      py *= -1;
    }
    // Bezier control points
    p1 = new Vector2d(from.getX() + dx * CONTROL_POINT_RATIO_1 + px * CONTROL_POINT_DIST,
        from.getY() + dy * CONTROL_POINT_RATIO_1 + py * CONTROL_POINT_DIST);
    p2 = new Vector2d(from.getX() + dx * CONTROL_POINT_RATIO_2 + px * CONTROL_POINT_DIST,
        from.getY() + dy * CONTROL_POINT_RATIO_2 + py * CONTROL_POINT_DIST);
    // Arrow base
    double adx = to.getX() - p2.getX();
    double ady = to.getY() - p2.getY();
    double length = Math.sqrt(adx * adx + ady * ady);
    double ndx = adx / length;
    double ndy = ady / length;
    double npx = ndy;
    double npy = -ndx;
    p3 = new Vector2d(to.getX() - ndx * ARROW_DEPTH + npx * ARROW_SMALL_WIDTH,
        to.getY() - ndy * ARROW_DEPTH + npy * ARROW_SMALL_WIDTH);
    p4 = new Vector2d(to.getX() - ndx * ARROW_DEPTH + npx * ARROW_LARGE_WIDTH,
        to.getY() - ndy * ARROW_DEPTH + npy * ARROW_LARGE_WIDTH);
    p5 = new Vector2d(to.getX() - ndx * ARROW_DEPTH - npx * ARROW_LARGE_WIDTH,
        to.getY() - ndy * ARROW_DEPTH - npy * ARROW_LARGE_WIDTH);
    p6 = new Vector2d(to.getX() - ndx * ARROW_DEPTH - npx * ARROW_SMALL_WIDTH,
        to.getY() - ndy * ARROW_DEPTH - npy * ARROW_SMALL_WIDTH);
  }

  @Override
  public void draw(double time, Context2d context) {
    context.save();
    try {
      getTransform().applies(time, context);
      context.beginPath();
      context.moveTo(from.getX(), from.getY());
      context.bezierCurveTo(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
      context.lineTo(p4.getX(), p4.getY());
      context.lineTo(to.getX(), to.getY());
      context.lineTo(p5.getX(), p5.getY());
      context.lineTo(p6.getX(), p6.getY());
      context.bezierCurveTo(p2.getX(), p2.getY(), p1.getX(), p1.getY(), from.getX(), from.getY());
      context.stroke();
      context.fill();
    } finally {
      context.restore();
    }
  }

}