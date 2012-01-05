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
import com.philbeaudoin.quebec.shared.PlayerColor;

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
  private final Info[][][] tileInfos = new Info[4][4][5];
  private final Info[] cubeInfos = new Info[5];
  private final Info[] pawnInfos = new Info[6];
  private final Info[] leaderInfos = new Info[5];

  @Inject
  SpriteResources(Resources resources) {
    Collection<Info> nullList = Collections.nCopies(Type.values().length, null);
    imageInfos.addAll(nullList);
    setInfoForType(Type.board, resources.board(), 0.00030656);

    setInfoForTile(InfluenceType.RELIGIOUS, 0, resources.tileReligiousOne(),
        resources.tileReligiousOne1(), resources.tileReligiousOne2(), resources.tileReligiousOne3(),
        resources.tileReligiousOne4());
    setInfoForTile(InfluenceType.RELIGIOUS, 1, resources.tileReligiousTwo(),
        resources.tileReligiousTwo1(), resources.tileReligiousTwo2());
    setInfoForTile(InfluenceType.RELIGIOUS, 2, resources.tileReligiousThree(),
        resources.tileReligiousThree1(), resources.tileReligiousThree2(),
        resources.tileReligiousThree3());
    setInfoForTile(InfluenceType.RELIGIOUS, 3, resources.tileReligiousFour(),
        resources.tileReligiousFour1(), resources.tileReligiousFour2());
    setInfoForTile(InfluenceType.POLITIC, 0, resources.tilePoliticOne(),
        resources.tilePoliticOne1(), resources.tilePoliticOne2());
    setInfoForTile(InfluenceType.POLITIC, 1, resources.tilePoliticTwo(),
        resources.tilePoliticTwo1(), resources.tilePoliticTwo2(), resources.tilePoliticTwo3(),
        resources.tilePoliticTwo4());
    setInfoForTile(InfluenceType.POLITIC, 2, resources.tilePoliticThree(),
        resources.tilePoliticThree1(), resources.tilePoliticThree2());
    setInfoForTile(InfluenceType.POLITIC, 3, resources.tilePoliticFour(),
        resources.tilePoliticFour1(), resources.tilePoliticFour2(), resources.tilePoliticFour3());
    setInfoForTile(InfluenceType.ECONOMIC, 0, resources.tileEconomicOne(),
        resources.tileEconomicOne1(), resources.tileEconomicOne2(), resources.tileEconomicOne3());
    setInfoForTile(InfluenceType.ECONOMIC, 1, resources.tileEconomicTwo(),
        resources.tileEconomicTwo1(), resources.tileEconomicTwo2());
    setInfoForTile(InfluenceType.ECONOMIC, 2, resources.tileEconomicThree(),
        resources.tileEconomicThree1(), resources.tileEconomicThree2(),
        resources.tileEconomicThree3(), resources.tileEconomicThree4());
    setInfoForTile(InfluenceType.ECONOMIC, 3, resources.tileEconomicFour(),
        resources.tileEconomicFour1(), resources.tileEconomicFour2());
    setInfoForTile(InfluenceType.CULTURAL, 0, resources.tileCulturalOne(),
        resources.tileCulturalOne1(), resources.tileCulturalOne2());
    setInfoForTile(InfluenceType.CULTURAL, 1, resources.tileCulturalTwo(),
        resources.tileCulturalTwo1(), resources.tileCulturalTwo2(), resources.tileCulturalTwo3());
    setInfoForTile(InfluenceType.CULTURAL, 2, resources.tileCulturalThree(),
        resources.tileCulturalThree1(), resources.tileCulturalThree2());
    setInfoForTile(InfluenceType.CULTURAL, 3, resources.tileCulturalFour(),
        resources.tileCulturalFour1(), resources.tileCulturalFour2(), resources.tileCulturalFour3(),
        resources.tileCulturalFour4());

    setInfoForCube(PlayerColor.BLACK, resources.cubeBlack());
    setInfoForCube(PlayerColor.WHITE, resources.cubeWhite());
    setInfoForCube(PlayerColor.ORANGE, resources.cubeOrange());
    setInfoForCube(PlayerColor.GREEN, resources.cubeGreen());
    setInfoForCube(PlayerColor.PINK, resources.cubePink());

    setInfoForPawn(PlayerColor.BLACK, resources.pawnBlack());
    setInfoForPawn(PlayerColor.WHITE, resources.pawnWhite());
    setInfoForPawn(PlayerColor.ORANGE, resources.pawnOrange());
    setInfoForPawn(PlayerColor.GREEN, resources.pawnGreen());
    setInfoForPawn(PlayerColor.PINK, resources.pawnPink());
    setInfoForPawn(PlayerColor.NEUTRAL, resources.pawnGold());

    setInfoForLeader(InfluenceType.RELIGIOUS, resources.leaderReligious());
    setInfoForLeader(InfluenceType.POLITIC, resources.leaderPolitic());
    setInfoForLeader(InfluenceType.ECONOMIC, resources.leaderEconomic());
    setInfoForLeader(InfluenceType.CULTURAL, resources.leaderCultural());
    setInfoForLeader(InfluenceType.CITADEL, resources.leaderCitadel());
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
    Info imageInfo = tileInfos[influenceType.ordinal()][century][0];
    lazilyInstantiateImageElement(imageInfo);
    return imageInfo;
  }

  /**
   * Obtain information, including the {@link ImageElement}, for the given type of tile sprite,
   * shown facing the "built" side.
   * @param influenceType The influence type of tile sprite desired.
   * @param century The century of tile sprite desired (0, 1, 2 or 3).
   * @param index The index of the building tile (0 to 3, the valid range depends on influenceType
   *     and century).
   * @return The information of that sprite.
   */
  public Info getBuildingTile(InfluenceType influenceType, int century, int index) {
    Info imageInfo = tileInfos[influenceType.ordinal()][century][index + 1];
    lazilyInstantiateImageElement(imageInfo);
    return imageInfo;
  }

  /**
   * Obtain information, including the {@link ImageElement}, for the given type of cube.
   * @param playerColor The color of the player for which to get cubes. Will not work with
   *     {@code PlayerColor.NONE}.
   * @return The information on that sprite.
   */
  public Info getCube(PlayerColor playerColor) {
    int colorIndex = playerColor.ordinal();
    assert colorIndex > 0;
    Info imageInfo = cubeInfos[colorIndex - 1];
    lazilyInstantiateImageElement(imageInfo);
    return imageInfo;
  }

  /**
   * Obtain information, including the {@link ImageElement}, for the given type of pawn.
   * @param playerColor The color of the player for which to get cubes. Will not work with
   *     {@code PlayerColor.NONE}.
   * @return The information on that sprite.
   */
  public Info getPawn(PlayerColor playerColor) {
    int colorIndex = playerColor.ordinal();
    assert colorIndex > 0 && colorIndex <= 6;
    Info imageInfo = pawnInfos[colorIndex - 1];
    lazilyInstantiateImageElement(imageInfo);
    return imageInfo;
  }

  /**
   * Obtain information, including the {@link ImageElement}, for the given type of leader card.
   * @param leaderType The type of the leader for which to get the card.
   * @return The information on that sprite.
   */
  public Info getLeader(InfluenceType leaderType) {
    Info imageInfo = leaderInfos[leaderType.ordinal()];
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

  private void setInfoForTile(InfluenceType influenceType, int century, DataResource dataResource,
      DataResource... buildingDataResources) {
    tileInfos[influenceType.ordinal()][century][0] = new Info(dataResource.getSafeUri(), 0.000303);
    int index = 1;
    for (DataResource buildingDataResource : buildingDataResources) {
      tileInfos[influenceType.ordinal()][century][index] = new Info(
          buildingDataResource.getSafeUri(), 0.000303);
      index++;
    }
  }

  private void setInfoForCube(PlayerColor playerColor, DataResource dataResource) {
    cubeInfos[playerColor.ordinal() - 1] = new Info(dataResource.getSafeUri(), 0.000315);
  }

  private void setInfoForPawn(PlayerColor playerColor, DataResource dataResource) {
    pawnInfos[playerColor.ordinal() - 1] = new Info(dataResource.getSafeUri(), 0.000355);
  }

  private void setInfoForLeader(InfluenceType leaderType, DataResource dataResource) {
    leaderInfos[leaderType.ordinal()] = new Info(dataResource.getSafeUri(), 0.000265);
  }
}
