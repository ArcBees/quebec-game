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
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationOffboardNeutral;
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationPlayer;
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationTile;
import com.philbeaudoin.quebec.shared.location.CubeDestinationInfluenceZone;
import com.philbeaudoin.quebec.shared.location.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.location.CubeDestinationTile;
import com.philbeaudoin.quebec.shared.location.LeaderDestinationBoard;
import com.philbeaudoin.quebec.shared.location.LeaderDestinationPlayer;
import com.philbeaudoin.quebec.shared.location.Location;
import com.philbeaudoin.quebec.shared.location.LocationBoardAction;
import com.philbeaudoin.quebec.shared.location.LocationBottomCenter;
import com.philbeaudoin.quebec.shared.location.LocationCenter;
import com.philbeaudoin.quebec.shared.location.LocationPlayerAreas;
import com.philbeaudoin.quebec.shared.location.LocationScore;
import com.philbeaudoin.quebec.shared.location.LocationTopCenter;
import com.philbeaudoin.quebec.shared.location.LocationVisitor;
import com.philbeaudoin.quebec.shared.location.LocationRelative;
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
      Vector2d anchor = computeBoardLocation(textBoxInfo.getAnchor(), gameStateRenderer,
          messageRenderer.calculateApproximateSize());
      Vector2d pointTo = computeBoardLocation(textBoxInfo.getPointTo(), gameStateRenderer,
          new Vector2d(0, 0));
      if (pointTo != null) {
        result.add(new Callout(anchor, pointTo));
      }

      result.add(new ComplexText(messageRenderer.getComponents(), new ConstantTransform(anchor)));
    }
    return result;
  }

  private Vector2d computeBoardLocation(Location location, GameStateRenderer gameStateRenderer,
      Vector2d size) {
    if (location == null) {
      return null;
    }
    return location.accept(new Positioner(gameStateRenderer, size));
  }

  private class Positioner implements LocationVisitor<Vector2d> {

    private static final double SCALE_FACTOR = 0.06;
    private final GameStateRenderer gameStateRenderer;
    private final Vector2d size;

    Positioner(GameStateRenderer gameStateRenderer, Vector2d size) {
      this.gameStateRenderer = gameStateRenderer;
      this.size = size;
    }

    @Override
    public Vector2d visit(ArchitectDestinationOffboardNeutral host) {
      return new Vector2d(0, 0.5);
    }

    @Override
    public Vector2d visit(ArchitectDestinationPlayer host) {
      return gameStateRenderer.getArchitectOnPlayerTransform(
          host.getArchitectColor(), host.isNeutralArchitect()).getTranslation(0);
    }

    @Override
    public Vector2d visit(ArchitectDestinationTile host) {
      return gameStateRenderer.getArchitectOnTileTransform(host.getTile()).getTranslation(0);
    }

    @Override
    public Vector2d visit(CubeDestinationInfluenceZone host) {
      return gameStateRenderer.getInfluenceZoneNode(
          host.getInfluenceType()).getTotalTransform(0).getTranslation();
    }

    @Override
    public Vector2d visit(CubeDestinationPlayer host) {
      return gameStateRenderer.getPlayerCubeZoneTransform(host.getPlayerColor(),
          host.isActive()).getTranslation(0);
    }

    @Override
    public Vector2d visit(CubeDestinationTile host) {
      return gameStateRenderer.getCubesOnTileTransform(host.getTile(),
          host.getSpot()).getTranslation(0);
    }

    @Override
    public Vector2d visit(LeaderDestinationBoard host) {
      return gameStateRenderer.getLeaderCardOnBoardTransform(
          host.getLeaderCard()).getTranslation(0).add(0, 0.052);
    }

    @Override
    public Vector2d visit(LeaderDestinationPlayer host) {
      return gameStateRenderer.getLeaderCardOnPlayerTransform(
          host.getPlayerColor()).getTranslation(0).add(0, 0.052);
    }

    @Override
    public Vector2d visit(LocationRelative host) {
      // Check relative locations.
      Vector2d targetLocation = host.getTarget().accept(
          new Positioner(gameStateRenderer, new Vector2d()));
      Vector2d pos = host.getRelativePosition();
      double deltaX = Math.signum(pos.getX()) * (size.getX() / 2.0 +
          Math.max(0, Math.abs(pos.getX()) - 1) * SCALE_FACTOR + 0.02);
      double deltaY = Math.signum(pos.getY()) * (size.getY() / 2.0 +
          Math.max(0, Math.abs(pos.getY()) - 1) * SCALE_FACTOR + 0.02);
      return targetLocation.add(deltaX, deltaY);
    }

    @Override
    public Vector2d visit(LocationTopCenter host) {
      return new Vector2d(GameStateRenderer.TEXT_CENTER, GameStateRenderer.TEXT_LINE_1);
    }

    @Override
    public Vector2d visit(LocationCenter host) {
      return new Vector2d(GameStateRenderer.TEXT_CENTER, 0.4);
    }

    @Override
    public Vector2d visit(LocationBottomCenter host) {
      return new Vector2d(GameStateRenderer.TEXT_CENTER, 0.9);
    }

    @Override
    public Vector2d visit(LocationPlayerAreas host) {
      return new Vector2d(0.38, 0.3);
    }

    @Override
    public Vector2d visit(LocationScore host) {
      return gameStateRenderer.getScoreSpotTransform(host.getScore()).getTranslation(0);
    }

    @Override
    public Vector2d visit(LocationBoardAction host) {
      return gameStateRenderer.getActionTransform(host.getBoardAction()).getTranslation(0);
    }
  }
}
