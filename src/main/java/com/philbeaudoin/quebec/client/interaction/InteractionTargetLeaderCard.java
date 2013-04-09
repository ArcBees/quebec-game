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
import com.philbeaudoin.quebec.shared.game.action.HasLeaderCard;
import com.philbeaudoin.quebec.shared.game.state.LeaderCard;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * This is a target for interactions occurring on a given leader card.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionTargetLeaderCard implements InteractionTarget {

  private final GameStateRenderer gameStateRenderer;
  private final LeaderCard leaderCard;
  private final SceneNodeAnimation leaderCardAnimation;
  private final Trigger trigger;

  @Inject
  InteractionTargetLeaderCard(SceneNodeAnimation.Factory sceneNodeAnimationFactory,
      @Assisted GameStateRenderer gameStateRenderer, @Assisted HasLeaderCard target) {
    this.gameStateRenderer = gameStateRenderer;
    leaderCard = target.getLeaderCard();
    Transform fromTransform = gameStateRenderer.getLeaderCardOnBoardTransform(leaderCard);
    Transform toTransform = new ConstantTransform(
        fromTransform.getTranslation(0),
        fromTransform.getScaling(0) * 1.08,
        fromTransform.getRotation(0));
    leaderCardAnimation = sceneNodeAnimationFactory.create(gameStateRenderer, fromTransform,
        toTransform, gameStateRenderer.copyLeaderCardOnBoard(leaderCard));
    this.trigger = new RectangleTrigger(fromTransform.getTranslation(0), 0.06, 0.1);
  }

  @Override
  public void highlight() {
    gameStateRenderer.highlightLeaderCard(leaderCard);
  }

  @Override
  public void onMouseEnter(double time) {
    leaderCardAnimation.startAnim(time);
  }

  @Override
  public void onMouseLeave(double time) {
    leaderCardAnimation.stopAnim(time);
  }

  @Override
  public Trigger getTrigger() {
    return trigger;
  }
}
