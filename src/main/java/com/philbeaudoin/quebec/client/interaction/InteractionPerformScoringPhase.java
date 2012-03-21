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
import javax.inject.Provider;

import com.google.gwt.core.client.Scheduler;
import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.MessageRenderer;
import com.philbeaudoin.quebec.client.scene.Arrow;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.ZoneScoringInformation;
import com.philbeaudoin.quebec.shared.action.ActionPerformScoringPhase;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * This is an interaction that performs one phase of scoring, pausing to let the user continue.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionPerformScoringPhase extends InteractionWithAction {
  private final SceneNodeList arrows;

  @Inject
  public InteractionPerformScoringPhase(Scheduler scheduler,
      InteractionFactories interactionFactories, Provider<MessageRenderer> messageRendererProvider,
      @Assisted GameState gameState, @Assisted GameStateRenderer gameStateRenderer,
      @Assisted ActionPerformScoringPhase action) {
    super(scheduler, gameState, gameStateRenderer,
        interactionFactories.createInteractionTargetText(gameStateRenderer,
            createContinueMessageRenderer(messageRendererProvider),
            new Vector2d(GameStateRenderer.TEXT_CENTER, GameStateRenderer.TEXT_LINE_2)),
        action.execute(gameState));

    this.arrows = new SceneNodeList();

    ZoneScoringInformation scoringInformation = action.computeZoneScoringInformation(gameState);
    if (scoringInformation == null) {
      return;
    }

    Vector2d from = new InfluenceZoneInfo(scoringInformation.getZoneToScore()).getArrowCenter();
    int scoringZoneIndex = scoringInformation.getScoringZoneIndex();
    boolean needCascadeArrow = false;
    for (PlayerColor playerColor : PlayerColor.NORMAL) {
      if (scoringInformation.getCubesToCascade(playerColor) > 0) {
        if (scoringZoneIndex == 4) {
          // Last scoring zone of century, cascade into active reserve.
          Vector2d to = gameStateRenderer.getPlayerCubeZoneTransform(
              playerColor, true).getTranslation(0);
          arrows.add(new Arrow(from, to));
        } else {
          needCascadeArrow = true;
        }
      }
    }
    if (needCascadeArrow) {
      int century = gameState.getCentury();
      InfluenceType nextZone = InfluenceType.getScoringZoneForCentury(century,
          scoringZoneIndex + 1);
      Vector2d to = new InfluenceZoneInfo(nextZone).getArrowCenter();
      arrows.add(new Arrow(from, to));
    }
  }

  private static MessageRenderer createContinueMessageRenderer(
      Provider<MessageRenderer> messageRendererProvider) {
    MessageRenderer messageRenderer = messageRendererProvider.get();
    new Message.Text("continue").accept(messageRenderer);
    return messageRenderer;
  }

  @Override
  protected void doMouseEnter(double x, double y, double time) {
    super.doMouseEnter(x, y, time);
    gameStateRenderer.addToAnimationGraph(arrows);
    arrows.getParent().sendToBack(arrows);
  }

  @Override
  protected void doMouseLeave(double x, double y, double time) {
    super.doMouseLeave(x, y, time);
    arrows.setParent(null);
  }

}
