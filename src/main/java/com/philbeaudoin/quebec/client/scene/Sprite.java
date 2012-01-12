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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.philbeaudoin.quebec.client.utils.LoggerFactory;
import com.philbeaudoin.quebec.shared.utils.Transformation;

/**
 * A sprite that can be transformed and drawn into an HTML5 2d canvas.
 * @author Philippe Beaudoin
 */
public class Sprite extends SceneNodeImpl {
  final Logger logger;

  private final SpriteResources.Info info;

  public Sprite(SpriteResources.Info info) {
    super();
    logger = LoggerFactory.get(Sprite.class);
    this.info = info;
  }

  public Sprite(SpriteResources.Info info, Transformation transformation) {
    super(transformation);
    logger = LoggerFactory.get(Sprite.class);
    this.info = info;
  }

  @Override
  public void draw(double time, Context2d context) {
    if (info == null || info.getElement() == null) {
      logger.log(Level.SEVERE, "Trying to draw a sprite with null image element.");
    }
    context.save();
    try {
      getTransformation().applies(time, context, info.getSizeFactor());
      ImageElement imageElement = info.getElement();
      context.drawImage(imageElement, -imageElement.getWidth() / 2, -imageElement.getHeight() / 2);
    } finally {
      context.restore();
    }
  }

}