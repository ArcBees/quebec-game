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
import com.philbeaudoin.quebec.shared.action.AcceptPossibleActions;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.action.ActionSendOneWorker;
import com.philbeaudoin.quebec.shared.action.ActionSendWorkers;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.action.PossibleActionsComposite;
import com.philbeaudoin.quebec.shared.action.PossibleActionsVisitor;
import com.philbeaudoin.quebec.shared.state.GameState;

/**
 * Use this class to generate the list of {@link Interaction} corresponding to a given
 * {@link PossibleActions}. All the generated interactions will be added to the provided
 * {@link GameStateRenderer}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionGenerator implements PossibleActionsVisitor {

  private final InteractionFactories factories;
  private final GameState gameState;
  private final GameStateRenderer gameStateRenderer;

  @Inject
  InteractionGenerator(InteractionFactories factories,
      @Assisted GameState gameState,
      @Assisted GameStateRenderer gameStateRenderer) {
    this.factories = factories;
    this.gameState = gameState;
    this.gameStateRenderer = gameStateRenderer;
  }

  @Override
  public void visit(PossibleActionsComposite host) {
    host.callOnEach(new AcceptPossibleActions() {
      @Override
      public void execute(PossibleActions possibleActions) {
        possibleActions.accept(InteractionGenerator.this);
      }
    });
  }

  @Override
  public void visit(ActionMoveArchitect host) {
    gameStateRenderer.addInteraction(factories.createInteractionMoveArchitect(gameState,
        gameStateRenderer, host));
  }

  @Override
  public void visit(ActionSendWorkers host) {
    gameStateRenderer.addInteraction(factories.createInteractionSendWorkers(gameState,
        gameStateRenderer, host));
  }

  @Override
  public void visit(ActionSendOneWorker host) {
    gameStateRenderer.addInteraction(factories.createInteractionSendOneWorker(gameState,
        gameStateRenderer, host));
  }
}
