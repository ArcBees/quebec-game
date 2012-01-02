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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.philbeaudoin.quebec.client.utils.LoggerFactory;
import com.philbeaudoin.quebec.shared.utils.MutableTransformation;
import com.philbeaudoin.quebec.shared.utils.Transformation;

/**
 * A sprite that can be transformed and rendered into an HTML5 2d canvas.
 * @author beaudoin
 */
public class Sprite implements Renderable {
  final Logger logger;

  protected final SpriteResources.Info info;
  private final MutableTransformation transformation;

  public Sprite(SpriteResources.Info info) {
    logger = LoggerFactory.get(Sprite.class);
    this.info = info;
    transformation = new MutableTransformation();
  }

  public Sprite(SpriteResources.Info info, Transformation transformation) {
    logger = LoggerFactory.get(Sprite.class);
    this.info = info;
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

  @Override
  public void render(Context2d context) {
    if (info == null || info.getElement() == null) {
      logger.log(Level.SEVERE, "Trying to render sprite with null image element.");
    }
    context.save();
    try {
      transformation.applies(context, info.getSizeFactor());
      ImageElement imageElement = info.getElement();
      context.drawImage(imageElement, -imageElement.getWidth() / 2, -imageElement.getHeight() / 2);
    } finally {
      context.restore();
    }
  }

}