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
import com.philbeaudoin.quebec.client.renderer.TextBoxRenderer;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.shared.action.GameAction;
import com.philbeaudoin.quebec.shared.location.LocationTopCenter;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.message.TextBoxInfo;
import com.philbeaudoin.quebec.shared.state.GameController;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.utils.CallbackRegistration;

/**
 * This is the basic implementation of an interaction for which a click results in executing an
 * action.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public abstract class InteractionWithAction implements Interaction {

  protected final Scheduler scheduler;
  protected final GameState gameState;
  protected final GameStateRenderer gameStateRenderer;
  private final SceneNodeList actionText;
  private final GameAction gameAction;
  private final InteractionTarget target;

  private CallbackRegistration animationCompletedRegistration;
  private boolean inside;

  protected InteractionWithAction(Scheduler scheduler, GameState gameState,
      GameStateRenderer gameStateRenderer, InteractionTarget target,
      GameAction gameAction) {
    this(scheduler, null, gameState, gameStateRenderer, target, null, gameAction);
  }

  protected InteractionWithAction(Scheduler scheduler, TextBoxRenderer textBoxRenderer,
      GameState gameState, GameStateRenderer gameStateRenderer,
      InteractionTarget target, Message message, GameAction gameAction) {
    assert (message == null) || (textBoxRenderer != null);
    this.scheduler = scheduler;
    this.gameState = gameState;
    this.gameStateRenderer = gameStateRenderer;
    this.gameAction = gameAction;
    this.target = target;

    if (message != null && !gameState.hasPossibleActionMessage()) {
      actionText = textBoxRenderer.render(
          new TextBoxInfo(message, new LocationTopCenter()), gameStateRenderer);
    } else {
      this.actionText = null;
    }
  }

  @Override
  public void onMouseClick(GameController gameController, double x, double y, double time) {
    if (getTrigger().triggerAt(x, y)) {
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
      GameStateChange gameStateChange = gameAction.execute(gameController, gameState);
      gameStateRenderer.generateAnimFor(gameState, gameStateChange, gameAction);
    }
  }

  @Override
  public void onMouseMove(double x, double y, double time) {
    if (target.getTrigger().triggerAt(x, y)) {
      doMouseMove(x, y, time);
      if (!inside) {
        doMouseEnter(x, y, time);
        if (actionText != null) {
          gameStateRenderer.addToAnimationGraph(actionText);
        }
        inside = true;
      }
    } else if (inside) {
      inside = false;
      doMouseLeave(x, y, time);
      if (actionText != null) {
        actionText.setParent(null);
      }
    }
  }

  @Override
  public void highlight() {
    target.highlight();
  }

  /**
   * Access the trigger of this interaction.
   * @return The trigger of the interaction.
   */
  protected Trigger getTrigger() {
    return target.getTrigger();
  }

  /**
   * Performs any graphical additions that should be performed when the mouse is moving inside the
   * interaction area.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param time The current time.
   */
  protected void doMouseMove(double x, double y, double time) {
  }

  /**
   * Performs any graphical additions that should be performed when the mouse enters the
   * interaction area.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param time The current time.
   */
  protected void doMouseEnter(double x, double y, double time) {
    target.onMouseEnter(time);
  }

  /**
   * Performs any graphical additions that should be performed when the mouse leaves the
   * interaction area.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param time The current time.
   */
  protected void doMouseLeave(double x, double y, double time) {
    target.onMouseLeave(time);
  }
}
