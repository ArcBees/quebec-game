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

package com.philbeaudoin.quebec.client.interaction;

import com.google.gwt.core.client.Scheduler;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.MessageRenderer;
import com.philbeaudoin.quebec.client.renderer.RendererFactories;
import com.philbeaudoin.quebec.client.scene.Arrow;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.TileState;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * This is the base class for interactions involving move architect actions.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public abstract class InteractionMoveArchitectBase extends
    InteractionWithAction {

  private final SceneNodeList arrows;

  public InteractionMoveArchitectBase(Scheduler scheduler,
      RendererFactories rendererFactories, MessageRenderer messageRenderer, GameState gameState,
      GameStateRenderer gameStateRenderer, InteractionTarget target,
      ActionMoveArchitect action) {
    super(scheduler, rendererFactories, gameState, gameStateRenderer, target,
        createActionMessage(messageRenderer), action.execute(gameState));

    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    PlayerColor architectColor = action.isNeutralArchitect() ? PlayerColor.NEUTRAL : playerColor;
    TileState origin = gameState.findTileUnderArchitect(architectColor);

    arrows = new SceneNodeList();

    Transform architectFrom;
    if (origin == null) {
      // Architect starts from the player zone.
      architectFrom = gameStateRenderer.getArchitectOnPlayerTransform(
          playerColor, action.isNeutralArchitect());
    } else {
      // Architect starts from its current tile.
      architectFrom = gameStateRenderer.getArchitectSlotOnTileTransform(origin.getTile());
    }

    // Arrow to move architect.
    Transform architectTo = gameStateRenderer.getArchitectSlotOnTileTransform(
        action.getDestinationTile());
    arrows.add(new Arrow(architectFrom.getTranslation(0), architectTo.getTranslation(0)));

    // Arrow to move passive cubes to active, if needed.
    if (gameState.getCurrentPlayer().getNbPassiveCubes() > 0) {
      Transform cubeFrom = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, false);
      Transform cubeTo = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, true);
      arrows.add(new Arrow(cubeFrom.getTranslation(0), cubeTo.getTranslation(0)));
    }
  }

  @Override
  protected void doMouseEnter(double x, double y, double time) {
    super.doMouseEnter(x, y, time);
    gameStateRenderer.addToAnimationGraph(arrows);
  }

  @Override
  protected void doMouseLeave(double x, double y, double time) {
    super.doMouseLeave(x, y, time);
    arrows.setParent(null);
  }

  private static MessageRenderer createActionMessage(MessageRenderer messageRenderer) {
    new Message.MoveYourArchitectToThisTile().accept(messageRenderer);
    return messageRenderer;
  }
}