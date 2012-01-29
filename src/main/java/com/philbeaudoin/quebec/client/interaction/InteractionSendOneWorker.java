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
import com.philbeaudoin.quebec.client.renderer.RendererFactories;
import com.philbeaudoin.quebec.client.scene.Arrow;
import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.client.scene.Sprite;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionSendOneWorker;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.utils.ArcTransform;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * This is an interaction with the game board for the action of sending one worker to an influence
 * zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionSendOneWorker extends InteractionImpl {

  private static final double BUMP_ANIM_DURATION = 0.2;

  private final SceneNodeList arrows;
  private final InfluenceType influenceZone;
  private final ConstantTransform transform;
  private final ConstantTransform scaledTransform;
  private final SceneNodeList zoneNode;

  private InteractionSendOneWorker(Scheduler scheduler, SpriteResources spriteResources,
      RendererFactories rendererFactories, GameState gameState,
      GameStateRenderer gameStateRenderer, ActionSendOneWorker action,
      InfluenceZoneInfo influenceZoneInfo) {
    super(scheduler, rendererFactories, gameState, gameStateRenderer, influenceZoneInfo, action);
    this.influenceZone = action.getInfluenceZone();

    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    arrows = new SceneNodeList();
    Transform cubesFrom = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, true);

    // Arrow to move cubes.
    arrows.add(new Arrow(cubesFrom.getTranslation(0), influenceZoneInfo.getArrowCenter()));

    // Transform for bumpy animation.
    transform = new ConstantTransform(influenceZoneInfo.getSpriteCenter());
    scaledTransform = new ConstantTransform(
        transform.getTranslation(0),
        transform.getScaling(0) * 1.08,
        transform.getRotation(0));

    zoneNode = new SceneNodeList(transform);
    zoneNode.add(new Sprite(spriteResources.getInfluenceZone(influenceZone)));
    SceneNode influenceZoneCubeNode = gameStateRenderer.getInfluenceZoneNode(influenceZone);
    Transform zoneCubesTransform =
        transform.inverse().times(influenceZoneCubeNode.getTotalTransform(0));
    influenceZoneCubeNode = influenceZoneCubeNode.deepClone();
    influenceZoneCubeNode.setTransform(zoneCubesTransform);
    zoneNode.add(influenceZoneCubeNode);
  }

  @Inject
  public InteractionSendOneWorker(Scheduler scheduler, SpriteResources spriteResources,
      InteractionFactories interactionFactories, RendererFactories rendererFactories,
      @Assisted GameState gameState, @Assisted GameStateRenderer gameStateRenderer,
      @Assisted ActionSendOneWorker action) {
    this(scheduler, spriteResources, rendererFactories, gameState, gameStateRenderer, action,
        new InfluenceZoneInfo(action.getInfluenceZone()));
  }

  @Override
  protected void doMouseMove(double x, double y, double time) {
  }

  @Override
  public void highlight() {
    // Hide the non-highlighted no to ensure we don't double-render it.
    gameStateRenderer.getInfluenceZoneNode(influenceZone).setVisible(false);
    gameStateRenderer.forceHighlight();
    gameStateRenderer.addToAnimationGraph(zoneNode);
  }

  // TODO: Extract this logic.
  @Override
  protected void doMouseEnter(double x, double y, double time) {
    // Bump up.
    zoneNode.setTransform(new ArcTransform(transform, scaledTransform, time,
        time + BUMP_ANIM_DURATION));
    gameStateRenderer.addToAnimationGraph(arrows);
  }

  @Override
  protected void doMouseLeave(double x, double y, double time) {
    // Bump down.
    zoneNode.setTransform(new ArcTransform(scaledTransform, transform, time,
        time + BUMP_ANIM_DURATION));
    arrows.setParent(null);
  }
}
