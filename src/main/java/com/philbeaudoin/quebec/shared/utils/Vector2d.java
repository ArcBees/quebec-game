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

import java.io.Serializable;

/**
 * An immutable 2-dimensional vector.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class Vector2d implements Serializable {
  protected double x, y;

  public Vector2d() {
    this(0, 0);
  }

  public Vector2d(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public Vector2d(Vector2d v) {
    this(v.x, v.y);
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public int getColumn() {
    return (int) x;
  }

  public int getLine() {
    return (int) y;
  }

  @Override
  public boolean equals(Object that) {
    if (this == that) {
      return true;
    }
    if (!(that instanceof Vector2d)) {
      return false;
    }
    Vector2d other = (Vector2d) that;
    return x == other.x && y == other.y;
  }

  @Override
  public int hashCode() {
    return new Double(x).hashCode() ^ new Double(y).hashCode();
  }

  public double distanceTo(Vector2d to) {
    double dx = to.x - x;
    double dy = to.y - y;
    return Math.sqrt(dx * dx + dy * dy);
  }

  public Vector2d add(double dx, double dy) {
    return new Vector2d(x + dx, y + dy);
  }

  public Vector2d add(Vector2d d) {
    return new Vector2d(x + d.x, y + d.y);
  }
}
