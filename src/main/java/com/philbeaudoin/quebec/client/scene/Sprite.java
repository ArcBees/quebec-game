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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.philbeaudoin.quebec.client.utils.LoggerFactory;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * A sprite that can be transformed and drawn into an HTML5 2d canvas.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class Sprite extends SceneNodeImpl {
  final Logger logger;

  private final SpriteResources.Info info;

  public Sprite(SpriteResources.Info info) {
    this(info, new ConstantTransform(), true);
  }

  public Sprite(SpriteResources.Info info, Transform transform) {
    this(info, transform, true);
  }

  private Sprite(SpriteResources.Info info, Transform transform, boolean visible) {
    super(transform, visible);
    logger = LoggerFactory.get(Sprite.class);
    this.info = info;
  }

  @Override
  public void drawUntransformed(double time, Context2d context) {
    if (info == null || info.getElement() == null) {
      logger.log(Level.SEVERE, "Trying to draw a sprite with null image element.");
    }
    double sizeFactor = info.getSizeFactor();
    ImageElement imageElement = info.getElement();
    context.scale(sizeFactor, sizeFactor);
    context.drawImage(imageElement, -info.getWidth() / 2, -info.getHeight() / 2);
  }

  @Override
  public SceneNode deepClone() {
    return new Sprite(info, getTransform(), isVisible());
  }

}