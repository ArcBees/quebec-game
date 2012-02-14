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

import java.util.ArrayList;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.action.ActionScorePoints;
import com.philbeaudoin.quebec.shared.action.ActionSelectBoadAction;
import com.philbeaudoin.quebec.shared.action.ActionSendCubesToZone;
import com.philbeaudoin.quebec.shared.action.ActionSendOneWorker;
import com.philbeaudoin.quebec.shared.action.ActionSendWorkers;
import com.philbeaudoin.quebec.shared.action.ActionTakeLeaderCard;
import com.philbeaudoin.quebec.shared.action.GameActionVisitor;
import com.philbeaudoin.quebec.shared.state.GameState;

/**
 * Use this class to generate the list of {@link Interaction} corresponding to a given
 * {@link com.philbeaudoin.quebec.shared.action.GameAction GameAction}. All the generated
 * interactions will be added to the provided{@link GameStateRenderer}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionGenerator implements GameActionVisitor {

  private final InteractionFactories factories;
  private final GameState gameState;
  private final GameStateRenderer gameStateRenderer;

  private final ArrayList<ActionMoveArchitect> moveArchitectActions =
      new ArrayList<ActionMoveArchitect>();
  private final ArrayList<ActionSendWorkers> sendWorkersActions =
      new ArrayList<ActionSendWorkers>();
  private final ArrayList<ActionSendOneWorker> sendOneWorkerActions =
      new ArrayList<ActionSendOneWorker>();
  private final ArrayList<ActionTakeLeaderCard> takeLeaderCardActions =
      new ArrayList<ActionTakeLeaderCard>();
  private final ArrayList<ActionSendCubesToZone> sendCubesToZoneActions =
      new ArrayList<ActionSendCubesToZone>();
  private final ArrayList<ActionSelectBoadAction> selectBoardActionActions =
      new ArrayList<ActionSelectBoadAction>();
  private final ArrayList<ActionScorePoints> scorePointsActions =
      new ArrayList<ActionScorePoints>();

  @Inject
  InteractionGenerator(InteractionFactories factories,
      @Assisted GameState gameState,
      @Assisted GameStateRenderer gameStateRenderer) {
    this.factories = factories;
    this.gameState = gameState;
    this.gameStateRenderer = gameStateRenderer;
  }

  /**
   * Generates all the interactions that correspond to the visited actions. The method should be
   * called exactly once after the possible actions have been visited.
   */
  public void generateInteractions() {

    // Look at move architect action and pair them if possible. Actions are paired if they send
    // the regular and the neutral architect to the same tile.
    while (!moveArchitectActions.isEmpty()) {
      ActionMoveArchitect action = moveArchitectActions.remove(moveArchitectActions.size() - 1);
      boolean paired = false;
      for (ActionMoveArchitect other : moveArchitectActions) {
        if (action.getDestinationTile() == other.getDestinationTile()) {
          paired = true;
          if (action.isNeutralArchitect()) {
            assert !other.isNeutralArchitect();
            gameStateRenderer.addInteraction(factories.createInteractionMoveUnknownArchitect(
                gameState, gameStateRenderer, other, action));
          } else {
            assert !action.isNeutralArchitect();
            gameStateRenderer.addInteraction(factories.createInteractionMoveUnknownArchitect(
                gameState, gameStateRenderer, action, other));
          }
          moveArchitectActions.remove(other);
          break;
        }
      }
      if (!paired) {
        gameStateRenderer.addInteraction(factories.createInteractionMoveArchitect(gameState,
            gameStateRenderer, action));
      }
    }

    for (ActionSendWorkers action : sendWorkersActions) {
      gameStateRenderer.addInteraction(factories.createInteractionSendWorkers(gameState,
          gameStateRenderer, action));
    }
    sendWorkersActions.clear();
    for (ActionSendOneWorker action : sendOneWorkerActions) {
      gameStateRenderer.addInteraction(factories.createInteractionSendCubesToZone(gameState,
          gameStateRenderer, true, action));
    }
    sendOneWorkerActions.clear();
    for (ActionTakeLeaderCard action : takeLeaderCardActions) {
      gameStateRenderer.addInteraction(factories.createInteractionTakeLeaderCard(gameState,
          gameStateRenderer, action));
    }
    takeLeaderCardActions.clear();
    for (ActionSendCubesToZone action : sendCubesToZoneActions) {
      gameStateRenderer.addInteraction(factories.createInteractionSendCubesToZone(gameState,
          gameStateRenderer, false, action));
    }
    sendCubesToZoneActions.clear();
    for (ActionSelectBoadAction action : selectBoardActionActions) {
      gameStateRenderer.addInteraction(factories.createInteractionSelectBoardAction(gameState,
          gameStateRenderer, action));
    }
    selectBoardActionActions.clear();
    for (ActionScorePoints action : scorePointsActions) {
      // TODO(beaudoin): Internationalize.
      gameStateRenderer.addInteraction(factories.createInteractionText(gameState,
          gameStateRenderer, "Score " + action.getNbPoints() + " points", action));
    }
    selectBoardActionActions.clear();
  }

  @Override
  public void visit(ActionMoveArchitect host) {
    moveArchitectActions.add(host);
  }

  @Override
  public void visit(ActionSendWorkers host) {
    sendWorkersActions.add(host);
  }

  @Override
  public void visit(ActionSendOneWorker host) {
    sendOneWorkerActions.add(host);
  }

  @Override
  public void visit(ActionTakeLeaderCard host) {
    takeLeaderCardActions.add(host);
  }

  @Override
  public void visit(ActionSendCubesToZone host) {
    sendCubesToZoneActions.add(host);
  }

  @Override
  public void visit(ActionSelectBoadAction host) {
    selectBoardActionActions.add(host);
  }

  @Override
  public void visit(ActionScorePoints host) {
    scorePointsActions.add(host);
  }
}
