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
import com.philbeaudoin.quebec.client.scene.SceneNodeAnimation;
import com.philbeaudoin.quebec.shared.game.action.HasDestinationTile;
import com.philbeaudoin.quebec.shared.game.state.Tile;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * This is a target for interactions occurring on a given tile.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionTargetTile implements InteractionTarget {

  private final GameStateRenderer gameStateRenderer;
  private final Tile destinationTile;
  private final SceneNodeAnimation tileAnimation;
  private final Trigger trigger;

  @Inject
  InteractionTargetTile(SceneNodeAnimation.Factory sceneNodeAnimationFactory,
      @Assisted GameStateRenderer gameStateRenderer, @Assisted HasDestinationTile target) {
    this.gameStateRenderer = gameStateRenderer;
    destinationTile = target.getDestinationTile();
    Transform fromTransform = gameStateRenderer.getTileTransform(destinationTile);
    Transform toTransform = new ConstantTransform(
        fromTransform.getTranslation(0),
        fromTransform.getScaling(0) * 1.08,
        fromTransform.getRotation(0));
    tileAnimation = sceneNodeAnimationFactory.create(gameStateRenderer, fromTransform, toTransform,
        gameStateRenderer.copyTile(destinationTile));
    this.trigger = new CircleTrigger(fromTransform.getTranslation(0), 0.044);
  }

  @Override
  public void highlight() {
    gameStateRenderer.highlightTile(destinationTile);
  }

  @Override
  public void onMouseEnter(double time) {
    tileAnimation.startAnim(time);
  }

  @Override
  public void onMouseLeave(double time) {
    tileAnimation.stopAnim(time);
  }

  @Override
  public Trigger getTrigger() {
    return trigger;
  }
}
