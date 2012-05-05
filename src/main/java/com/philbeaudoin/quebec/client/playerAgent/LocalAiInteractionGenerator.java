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

package com.philbeaudoin.quebec.client.playerAgent;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.interaction.InteractionFactories;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.TextBoxRenderer;
import com.philbeaudoin.quebec.shared.action.ActionActivateCubes;
import com.philbeaudoin.quebec.shared.action.ActionEmptyTileToZone;
import com.philbeaudoin.quebec.shared.action.ActionExplicit;
import com.philbeaudoin.quebec.shared.action.ActionExplicitHighlightBoardActions;
import com.philbeaudoin.quebec.shared.action.ActionExplicitHighlightTiles;
import com.philbeaudoin.quebec.shared.action.ActionIncreaseStar;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.action.ActionMoveCubes;
import com.philbeaudoin.quebec.shared.action.ActionPerformScoringPhase;
import com.philbeaudoin.quebec.shared.action.ActionScorePoints;
import com.philbeaudoin.quebec.shared.action.ActionSelectBoardAction;
import com.philbeaudoin.quebec.shared.action.ActionSendCubesToZone;
import com.philbeaudoin.quebec.shared.action.ActionSendWorkers;
import com.philbeaudoin.quebec.shared.action.ActionTakeLeaderCard;
import com.philbeaudoin.quebec.shared.action.GameActionVisitor;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.state.GameState;

/**
 * Use this class to generate the list of
 * {@link com.philbeaudoin.quebec.client.interaction.Interaction Interaction} corresponding to a
 * given {@link com.philbeaudoin.quebec.shared.action.GameAction GameAction} for a local AI. All the
 * generated interactions will be added to the provided {@link GameStateRenderer}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class LocalAiInteractionGenerator implements GameActionVisitor {

  private final InteractionFactories factories;
  private final TextBoxRenderer textBoxRenderer;
  private final GameState gameState;
  private final GameStateRenderer gameStateRenderer;

  private PossibleActions generatingActions;
  private ActionPerformScoringPhase manualAction;

  @Inject
  LocalAiInteractionGenerator(InteractionFactories factories,
      TextBoxRenderer textBoxRenderer,
      @Assisted GameState gameState,
      @Assisted GameStateRenderer gameStateRenderer) {
    this.factories = factories;
    this.textBoxRenderer = textBoxRenderer;
    this.gameState = gameState;
    this.gameStateRenderer = gameStateRenderer;
  }

  /**
   * Generates all the interactions that correspond to the visited actions. The method should be
   * called exactly once after the possible actions have been visited.
   */
  public void generateInteractions() {
    gameStateRenderer.addInteraction(factories.createInteractionPerformScoringPhase(gameState,
        gameStateRenderer, manualAction));

    assert generatingActions != null;
    gameStateRenderer.addToAnimationGraph(
        textBoxRenderer.render(generatingActions.getTextBoxInfo(), gameStateRenderer));
  }

  /**
   * Checks whether or not the move should be performed manually, or automatically by the AI.
   * @return true if it's a manual move, false otherwise.
   */
  public boolean isManualMove() {
    return manualAction != null;
  }

  @Override
  public void setPossibleActions(PossibleActions generatingActions) {
    assert this.generatingActions == null;
    this.generatingActions  = generatingActions;
  }

  @Override
  public void visit(ActionPerformScoringPhase host) {
    assert manualAction == null;
    manualAction = host;
  }

  @Override
  public void visit(ActionSendWorkers host) {
  }

  @Override
  public void visit(ActionTakeLeaderCard host) {
  }

  @Override
  public void visit(ActionSendCubesToZone host) {
  }

  @Override
  public void visit(ActionSelectBoardAction host) {
  }

  @Override
  public void visit(ActionScorePoints host) {
  }

  @Override
  public void visit(ActionMoveArchitect host) {
  }

  @Override
  public void visit(ActionExplicit host) {
  }

  @Override
  public void visit(ActionExplicitHighlightTiles host) {
  }

  @Override
  public void visit(ActionExplicitHighlightBoardActions host) {
  }

  @Override
  public void visit(ActionActivateCubes host) {
  }

  @Override
  public void visit(ActionIncreaseStar host) {
  }

  @Override
  public void visit(ActionMoveCubes host) {
  }

  @Override
  public void visit(ActionEmptyTileToZone host) {
  }
}
