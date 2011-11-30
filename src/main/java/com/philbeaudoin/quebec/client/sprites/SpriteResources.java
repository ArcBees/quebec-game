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
import com.philbeaudoin.quebec.shared.InfluenceType;

/**
 * This class makes it possible to obtain the image element for any type of sprite. Images are
 * lazily instantiated when first needed.
 *
 * @author Philippe Beaudoin
 */
@Singleton
public class SpriteResources {

  /**
   * Static sprite types.
   */
  public static enum Type {
    board
  }

  /**
   * Information on the image for a sprite. The image element is lazily instantiated the first time
   * the information is requested.
   * @author Philippe Beaudoin
   */
  public static class Info {
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
  private final Info[][] tileInfos = new Info[4][4];

  @Inject
  SpriteResources(Resources resources) {
    Collection<Info> nullList = Collections.nCopies(Type.values().length, null);
    imageInfos.addAll(nullList);
    setInfoForType(Type.board, resources.board(), 1);

    setInfoForTile(InfluenceType.RELIGIOUS, 0, resources.tileReligiousOne());
    setInfoForTile(InfluenceType.RELIGIOUS, 1, resources.tileReligiousTwo());
    setInfoForTile(InfluenceType.RELIGIOUS, 2, resources.tileReligiousThree());
    setInfoForTile(InfluenceType.RELIGIOUS, 3, resources.tileReligiousFour());
    setInfoForTile(InfluenceType.POLITIC, 0, resources.tilePoliticOne());
    setInfoForTile(InfluenceType.POLITIC, 1, resources.tilePoliticTwo());
    setInfoForTile(InfluenceType.POLITIC, 2, resources.tilePoliticThree());
    setInfoForTile(InfluenceType.POLITIC, 3, resources.tilePoliticFour());
    setInfoForTile(InfluenceType.ECONOMIC, 0, resources.tileEconomicOne());
    setInfoForTile(InfluenceType.ECONOMIC, 1, resources.tileEconomicTwo());
    setInfoForTile(InfluenceType.ECONOMIC, 2, resources.tileEconomicThree());
    setInfoForTile(InfluenceType.ECONOMIC, 3, resources.tileEconomicFour());
    setInfoForTile(InfluenceType.CULTURAL, 0, resources.tileCulturalOne());
    setInfoForTile(InfluenceType.CULTURAL, 1, resources.tileCulturalTwo());
    setInfoForTile(InfluenceType.CULTURAL, 2, resources.tileCulturalThree());
    setInfoForTile(InfluenceType.CULTURAL, 3, resources.tileCulturalFour());
  }

  /**
   * Obtain information, including the {@link ImageElement}, for the given type of static sprite.
   * The ImageElement is lazily instantiated once.
   * @param type The type of sprite desired.
   * @return The information of that sprite.
   */
  public Info get(Type type) {
    Info imageInfo = imageInfos.get(type.ordinal());
    lazilyInstantiateImageElement(imageInfo);
    return imageInfo;
  }
  /**
   * Obtain information, including the {@link ImageElement}, for the given type of tile sprite,
   * shown facing the "unbuilt" side.
   * @param influenceType The influence type of tile sprite desired.
   * @param century The century of tile sprite desired (0, 1, 2 or 3).
   * @return The information of that sprite.
   */
  public Info getTile(InfluenceType influenceType, int century) {
    Info imageInfo = tileInfos[influenceType.ordinal()][century];
    lazilyInstantiateImageElement(imageInfo);
    return imageInfo;
  }

  /**
   * Lazily instantiate the image element of an image info, if needed.
   * @param imageInfo The image info into which to instantiate the image element.
   */
  private void lazilyInstantiateImageElement(Info imageInfo) {
    if (imageInfo.element == null) {
      Image image = new Image(imageInfo.safeUri);
      imageInfo.element = (ImageElement) image.getElement().cast();
    }
  }

  private void setInfoForType(Type type, DataResource dataResource, double resizeFactor) {
    imageInfos.set(type.ordinal(), new Info(dataResource.getSafeUri(), resizeFactor));
  }

  private void setInfoForTile(InfluenceType influenceType, int century, DataResource dataResource) {
    tileInfos[influenceType.ordinal()][century] = new Info(dataResource.getSafeUri(), 0.00023);
  }
}
