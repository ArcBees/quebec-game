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
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.message.BoardLocation;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.message.TextBoxInfo;
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
   * Renders a {@link TextBoxInfo} into the animation graph of the given game state renderer.
   * @param textBoxInfo The text box information to render, if null nothing is rendered.
   * @param gameStateRenderer The game state renderer into which to render.
   */
  public void render(TextBoxInfo textBoxInfo, GameStateRenderer gameStateRenderer) {
    if (textBoxInfo != null) {
      Message message = textBoxInfo.getMessage();
      MessageRenderer messageRenderer = messageRendererProvider.get();
      message.accept(messageRenderer);
      Vector2d anchor = computeBoardLocation(textBoxInfo.getAnchor(), gameStateRenderer);
      BoardLocation pointTo = textBoxInfo.getPointTo();
      if (pointTo != BoardLocation.NONE) {
        gameStateRenderer.addToAnimationGraph(new Callout(anchor, computeBoardLocation(pointTo,
            gameStateRenderer)));
      }
      gameStateRenderer.addToAnimationGraph(new ComplexText(messageRenderer.getComponents(),
          new ConstantTransform(anchor)));
    }
  }

  private Vector2d computeBoardLocation(BoardLocation location,
      GameStateRenderer gameStateRenderer) {
    switch (location) {
    case CENTER:
      return new Vector2d(GameStateRenderer.TEXT_CENTER, 0.4);
    case TOP_CENTER:
      return new Vector2d(GameStateRenderer.TEXT_CENTER, GameStateRenderer.TEXT_LINE_1);
    case PLAYER_AREAS_TEXT:
      return new Vector2d(0.8, 0.3);
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
    default:
      assert false;
    }
    assert false;
    return null;
  }
}
