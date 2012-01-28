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
import com.philbeaudoin.quebec.client.scene.Arrow;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.AcceptPossibleActions;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.action.PossibleActionsComposite;
import com.philbeaudoin.quebec.shared.action.PossibleActionsVisitor;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * Renders the possible actions.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class PossibleActionsRenderer implements PossibleActionsVisitor {

  private final InteractionFactories factories;
  private final GameState gameState;
  private final GameStateRenderer gameStateRenderer;

  @Inject
  PossibleActionsRenderer(InteractionFactories factories,
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
        possibleActions.accept(PossibleActionsRenderer.this);
      }
    });
  }

  @Override
  public void visit(ActionMoveArchitect host) {
    // TODO: Handle moving from one tile to another.

    Tile tile = host.getDestinationTile();
    Transform tileTransform = gameStateRenderer.getTileTransform(tile);
    gameStateRenderer.highlightTile(tile);

    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    SceneNodeList arrows = new SceneNodeList();

    // Arrow to move architect.
    Transform architectFrom = gameStateRenderer.getArchitectOnPlayerTransform(
        playerColor, host.isNeutralArchitect());
    Transform architectTo = gameStateRenderer.getArchitectOnTileTransform(tile);
    arrows.add(new Arrow(architectFrom.getTranslation(0), architectTo.getTranslation(0)));

    // Arrow to move passive cubes to active.
    Transform cubeFrom = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, false);
    Transform cubeTo = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, true);
    arrows.add(new Arrow(cubeFrom.getTranslation(0), cubeTo.getTranslation(0)));

    Interaction interaction = factories.getInteraction(factories.getCircleTrigger(
        tileTransform.getTranslation(0), 0.044), arrows, host);
    gameStateRenderer.addInteraction(interaction);
  }
}
