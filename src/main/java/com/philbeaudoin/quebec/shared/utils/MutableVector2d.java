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

/**
 * A 2-dimensional vector that can be modified.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class MutableVector2d extends Vector2d {

  public MutableVector2d() {
    super();
  }

  public MutableVector2d(double x, double y) {
    super(x,y);
  }

  public MutableVector2d(Vector2d v) {
    super(v);
  }

  public void set(double x, double y) {
    this.setX(x);
    this.setY(y);
  }

  public void set(Vector2d v) {
    this.set(v.getX(), v.getY());
  }

  public void setX(double x) {
    this.x = x;
  }

  public void setY(double y) {
    this.y = y;
  }
}
