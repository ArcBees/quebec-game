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
import com.philbeaudoin.quebec.shared.Board;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

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
   * Sets the position of a tile sprite so that it fits at the correct location on the board.
   * @param sprite
   * @param loc Both coordinates should be integer valued.
   */
  public void snapTileSprite(TileSprite sprite, Vector2d loc) {
    sprite.setPos(0.125 + loc.getX() * 0.0342, 0.18 + loc.getY() * 0.0592);
    sprite.setAngle(Board.rotationAngleForLocation(loc.getColumn(), loc.getLine()));
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
