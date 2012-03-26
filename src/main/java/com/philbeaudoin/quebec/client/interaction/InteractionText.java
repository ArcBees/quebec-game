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
import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.MessageRenderer;
import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.shared.action.GameAction;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * This is an interaction that allows triggering an action via a box of text.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionText extends InteractionWithAction {
  private final Highlighter highlighter;
  private final SceneNode extras;

  @Inject
  public InteractionText(Scheduler scheduler, InteractionFactories interactionFactories,
      @Assisted GameState gameState, @Assisted GameStateRenderer gameStateRenderer,
      @Assisted MessageRenderer messageRenderer, @Assisted Highlighter highlighter,
      @Assisted SceneNode extras, @Assisted Vector2d pos, @Assisted GameAction action) {
    super(scheduler, gameState, gameStateRenderer,
        interactionFactories.createInteractionTargetText(gameStateRenderer, messageRenderer, pos),
        action.execute(gameState));
    this.highlighter = highlighter;
    this.extras = extras;
  }

  @Override
  public void highlight() {
    super.highlight();
    if (highlighter != null) {
      highlighter.highlight();
    }
  }

  @Override
  protected void doMouseEnter(double x, double y, double time) {
    super.doMouseEnter(x, y, time);
    if (extras != null) {
      gameStateRenderer.addToAnimationGraph(extras);
    }
  }

  @Override
  protected void doMouseLeave(double x, double y, double time) {
    super.doMouseLeave(x, y, time);
    if (extras != null) {
      extras.setParent(null);
    }
  }

}
