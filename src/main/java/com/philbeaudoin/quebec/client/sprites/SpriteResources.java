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
import java.util.Collection;
import java.util.Collections;

import javax.inject.Singleton;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.philbeaudoin.quebec.client.resources.Resources;

/**
 * This class makes it possible to obtain the image element for any type of sprite. Images are
 * lazily instantiated when first needed.
 *
 * @author Philippe Beaudoin
 */
@Singleton
public class SpriteResources {

  /**
   * The various sprites.
   */
  public enum Type {
    board,
    tileRedOne
  }

  /**
   * Information on the image for a sprite. The image element is lazily instantiated the first time
   * the information is requested.
   * @author Philippe Beaudoin
   */
  public class Info {
    private final SafeUri safeUri;
    private final double sizeFactor;
    private ImageElement element;

    public Info(SafeUri safeUri, double sizeFactor) {
      this.safeUri = safeUri;
      this.sizeFactor = sizeFactor;
    }

    /**
     * Access the optimal size factor of the sprite. When resized uniformally according to that
     * factor, the sprite is rendered to its real size on the board.
     * @return The optimal size factor.
     */
    public double getSizeFactor() {
      return sizeFactor;
    }

    /**
     * Access the image element for the sprite.
     * @return The image element.
     */
    public ImageElement getElement() {
      return element;
    }
  }
  
  private final ArrayList<Info> imageInfos = new ArrayList<Info>(Type.values().length);

  @Inject
  SpriteResources(Resources resources) {
    Collection<Info> nullList = Collections.nCopies(Type.values().length, null);
    imageInfos.addAll(nullList);
    safeUri(Type.board, resources.board(), 1);
    safeUri(Type.tileRedOne, resources.tileRedOne(), 0.00023);
  }

  private void safeUri(Type type, DataResource dataResource, double resizeFactor) {
    imageInfos.set(type.ordinal(), new Info(dataResource.getSafeUri(), resizeFactor));
  }

  /**
   * Obtain the {@link ImageElement} for the given type of sprite. The ImageElement is lazily
   * instantiated once.
   * @param type The type of sprite desired.
   * @return The ImageElement of that sprite.
   */
  public Info get(Type type) {
    Info imageInfo = imageInfos.get(type.ordinal());
    if (imageInfo.element == null) {
      Image image = new Image(imageInfo.safeUri);
      imageInfo.element = (ImageElement) image.getElement().cast();
    }
    return imageInfo;
  }
}
