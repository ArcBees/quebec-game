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

import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.MessageRenderer;
import com.philbeaudoin.quebec.client.renderer.RendererFactories;
import com.philbeaudoin.quebec.client.scene.Arrow;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionSendCubesToZone;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * This is an interaction with the game board for the action of sending one worker to an influence
 * zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionSendCubesToZone extends InteractionWithAction {

  private final SceneNodeList arrows;

  public InteractionSendCubesToZone(Scheduler scheduler, InteractionFactories interactionFactories,
      RendererFactories rendererFactories, MessageRenderer messageRenderer, GameState gameState,
      GameStateRenderer gameStateRenderer, InteractionTargetInfluenceZone target,
      ActionSendCubesToZone action) {
    super(scheduler, rendererFactories, gameState, gameStateRenderer, target,
        createActionMessage(messageRenderer, gameState, action),
        action.execute(gameState));

    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    arrows = new SceneNodeList();
    Transform cubesFrom = gameStateRenderer.getPlayerCubeZoneTransform(playerColor,
        action.areCubesFromActive());

    // Arrow to move cubes.
    arrows.add(new Arrow(cubesFrom.getTranslation(0),
        target.getInfluenceZoneInfo().getArrowCenter()));
  }

  @Inject
  public InteractionSendCubesToZone(Scheduler scheduler, InteractionFactories interactionFactories,
      RendererFactories rendererFactories, MessageRenderer messageRenderer,
      @Assisted GameState gameState, @Assisted GameStateRenderer gameStateRenderer,
      @Assisted ActionSendCubesToZone action) {
    this(scheduler, interactionFactories, rendererFactories, messageRenderer, gameState,
        gameStateRenderer, interactionFactories.createInteractionTargetInfluenceZone(
            gameStateRenderer, action), action);
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

  private static MessageRenderer createActionMessage(MessageRenderer messageRenderer,
      GameState gameState, ActionSendCubesToZone action) {
    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    if (action.areCubesFromActive()) {
      new Message.SendActiveCubesToZone(action.getNbCubes(), playerColor,
          action.getInfluenceZone()).accept(messageRenderer);
    } else {
      new Message.SendPassiveCubesToZone(action.getNbCubes(), playerColor,
          action.getInfluenceZone()).accept(messageRenderer);
    }
    return messageRenderer;
  }
}
