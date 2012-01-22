/**
 * Copyright 2011 Philippe Beaudoin
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

package com.philbeaudoin.quebec.client.renderer;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.shared.action.AcceptPossibleActions;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.action.PossibleActionsComposite;
import com.philbeaudoin.quebec.shared.action.PossibleActionsMoveArchitect;
import com.philbeaudoin.quebec.shared.action.PossibleActionsVisitor;

/**
 * Renders the possible actions.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class PossibleActionsRenderer implements PossibleActionsVisitor {

  private final GameStateRenderer gameStateRenderer;

  @Inject
  PossibleActionsRenderer(@Assisted GameStateRenderer gameStateRenderer) {
    this.gameStateRenderer = gameStateRenderer;
  }

  @Override
  public void visit(PossibleActionsComposite host) {
    host.callOnEach(new AcceptPossibleActions() {
      @Override
      public void execute(PossibleActions possibleActions) {
        possibleActions.accept(PossibleActionsRenderer.this);
      }
    });
  }

  @Override
  public void visit(PossibleActionsMoveArchitect host) {
    gameStateRenderer.highlightTile(host.getDestinationTile());
  }
}
