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
 * A transform that animates between two others using an arc and an ease-in/ease-out curve.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ArcTransform implements Transform {
  private final Transform start;
  private final Transform finish;
  private final double t0;
  private final double t1;

  public ArcTransform(Transform start, Transform finish, double t0, double t1) {
    this.start = start;
    this.finish = finish;
    this.t0 = t0;
    this.t1 = t1;
  }

  @Override
  public Vector2d getTranslation(double time) {
    Vector2d p0 = start.getTranslation(time);
    Vector2d p2 = finish.getTranslation(time);
    double dx = (p2.x - p0.x) / 2.0;
    double dy = (p2.y - p0.y) / 2.0;
    double px = dy / 4.0;
    double py = -dx / 4.0;
    if (py > 0) {
      px = -px;
      py = -py;
    }
    double p1x = p0.x + dx + px;
    double p1y = p0.y + dy + py;

    double r = ease(time);
    double r2 = r * r;
    double f0 = r2 - 2 * r + 1;
    double f1 = -2 * r2 + 2 * r;
    double f2 = r2;

    return new Vector2d(
        f0 * p0.x + f1 * p1x + f2 * p2.x,
        f0 * p0.y + f1 * p1y + f2 * p2.y);
  }

  @Override
  public double getScaling(double time) {
    double r = ease(time);
    return (1 - r) * start.getScaling(time) + r * finish.getScaling(time);
  }

  @Override
  public double getRotation(double time) {
    double r = ease(time);
    return (1 - r) * start.getRotation(time) + r * finish.getRotation(time);
  }

  @Override
  public void applies(double time, Context2d context) {
    applies(time, context, 1);
  }

  @Override
  public void applies(double time, Context2d context, double sizeFactor) {
    Vector2d translation = getTranslation(time);
    double totalScaling = getScaling(time) * sizeFactor;
    context.translate(translation.x, translation.y);
    context.scale(totalScaling, totalScaling);
    context.rotate(getRotation(time));
  }

  @Override
  public ConstantTransform eval(double time) {
    // TODO Speed up by refactoring to call ease() only once.
    return new ConstantTransform(
        getTranslation(time),
        getScaling(time),
        getRotation(time));
  }

  private double ease(double time) {
    double r = (time - t0) / (t1 - t0);
    if (r < 0) {
      r = 0;
    } else if (r < 0.5) {
      r = Math.pow(r, 1.8) * 1.74110113;
    } else if (r < 1) {
      r = 1 - r;
      r = Math.pow(r, 1.8) * 1.74110113;
      r = 1 - r;
    } else {
      r = 1;
    }
    return r;
  }
}
