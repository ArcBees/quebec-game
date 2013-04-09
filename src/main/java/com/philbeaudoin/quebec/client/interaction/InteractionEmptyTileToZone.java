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
import com.philbeaudoin.quebec.client.scene.Arrow;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.shared.game.action.ActionEmptyTileToZone;
import com.philbeaudoin.quebec.shared.game.state.GameState;

/**
 * This is an interaction with the game board for the action of sending all the cubes from a given
 * tile to a given influence zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionEmptyTileToZone extends InteractionWithAction {

  private final SceneNodeList arrows;

  public InteractionEmptyTileToZone(Scheduler scheduler, InteractionFactories interactionFactories,
      GameState gameState, GameStateRenderer gameStateRenderer,
      InteractionTargetInfluenceZone target, ActionEmptyTileToZone action) {
    super(scheduler, gameState, gameStateRenderer, target, action);

    arrows = new SceneNodeList();
    // Arrow to move cubes.
    arrows.add(new Arrow(gameStateRenderer.getTileTransform(action.getOrigin()).getTranslation(0),
        target.getInfluenceZoneInfo().getArrowCenter()));
  }

  @Inject
  public InteractionEmptyTileToZone(Scheduler scheduler, InteractionFactories interactionFactories,
      @Assisted GameState gameState, @Assisted GameStateRenderer gameStateRenderer,
      @Assisted ActionEmptyTileToZone action) {
    this(scheduler, interactionFactories, gameState, gameStateRenderer,
        interactionFactories.createInteractionTargetInfluenceZone(gameStateRenderer, action),
        action);
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
}
