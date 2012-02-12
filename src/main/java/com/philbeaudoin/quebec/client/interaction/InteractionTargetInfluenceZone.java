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

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.client.scene.SceneNodeAnimation;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.client.scene.Sprite;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.action.HasInfluenceZone;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * This is a target for interactions occurring on a given influence zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionTargetInfluenceZone implements InteractionTarget {

  private final GameStateRenderer gameStateRenderer;
  private final InfluenceType influenceZone;
  private final SceneNodeAnimation zoneAnimation;
  private final InfluenceZoneInfo influenceZoneInfo;
  private final SceneNodeList zoneNode;

  @Inject
  InteractionTargetInfluenceZone(SceneNodeAnimation.Factory sceneNodeAnimationFactory,
      SpriteResources spriteResources, @Assisted GameStateRenderer gameStateRenderer,
      @Assisted HasInfluenceZone target) {
    this.gameStateRenderer = gameStateRenderer;
    influenceZone = target.getInfluenceZone();
    influenceZoneInfo = new InfluenceZoneInfo(influenceZone);
    ConstantTransform fromTransform = new ConstantTransform(influenceZoneInfo.getSpriteCenter());
    Transform toTransform = new ConstantTransform(
        fromTransform.getTranslation(0),
        fromTransform.getScaling(0) * 1.06,
        fromTransform.getRotation(0));

    zoneNode = new SceneNodeList(fromTransform);
    zoneNode.add(new Sprite(spriteResources.getInfluenceZone(influenceZone)));
    SceneNode influenceZoneCubeNode = gameStateRenderer.getInfluenceZoneNode(influenceZone);
    Transform zoneCubesTransform =
        fromTransform.inverse().times(influenceZoneCubeNode.getTotalTransform(0));
    influenceZoneCubeNode = influenceZoneCubeNode.deepClone();
    influenceZoneCubeNode.setTransform(zoneCubesTransform);
    zoneNode.add(influenceZoneCubeNode);

    zoneAnimation = sceneNodeAnimationFactory.create(gameStateRenderer, fromTransform, toTransform,
        zoneNode);
  }

  @Override
  public void highlight() {
    // Hide the non-highlighted zone to ensure we don't double-render it.
    gameStateRenderer.getInfluenceZoneNode(influenceZone).setVisible(false);
    gameStateRenderer.forceGlassScreen();
    gameStateRenderer.addToAnimationGraph(zoneNode);
  }

  @Override
  public void onMouseEnter(double time) {
    zoneAnimation.startAnim(time);
  }

  @Override
  public void onMouseLeave(double time) {
    zoneAnimation.stopAnim(time);
  }

  @Override
  public Trigger getTrigger() {
    return influenceZoneInfo;
  }

  public InfluenceZoneInfo getInfluenceZoneInfo() {
    return influenceZoneInfo;
  }
}
