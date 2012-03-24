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

package com.philbeaudoin.quebec.client.renderer;

import javax.inject.Inject;
import javax.inject.Provider;

import com.philbeaudoin.quebec.client.scene.Callout;
import com.philbeaudoin.quebec.client.scene.ComplexText;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.message.BoardLocation;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.message.TextBoxInfo;
import com.philbeaudoin.quebec.shared.state.LeaderCard;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Utility class to render a textbox into the animation graph.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class TextBoxRenderer {
  Provider<MessageRenderer> messageRendererProvider;

  @Inject
  TextBoxRenderer(Provider<MessageRenderer> messageRendererProvider) {
    this.messageRendererProvider = messageRendererProvider;
  }

  /**
   * Renders a {@link TextBoxInfo} of the given game state renderer into a scene node list.
   * @param textBoxInfo The text box information to render, if null nothing is rendered.
   * @param gameStateRenderer The game state renderer into which to render.
   * @returns The scene node list for the text box.
   */
  public SceneNodeList render(TextBoxInfo textBoxInfo, GameStateRenderer gameStateRenderer) {
    SceneNodeList result = new SceneNodeList();
    if (textBoxInfo != null) {
      Message message = textBoxInfo.getMessage();
      MessageRenderer messageRenderer = messageRendererProvider.get();
      message.accept(messageRenderer);
      BoardLocation pointToLocation = textBoxInfo.getPointTo();
      Vector2d pointTo = null;
      if (pointToLocation != BoardLocation.NONE) {
        pointTo = computeBoardLocation(pointToLocation, gameStateRenderer, null, null);
      }
      Vector2d anchor = computeBoardLocation(textBoxInfo.getAnchor(), gameStateRenderer,
          pointTo, messageRenderer.calculateApproximateSize());
      if (pointTo != null) {
        result.add(new Callout(anchor, pointTo));
      }

      result.add(new ComplexText(messageRenderer.getComponents(), new ConstantTransform(anchor)));
    }
    return result;
  }

  private Vector2d computeBoardLocation(BoardLocation location,
      GameStateRenderer gameStateRenderer, Vector2d pointTo, Vector2d size) {

    // Check regular locations.
    switch (location) {
    case CENTER:
      return new Vector2d(GameStateRenderer.TEXT_CENTER, 0.4);
    case TOP_CENTER:
      return new Vector2d(GameStateRenderer.TEXT_CENTER, GameStateRenderer.TEXT_LINE_1);
    case PLAYER_AREAS:
      return new Vector2d(0.38, 0.3);
    case SCORE:
      return new Vector2d(1.65, 0.95);
    case BLACK_ARCHITECT_ON_PLAYER_AREA:
      return gameStateRenderer.getArchitectOnPlayerTransform(
          PlayerColor.BLACK, false).getTranslation(0);
    case BLACK_PASSIVE_CUBES:
      return gameStateRenderer.getPlayerCubeZoneTransform(
          PlayerColor.BLACK, false).getTranslation(0);
    case BLACK_ACTIVE_CUBES:
      return gameStateRenderer.getPlayerCubeZoneTransform(
          PlayerColor.BLACK, true).getTranslation(0);
    case RELIGIOUS_LEADER:
      return gameStateRenderer.getLeaderCardOnBoardTransform(
          LeaderCard.RELIGIOUS).getTranslation(0).add(0, 0.052);
    case POLITIC_LEADER:
      return gameStateRenderer.getLeaderCardOnBoardTransform(
          LeaderCard.POLITIC).getTranslation(0).add(0, 0.052);
    case ECONOMIC_LEADER:
      return gameStateRenderer.getLeaderCardOnBoardTransform(
          LeaderCard.ECONOMIC).getTranslation(0).add(0, 0.052);
    case CULTURAL_TWO_THREE_LEADER:
      return gameStateRenderer.getLeaderCardOnBoardTransform(
          LeaderCard.CULTURAL_TWO_THREE).getTranslation(0).add(0, 0.052);
    case CULTURAL_FOUR_FIVE_LEADER:
      return gameStateRenderer.getLeaderCardOnBoardTransform(
          LeaderCard.CULTURAL_FOUR_FIVE).getTranslation(0).add(0, 0.052);
    case CITADEL_LEADER:
      return gameStateRenderer.getLeaderCardOnBoardTransform(
          LeaderCard.CITADEL).getTranslation(0).add(0, 0.052);
    default:
      // Not a regular locations, relative locations need a pointTo and a size.
      assert pointTo != null && size != null;
      break;
    }

    // Check relative locations.
    double deltaX = size.getX() / 2.0 + 0.1;
    double deltaY = size.getY() / 2.0 + 0.1;
    double deltaXNear = size.getX() / 2.0 + 0.02;
    double deltaYNear = size.getY() / 2.0 + 0.02;

    switch (location) {
    case TOP_OF_TARGET_NEAR:
      return pointTo.add(0, -deltaYNear);
    case TOP_RIGHT_OF_TARGET_NEAR:
      return pointTo.add(deltaXNear, -deltaYNear);
    case RIGHT_OF_TARGET_NEAR:
      return pointTo.add(deltaXNear, 0);
    case BOTTOM_RIGHT_OF_TARGET_NEAR:
      return pointTo.add(deltaXNear, deltaYNear);
    case BOTTOM_OF_TARGET_NEAR:
      return pointTo.add(0, deltaYNear);
    case BOTTOM_LEFT_OF_TARGET_NEAR:
      return pointTo.add(-deltaXNear, deltaYNear);
    case LEFT_OF_TARGET_NEAR:
      return pointTo.add(-deltaXNear, 0);
    case TOP_LEFT_OF_TARGET_NEAR:
      return pointTo.add(-deltaXNear, -deltaYNear);

    case TOP_OF_TARGET:
      return pointTo.add(0, -deltaY);
    case TOP_RIGHT_OF_TARGET:
      return pointTo.add(deltaX, -deltaY);
    case RIGHT_OF_TARGET:
      return pointTo.add(deltaX, 0);
    case BOTTOM_RIGHT_OF_TARGET:
      return pointTo.add(deltaX, deltaY);
    case BOTTOM_OF_TARGET:
      return pointTo.add(0, deltaY);
    case BOTTOM_LEFT_OF_TARGET:
      return pointTo.add(-deltaX, deltaY);
    case LEFT_OF_TARGET:
      return pointTo.add(-deltaX, 0);
    case TOP_LEFT_OF_TARGET:
      return pointTo.add(-deltaX, -deltaY);

    default:
      assert false;
    }
    assert false;
    return null;
  }
}
