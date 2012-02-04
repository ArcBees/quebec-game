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
import com.philbeaudoin.quebec.shared.ActionType;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;

/**
 * This class makes it possible to obtain the image element for any type of sprite. Images are
 * lazily instantiated when first needed.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@Singleton
public class SpriteResources {

  /**
   * Static sprite types.
   */
  public static enum Type {
    board, activeToken
  }

  /**
   * Information on the image for a sprite. The image element is lazily instantiated the first time
   * the information is requested.
   * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
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
     * factor, the sprite is drawn to its real size on the board.
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
  private final Info[] influenceZoneInfos = new Info[5];
  private final Info[][] starTokensInfo = new Info[5][3];
  private final Info[] actionInfos = new Info[16];

  @Inject
  SpriteResources(Resources resources) {
    Collection<Info> nullList = Collections.nCopies(Type.values().length, null);
    imageInfos.addAll(nullList);
    setInfoForType(Type.board, resources.board(), 0.00030656);
    setInfoForType(Type.activeToken, resources.activeToken(), 0.000303);

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

    setInfoForInfluenceZone(InfluenceType.RELIGIOUS, resources.influenceZoneReligious());
    setInfoForInfluenceZone(InfluenceType.POLITIC, resources.influenceZonePolitic());
    setInfoForInfluenceZone(InfluenceType.ECONOMIC, resources.influenceZoneEconomic());
    setInfoForInfluenceZone(InfluenceType.CULTURAL, resources.influenceZoneCultural());
    setInfoForInfluenceZone(InfluenceType.CITADEL, resources.influenceZoneCitadel());

    setInfoForStarToken(PlayerColor.BLACK, 1, resources.starBlack1());
    setInfoForStarToken(PlayerColor.BLACK, 2, resources.starBlack2());
    setInfoForStarToken(PlayerColor.BLACK, 3, resources.starBlack3());
    setInfoForStarToken(PlayerColor.WHITE, 1, resources.starWhite1());
    setInfoForStarToken(PlayerColor.WHITE, 2, resources.starWhite2());
    setInfoForStarToken(PlayerColor.WHITE, 3, resources.starWhite3());
    setInfoForStarToken(PlayerColor.ORANGE, 1, resources.starOrange1());
    setInfoForStarToken(PlayerColor.ORANGE, 2, resources.starOrange2());
    setInfoForStarToken(PlayerColor.ORANGE, 3, resources.starOrange3());
    setInfoForStarToken(PlayerColor.GREEN, 1, resources.starGreen1());
    setInfoForStarToken(PlayerColor.GREEN, 2, resources.starGreen2());
    setInfoForStarToken(PlayerColor.GREEN, 3, resources.starGreen3());
    setInfoForStarToken(PlayerColor.PINK, 1, resources.starPink1());
    setInfoForStarToken(PlayerColor.PINK, 2, resources.starPink2());
    setInfoForStarToken(PlayerColor.PINK, 3, resources.starPink3());

    setInfoForAction(ActionType.PURPLE_ANY, resources.actionPurple1());
    setInfoForAction(ActionType.PURPLE_ONE_TO_CITADEL_ONE_TO_ANY, resources.actionPurple2());
    setInfoForAction(ActionType.PURPLE_ONE_POINT_ONE_TO_ANY_ACTIVATE_ONE, resources.actionPurple3());
    setInfoForAction(ActionType.PURPLE_ONE_TO_ANY_MOVE_TWO, resources.actionPurple4());
    setInfoForAction(ActionType.RED_ANY, resources.actionRed1());
    setInfoForAction(ActionType.RED_TWO_TO_RED_OR_BLUE, resources.actionRed2());
    setInfoForAction(ActionType.RED_TWO_TO_PURPLE_OR_YELLOW, resources.actionRed3());
    setInfoForAction(ActionType.RED_TWO_TO_CITADEL, resources.actionRed4());
    setInfoForAction(ActionType.YELLOW_ANY, resources.actionYellow1());
    setInfoForAction(ActionType.YELLOW_FILL_ONE_SPOT, resources.actionYellow2());
    setInfoForAction(ActionType.YELLOW_MOVE_ARCHITECT, resources.actionYellow3());
    setInfoForAction(ActionType.YELLOW_ACTIVATE_THREE, resources.actionYellow4());
    setInfoForAction(ActionType.BLUE_ANY, resources.actionBlue1());
    setInfoForAction(ActionType.BLUE_SCORE_FOR_CUBES_IN_HAND, resources.actionBlue2());
    setInfoForAction(ActionType.BLUE_SCORE_FOR_ZONES, resources.actionBlue3());
    setInfoForAction(ActionType.BLUE_ADD_STAR, resources.actionBlue4());
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
   * Obtain information, including the {@link ImageElement}, for the given type of influence zone.
   * @param influenceZone The type of the influence zone for which to get information.
   * @return The information on that sprite.
   */
  public Info getInfluenceZone(InfluenceType influenceZone) {
    Info imageInfo = influenceZoneInfos[influenceZone.ordinal()];
    lazilyInstantiateImageElement(imageInfo);
    return imageInfo;
  }

  /**
   * Obtain information, including the {@link ImageElement}, for the given type of star token.
   * @param starTokenColor The color of the desired star token. Not NONE or NEUTRAL.
   * @param nbStars The number of stars on the star token (1, 2 or 3).
   * @return The information on that sprite.
   */
  public Info getStarToken(PlayerColor starTokenColor, int nbStars) {
    assert starTokenColor.isNormalColor();
    assert nbStars > 0 && nbStars < 4;
    Info imageInfo = starTokensInfo[starTokenColor.ordinal() - 1][nbStars - 1];
    lazilyInstantiateImageElement(imageInfo);
    return imageInfo;
  }

  /**
   * Obtain information, including the {@link ImageElement}, for the given type of action.
   * @param actionType The type of action.
   * @return The information on that sprite.
   */
  public Info getAction(ActionType actionType) {
    Info imageInfo = actionInfos[actionType.ordinal()];
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

  private void setInfoForInfluenceZone(InfluenceType influenceZone, DataResource dataResource) {
    influenceZoneInfos[influenceZone.ordinal()] = new Info(dataResource.getSafeUri(), 0.000303);
  }

  private void setInfoForStarToken(PlayerColor starTokenColor, int nbStars,
      DataResource dataResource) {
    starTokensInfo[starTokenColor.ordinal() - 1][nbStars - 1] =
        new Info(dataResource.getSafeUri(), 0.000403);
  }

  private void setInfoForAction(ActionType actionType, DataResource dataResource) {
    actionInfos[actionType.ordinal()] = new Info(dataResource.getSafeUri(), 0.000303);
  }

}
