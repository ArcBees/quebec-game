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
import com.philbeaudoin.quebec.client.renderer.ChangeRenderer;
import com.philbeaudoin.quebec.client.renderer.ChangeRendererGenerator;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.RendererFactories;
import com.philbeaudoin.quebec.shared.action.GameAction;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.utils.Callback;
import com.philbeaudoin.quebec.shared.utils.CallbackRegistration;

/**
 * This is the basic implementation of an interaction a user can have with the game board. It will
 * keep track of entering and leaving a trigger area and will execute the animation and the action
 * upon a click.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public abstract class InteractionImpl implements Interaction {

  protected final Scheduler scheduler;
  protected final RendererFactories rendererFactories;
  protected final GameState gameState;
  protected final GameStateRenderer gameStateRenderer;
  protected final Trigger trigger;
  protected final GameAction action;

  private boolean inside;
  private CallbackRegistration animationCompletedRegistration;

  protected InteractionImpl(Scheduler scheduler, RendererFactories rendererFactories,
      GameState gameState, GameStateRenderer gameStateRenderer, Trigger trigger,
      GameAction action) {
    this.scheduler = scheduler;
    this.rendererFactories = rendererFactories;
    this.gameState = gameState;
    this.gameStateRenderer = gameStateRenderer;
    this.trigger = trigger;
    this.action = action;
  }

  @Override
  public void onMouseMove(double x, double y, double time) {
    if (trigger.triggerAt(x, y)) {
      doMouseMove(x, y, time);
      if (!inside) {
        doMouseEnter(x, y, time);
        inside = true;
      }
    } else if (inside) {
      inside = false;
      doMouseLeave(x, y, time);
    }
  }

  @Override
  public void onMouseClick(double x, double y, double time) {
    if (trigger.triggerAt(x, y)) {
      gameStateRenderer.clearAnimationGraph();
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

  /**
   * Performs any graphical additions that should be performed when the mouse is moving inside the
   * interaction area.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param time The current time.
   */
  protected abstract void doMouseMove(double x, double y, double time);

  /**
   * Performs any graphical additions that should be performed when the mouse enters the
   * interaction area.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param time The current time.
   */
  protected abstract void doMouseEnter(double x, double y, double time);

  /**
   * Performs any graphical additions that should be performed when the mouse leaves the
   * interaction area.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param time The current time.
   */
  protected abstract void doMouseLeave(double x, double y, double time);

}
