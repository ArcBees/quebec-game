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
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * Scene tree node drawing black text.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class Text extends SceneNodeImpl {

  private final String text;

  /**
   * Creates a scene tree node drawing black text.
   * @param text The text to print.
   * @param transform The transform to apply.
   */
  public Text(String text, Transform transform) {
    this(text, transform, true);
  }

  private Text(String text, Transform transform, boolean visible) {
    super(transform, visible);
    this.text = text;
  }

  @Override
  public void drawUntransformed(double time, Context2d context) {
    context.scale(0.001, 0.001);
    context.setFont("25px arial");
    context.fillText(text, 0, 0);
  }

  @Override
  public SceneNode deepClone() {
    return new Text(text, getTransform(), isVisible());
  }
}
