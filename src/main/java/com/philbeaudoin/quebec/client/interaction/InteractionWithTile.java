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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.RendererFactories;
import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.shared.action.GameActionOnTile;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.utils.ArcTransform;
import com.philbeaudoin.quebec.shared.utils.Callback;
import com.philbeaudoin.quebec.shared.utils.CallbackRegistration;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * This is the implementation of an interaction that is triggered by clicking on a destination tile.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public abstract class InteractionWithTile extends InteractionImpl {

  private static final double BUMP_ANIM_DURATION = 0.2;

  private final Tile destinationTile;
  private final Transform destinationTransform;
  private final Transform scaledTransform;
  private final SceneNode animatedTile;

  private CallbackRegistration bumpAnimRegistration;

  protected InteractionWithTile(Scheduler scheduler,
      InteractionFactories interactionFactories, RendererFactories rendererFactories,
      GameState gameState, GameStateRenderer gameStateRenderer, GameActionOnTile action,
      Tile destinationTile, Transform destinationTransform) {
    super(scheduler, rendererFactories, gameState, gameStateRenderer,
        new CircleTrigger(destinationTransform.getTranslation(0), 0.044), action);

    this.destinationTile = destinationTile;
    this.destinationTransform = destinationTransform;
    scaledTransform = new ConstantTransform(
        destinationTransform.getTranslation(0),
        destinationTransform.getScaling(0) * 1.08,
        destinationTransform.getRotation(0));
    animatedTile = gameStateRenderer.copyTile(destinationTile);
  }

  protected InteractionWithTile(Scheduler scheduler,
      InteractionFactories factories, RendererFactories rendererFactories, GameState gameState,
      GameStateRenderer gameStateRenderer, GameActionOnTile action, Tile destinationTile) {
    this(scheduler, factories, rendererFactories, gameState, gameStateRenderer, action,
        destinationTile, gameStateRenderer.getTileTransform(destinationTile));
  }

  protected InteractionWithTile(Scheduler scheduler,
      InteractionFactories factories, RendererFactories rendererFactories,  GameState gameState,
      GameStateRenderer gameStateRenderer, GameActionOnTile action) {
    this(scheduler, factories, rendererFactories, gameState, gameStateRenderer, action,
        action.getDestinationTile());
  }

  @Override
  public void highlight() {
    gameStateRenderer.highlightTile(destinationTile);
  }

  @Override
  protected void doMouseMove(double x, double y, double time) {
  }

  // TODO: Extract this logic.
  @Override
  protected void doMouseEnter(double x, double y, double time) {
    // Bump up.
    ensureBumpAnimUnregistered();
    animatedTile.setTransform(new ArcTransform(destinationTransform, scaledTransform, time,
        time + BUMP_ANIM_DURATION));
    gameStateRenderer.addToAnimationGraph(animatedTile);
  }

  @Override
  protected void doMouseLeave(double x, double y, double time) {
    // Bump down.
    ensureBumpAnimUnregistered();
    animatedTile.setTransform(new ArcTransform(scaledTransform, destinationTransform, time,
        time + BUMP_ANIM_DURATION));
    gameStateRenderer.addToAnimationGraph(animatedTile);

    bumpAnimRegistration = animatedTile.addAnimationCompletedCallback(new Callback() {
      @Override
      public void execute() {
        scheduler.scheduleDeferred(new ScheduledCommand() {
          @Override
          public void execute() {
            animatedTile.setParent(null);
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
