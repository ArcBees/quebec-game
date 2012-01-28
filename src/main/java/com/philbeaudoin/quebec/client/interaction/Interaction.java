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
import com.philbeaudoin.quebec.client.renderer.ChangeRenderer;
import com.philbeaudoin.quebec.client.renderer.ChangeRendererGenerator;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.RendererFactories;
import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.shared.action.GameAction;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.utils.Callback;
import com.philbeaudoin.quebec.shared.utils.CallbackRegistration;

/**
 * This class indicates a possible interaction the user can have with the game board. It allows for
 * effects when the mouse moves over an object or when a click is detected.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class Interaction {
  private final Scheduler scheduler;
  private final RendererFactories rendererFactories;
  private final Trigger trigger;
  private final SceneNode mouseOverNode;
  private final GameAction action;
  private CallbackRegistration animationCompletedRegistration;

  @Inject
  Interaction(Scheduler scheduler,
      RendererFactories rendererFactories,
      @Assisted Trigger trigger, @Assisted SceneNode mouseOverNode,
      @Assisted GameAction action) {
    this.scheduler = scheduler;
    this.rendererFactories = rendererFactories;
    this.trigger = trigger;
    this.mouseOverNode = mouseOverNode;
    this.action = action;
  }

  /**
   * Indicates that the mouse has moved and that the interaction should be executed if it is
   * triggered.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param gameStateRenderer The game state renderer.
   */
  public void onMouseMove(double x, double y, GameStateRenderer gameStateRenderer) {
    if (trigger.triggerAt(x, y)) {
      gameStateRenderer.addToInteractionGraph(mouseOverNode);
    }
  }

  /**
   * Indicates that the mouse has been clicked and that the interaction should be executed if it is
   * triggered. The game state will be modified.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param gameState The game state.
   * @param gameStateRenderer The game state renderer.
   */
  public void onMouseClick(double x, double y, final GameState gameState,
      final GameStateRenderer gameStateRenderer) {
    if (trigger.triggerAt(x, y)) {
      if (animationCompletedRegistration != null) {
        animationCompletedRegistration.unregister();
        animationCompletedRegistration = null;
      }

      scheduler.scheduleDeferred(new ScheduledCommand() {
        @Override
        public void execute() {
          gameStateRenderer.clearInteractions();
          gameStateRenderer.removeAllHighlights();
        }
      });

      final GameStateChange change = action.execute(gameState);
      ChangeRendererGenerator generator = rendererFactories.createChangeRendererGenerator();
      change.accept(generator);

      ChangeRenderer changeRenderer = generator.getChangeRenderer();
      changeRenderer.generateAnim(gameStateRenderer, 0.0);
      changeRenderer.undoAdditions(gameStateRenderer);

      animationCompletedRegistration = gameStateRenderer.addAnimationCompletedCallback(
      new Callback() {
        @Override public void execute() {
          gameStateRenderer.clearAnimationGraph();
          change.apply(gameState);
          gameStateRenderer.render(gameState);
          animationCompletedRegistration.unregister();
        }
      });
    }
  }
}
