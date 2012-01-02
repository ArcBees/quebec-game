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

package com.philbeaudoin.quebec.client.sprites;

import java.util.ArrayList;

import com.google.gwt.canvas.dom.client.Context2d;
import com.philbeaudoin.quebec.shared.utils.MutableTransformation;
import com.philbeaudoin.quebec.shared.utils.Transformation;

/**
 * This class tracks a list of renderables. This class should have only logic, no GWT-specific code,
 * so it's easily testable.
 *
 * @author Philippe Beaudoin
 */
public class RenderableList implements Renderable {
  final ArrayList<Renderable> renderables = new ArrayList<Renderable>();
  private final MutableTransformation transformation;

  public RenderableList() {
    transformation = new MutableTransformation();
  }

  public RenderableList(Transformation transformation) {
    this.transformation = new MutableTransformation(transformation);
  }

  /**
   * Sets the transformation of the sprite.
   * @param transformation The desired transformation.
   */
  public void setTransformation(Transformation transformation) {
    this.transformation.set(transformation);
  }

  /**
   * Returns the transformation affecting the sprite.
   * @return The transformation.
   */
  public Transformation getTransformation() {
    return transformation;
  }

  /**
   * Adds a renderable.
   * @param renderable The renderable to add.
   */
  public void add(Renderable renderable) {
    renderables.add(renderable);
  }

  /**
   * Ensures that the specified renderable is rendered last so it appears on top of all other
   * renderables. Will only work if the provided {@code renderable} is in the list, otherwise it has
   * no effect.
   * @param renderable The renderable to render in front of all others.
   */
  public void sendToFront(Renderable renderable) {
    if (renderables.remove(renderable)) {
      renderables.add(renderable);
    }
  }

  /**
   * Renders all the renderables contained in the list.
   * @param context The canvas context into which to render.
   */
  @Override
  public void render(Context2d context) {
    context.save();
    try {
      transformation.applies(context);
      for (Renderable renderable : renderables) {
        renderable.render(context);
      }
    } finally {
      context.restore();
    }
  }
}
