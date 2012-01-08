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

import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.philbeaudoin.quebec.client.utils.CubeGrid;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.utils.MutableTransformation;
import com.philbeaudoin.quebec.shared.utils.ConstantTransformation;
import com.philbeaudoin.quebec.shared.utils.Transformation;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * A renderable that contains all the elements representing the state of a given player.
 *
 * @author beaudoin
 */
public class PlayerZone implements Renderable {
  private static final String COLOR[][] = {
      {"#666", "#999"},
      {"#E0E0E0", "#F8F8F8"},
      {"#DDD", "#EEE"},
      {"#DDD", "#EEE"},
      {"#DDD", "#EEE"}
  };

  private final RenderableList renderables = new RenderableList();
  private final CubeGrid cubeGrid = new CubeGrid(9, 3);
  private final MutableTransformation transformation;
  private final PlayerColor playerColor;
  private final String playerName;
  private final double sizeX;
  private final double sizeY;

  public PlayerZone(PlayerColor playerColor, String playerName, double sizeX, double sizeY,
      ConstantTransformation transformation) {
    assert playerColor != PlayerColor.NONE;
    this.transformation = new MutableTransformation(transformation);
    this.playerColor = playerColor;
    this.playerName = playerName;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
  }

  public void generateCubes(SpriteResources spriteResources) {
    RenderableList inactive = new RenderableList(
        new ConstantTransformation(new Vector2d(sizeX * 0.16, sizeY * 0.6)));
    renderables.add(inactive);
    RenderableList active = new RenderableList(
        new ConstantTransformation(new Vector2d(sizeX * 0.4, sizeY * 0.6)));
    renderables.add(active);
    for (int i = 0; i < 9; ++i) {
      for (int j = 0; j < 3; ++j) {
        Sprite cube = new Sprite(spriteResources.getCube(playerColor),
            new ConstantTransformation(cubeGrid.getPosition(i, j)));
        inactive.add(cube);
        cube = new Sprite(spriteResources.getCube(playerColor),
            new ConstantTransformation(cubeGrid.getPosition(i, j)));
        active.add(cube);
      }
    }

    Sprite card = new Sprite(spriteResources.getLeader(InfluenceType.CITADEL),
        new ConstantTransformation(new Vector2d(sizeX * 0.8, sizeY * 0.5)));
    renderables.add(card);
  }

  /**
   * Sets the transformation of the player zone.
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

  @Override
  public void render(double time, Context2d context) {
    context.save();
    try {
      transformation.applies(time, context);
      CanvasGradient gradient = context.createLinearGradient(0, 0, 0, sizeY);
      int index = playerColor.ordinal() - 1;
      gradient.addColorStop(0, COLOR[index][0]);
      gradient.addColorStop(1, COLOR[index][1]);
      context.setFillStyle(gradient);
      context.fillRect(0, 0, sizeX, sizeY);
      double x0 = 0.3 * sizeX, x1 = 0.5 * sizeX, y0 = 0.28 * sizeY, y1 = 0.9 * sizeY;
      gradient = context.createLinearGradient(0, y0, 0, y1);
      gradient.addColorStop(0, COLOR[index][1]);
      gradient.addColorStop(1, COLOR[index][0]);
      context.setFillStyle(gradient);
      context.setLineWidth(0.001);
      context.fillRect(x0, y0, x1 - x0, y1 - y0);
      context.strokeRect(x0, y0, x1 - x0, y1 - y0);

      context.setFillStyle("#000");
      context.save();
      try {
        context.scale(0.001, 0.001);
        context.setFont("25px arial");
        context.fillText(playerName, 12, 30);
      } finally {
        context.restore();
      }
      renderables.render(time, context);
    } finally {
      context.restore();
    }
  }

}
