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
import com.philbeaudoin.quebec.client.renderer.MessageRenderer;
import com.philbeaudoin.quebec.client.scene.ComplexText;
import com.philbeaudoin.quebec.client.scene.ComplexText.SizeInfo;
import com.philbeaudoin.quebec.client.scene.SceneNodeAnimation;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * This is a target for interactions occurring on a box of text.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionTargetText implements InteractionTarget {

  private final GameStateRenderer gameStateRenderer;
  private final ComplexText textNode;
  private final SceneNodeAnimation textAnimation;
  private final Trigger trigger;

  @Inject
  InteractionTargetText(SceneNodeAnimation.Factory sceneNodeAnimationFactory,
      @Assisted GameStateRenderer gameStateRenderer, @Assisted MessageRenderer messageRenderer,
      @Assisted Vector2d pos) {
    this.gameStateRenderer = gameStateRenderer;
    SizeInfo sizeInfo = messageRenderer.calculateApproximateSize();
    Transform fromTransform = new ConstantTransform(pos);
    Transform toTransform = new ConstantTransform(pos, 1.08, 0);

    textNode = new ComplexText(messageRenderer.getComponents(), "#87CEEB", "#6A5ACD",
        fromTransform);

    textAnimation = sceneNodeAnimationFactory.create(gameStateRenderer, fromTransform, toTransform,
        textNode);

    this.trigger = new RectangleTrigger(pos, sizeInfo.getWidth(), sizeInfo.getHeight());
  }

  @Override
  public void highlight() {
    gameStateRenderer.forceGlassScreen();
    gameStateRenderer.addToAnimationGraph(textNode);
  }

  @Override
  public void onMouseEnter(double time) {
    textAnimation.startAnim(time);
  }

  @Override
  public void onMouseLeave(double time) {
    textAnimation.stopAnim(time);
  }

  @Override
  public Trigger getTrigger() {
    return trigger;
  }
}
