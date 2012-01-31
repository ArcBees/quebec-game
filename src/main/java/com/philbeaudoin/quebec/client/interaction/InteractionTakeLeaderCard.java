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
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.RendererFactories;
import com.philbeaudoin.quebec.client.scene.Arrow;
import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionTakeLeaderCard;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.LeaderCard;
import com.philbeaudoin.quebec.shared.utils.ArcTransform;
import com.philbeaudoin.quebec.shared.utils.Callback;
import com.philbeaudoin.quebec.shared.utils.CallbackRegistration;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * This is an interaction with the game board for the action of taking a leader card.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionTakeLeaderCard extends InteractionImpl {

  private static final double BUMP_ANIM_DURATION = 0.2;

  private final LeaderCard leaderCard;
  private final Transform transform;
  private final Transform scaledTransform;
  private final SceneNode animatedCard;
  private final SceneNodeList arrows;

  private CallbackRegistration bumpAnimRegistration;

  private InteractionTakeLeaderCard(Scheduler scheduler, RendererFactories rendererFactories,
      GameState gameState, GameStateRenderer gameStateRenderer, ActionTakeLeaderCard action,
      Transform cardTransform) {
    super(scheduler, rendererFactories, gameState, gameStateRenderer,
        new SquareTrigger(cardTransform.getTranslation(0), 0.06, 0.1), action);

    leaderCard = action.getLeaderCard();
    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();

    arrows = new SceneNodeList();

    transform = cardTransform;
    scaledTransform = new ConstantTransform(
        transform.getTranslation(0),
        transform.getScaling(0) * 1.08,
        transform.getRotation(0));
    animatedCard = gameStateRenderer.copyLeaderCardOnBoard(action.getLeaderCard());

    // Arrow to move card.
    Transform cardTo = gameStateRenderer.getLeaderCardOnPlayerTransform(playerColor);
    arrows.add(new Arrow(transform.getTranslation(0), cardTo.getTranslation(0)));

    // Arrow to move passive cubes to active, if needed.
    if (gameState.nbPlayerWithLeaders() > 0 &&
        gameState.getCurrentPlayer().getNbPassiveCubes() > 0) {
      Transform cubeFrom = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, false);
      Transform cubeTo = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, true);
      arrows.add(new Arrow(cubeFrom.getTranslation(0), cubeTo.getTranslation(0)));
    }
  }

  @Inject
  public InteractionTakeLeaderCard(Scheduler scheduler, RendererFactories rendererFactories,
      @Assisted GameState gameState, @Assisted GameStateRenderer gameStateRenderer,
      @Assisted ActionTakeLeaderCard action) {
    this(scheduler, rendererFactories, gameState, gameStateRenderer, action,
        gameStateRenderer.getLeaderCardOnBoardTransform(action.getLeaderCard()));
  }

  @Override
  public void highlight() {
    gameStateRenderer.highlightLeaderCard(leaderCard);
  }

  @Override
  protected void doMouseMove(double x, double y, double time) {
  }

  // TODO: Extract this logic.
  @Override
  protected void doMouseEnter(double x, double y, double time) {
    // Bump up.
    ensureBumpAnimUnregistered();
    animatedCard.setTransform(new ArcTransform(transform, scaledTransform, time,
        time + BUMP_ANIM_DURATION));
    gameStateRenderer.addToAnimationGraph(animatedCard);
    // Draw arrows.
    gameStateRenderer.addToAnimationGraph(arrows);
  }

  @Override
  protected void doMouseLeave(double x, double y, double time) {
    // Clear arrows.
    arrows.setParent(null);
    // Bump down.
    ensureBumpAnimUnregistered();
    animatedCard.setTransform(new ArcTransform(scaledTransform, transform, time,
        time + BUMP_ANIM_DURATION));
    gameStateRenderer.addToAnimationGraph(animatedCard);

    bumpAnimRegistration = animatedCard.addAnimationCompletedCallback(new Callback() {
      @Override
      public void execute() {
        scheduler.scheduleDeferred(new ScheduledCommand() {
          @Override
          public void execute() {
            animatedCard.setParent(null);
            ensureBumpAnimUnregistered();
          }
        });
      }
    });
  }

  private void ensureBumpAnimUnregistered() {
    if (bumpAnimRegistration != null) {
      bumpAnimRegistration.unregister();
      bumpAnimRegistration = null;
    }
  }
}
