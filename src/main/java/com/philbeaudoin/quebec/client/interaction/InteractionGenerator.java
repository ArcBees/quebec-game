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
import com.philbeaudoin.quebec.shared.action.ActionSendWorkers;
import com.philbeaudoin.quebec.shared.action.ActionSkip;
import com.philbeaudoin.quebec.shared.action.ActionTakeLeaderCard;
import com.philbeaudoin.quebec.shared.action.GameActionVisitor;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.Tile;

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
  private final ArrayList<ActionTakeLeaderCard> takeLeaderCardActions =
      new ArrayList<ActionTakeLeaderCard>();
  private final ArrayList<ActionSendCubesToZone> sendCubesToZoneActions =
      new ArrayList<ActionSendCubesToZone>();
  private final ArrayList<ActionSelectBoadAction> selectBoardActionActions =
      new ArrayList<ActionSelectBoadAction>();
  private final ArrayList<ActionScorePoints> scorePointsActions =
      new ArrayList<ActionScorePoints>();
  private final ArrayList<ActionSkip> skipActions = new ArrayList<ActionSkip>();

  @Inject
  InteractionGenerator(InteractionFactories factories,
      @Assisted GameState gameState,
      @Assisted GameStateRenderer gameStateRenderer) {
    this.factories = factories;
    this.gameState = gameState;
    this.gameStateRenderer = gameStateRenderer;
  }

  private class PairedMoveArchitect {
    final Tile destinationTile;
    final ActionMoveArchitect a1;
    ActionMoveArchitect a2;
    public PairedMoveArchitect(ActionMoveArchitect action) {
      destinationTile = action.getDestinationTile();
      a1 = action;
    }
    public boolean isPair() {
      return a2 != null;
    }
  }

  /**
   * Generates all the interactions that correspond to the visited actions. The method should be
   * called exactly once after the possible actions have been visited.
   */
  public void generateInteractions() {

    generateMoveArchitectInteractions();

    for (ActionSendWorkers action : sendWorkersActions) {
      gameStateRenderer.addInteraction(factories.createInteractionSendWorkers(gameState,
          gameStateRenderer, action));
    }
    sendWorkersActions.clear();
    for (ActionTakeLeaderCard action : takeLeaderCardActions) {
      gameStateRenderer.addInteraction(factories.createInteractionTakeLeaderCard(gameState,
          gameStateRenderer, action));
    }
    takeLeaderCardActions.clear();
    for (ActionSendCubesToZone action : sendCubesToZoneActions) {
      gameStateRenderer.addInteraction(factories.createInteractionSendCubesToZone(gameState,
          gameStateRenderer, action));
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
    scorePointsActions.clear();
    for (ActionSkip action : skipActions) {
      // TODO(beaudoin): Internationalize.
      gameStateRenderer.addInteraction(factories.createInteractionText(gameState,
          gameStateRenderer, "Skip", action));
    }
    skipActions.clear();
  }

  private void generateMoveArchitectInteractions() {
    // Look at move architect action and pair them if possible. Actions are paired if they send
    // the regular and the neutral architect to the same tile.
    ArrayList<PairedMoveArchitect> pairedMoveArchitects = new ArrayList<PairedMoveArchitect>();
    for (ActionMoveArchitect action : moveArchitectActions) {
      boolean found = false;
      for (PairedMoveArchitect pair : pairedMoveArchitects) {
        if (pair.destinationTile == action.getDestinationTile()) {
          pair.a2 = action;
          found = true;
          break;
        }
      }
      if (!found) {
        pairedMoveArchitects.add(new PairedMoveArchitect(action));
      }
    }

    if (pairedMoveArchitects.size() == 1 && pairedMoveArchitects.get(0).isPair()) {
      // Case where we have only one pair, let the user select which architect to move.
      PairedMoveArchitect pair = pairedMoveArchitects.get(0);
      gameStateRenderer.addInteraction(factories.createInteractionMoveArchitectTo(
          gameState, gameStateRenderer, pair.a1));
      gameStateRenderer.addInteraction(factories.createInteractionMoveArchitectTo(
          gameState, gameStateRenderer, pair.a2));
    } else {
      // Many pairs (maybe they're not pairs) let the user select the destination tile.
      for (PairedMoveArchitect pair : pairedMoveArchitects) {
        if (pair.isPair()) {
          // Not sure which architect to move.
          gameStateRenderer.addInteraction(factories.createInteractionMoveUnknownArchitect(
              gameState, gameStateRenderer, pair.a1, pair.a2));
        } else {
          // Only one architect to move.
          gameStateRenderer.addInteraction(factories.createInteractionMoveArchitect(gameState,
              gameStateRenderer, pair.a1));
        }
      }
    }
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

  @Override
  public void visit(ActionSkip host) {
    skipActions.add(host);
  }
}
