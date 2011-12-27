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

/**
 * This class tracks a list of sprites. It contains method to automatically snap tiles to the grid.
 * This class should have only logic, no GWT-specific code, so it's easily testable.
 *
 * @author Philippe Beaudoin
 */
public class SpriteList {
  final ArrayList<TileSprite> sprites = new ArrayList<TileSprite>();

  /**
   * Adds a sprite that will be rendered.
   *
   * @param sprite The Sprite to add.
   */
  public void add(TileSprite sprite) {
    sprites.add(sprite);
  }

  /**
   * Ensures that the specified sprite is rendered last so it appears on top of all other sprites.
   * Will only work if the provided {@code sprite} is in the list, otherwise it has no effect.
   * @param sprite The sprite to render in front of all others.
   */
  public void sendToFront(TileSprite sprite) {
    if (sprites.remove(sprite)) {
      sprites.add(sprite);
    }
  }

  /**
   * Renders all the sprites contained in the list.
   *
   * @param context The canvas context into which to render.
   */
  public void render(Context2d context) {
    for (TileSprite sprite : sprites) {
      sprite.render(context);
    }
  }
}
