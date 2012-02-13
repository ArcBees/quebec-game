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
import com.philbeaudoin.quebec.client.scene.Sprite;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.shared.action.HasBoardAction;
import com.philbeaudoin.quebec.shared.state.BoardAction;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;

/**
 * This is a target for interactions occurring on a given board action.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionTargetBoardAction implements InteractionTarget {

  private final GameStateRenderer gameStateRenderer;
  private final SceneNodeAnimation actionTileAnimation;
  private final Trigger trigger;
  private final Sprite actionSprite;

  @Inject
  InteractionTargetBoardAction(SceneNodeAnimation.Factory sceneNodeAnimationFactory,
      SpriteResources spriteResources, @Assisted GameStateRenderer gameStateRenderer,
      @Assisted HasBoardAction target) {
    this.gameStateRenderer = gameStateRenderer;
    BoardAction boardAction = target.getBoardAction();
    ConstantTransform fromTransform = new ConstantTransform(
        gameStateRenderer.getActionTransform(boardAction));
    ConstantTransform toTransform = new ConstantTransform(fromTransform.getTranslation(0),
        1.08, 0);
    actionSprite = new Sprite(spriteResources.getAction(boardAction.getActionType()),
        fromTransform);
    actionTileAnimation = sceneNodeAnimationFactory.create(gameStateRenderer, fromTransform,
        toTransform, actionSprite);
    trigger = new CircleTrigger(fromTransform.getTranslation(0), 0.044);
  }

  @Override
  public void highlight() {
    gameStateRenderer.forceGlassScreen();
    gameStateRenderer.addToAnimationGraph(actionSprite);
  }

  @Override
  public void onMouseEnter(double time) {
    actionTileAnimation.startAnim(time);
  }

  @Override
  public void onMouseLeave(double time) {
    actionTileAnimation.stopAnim(time);
  }

  @Override
  public Trigger getTrigger() {
    return trigger;
  }
}
