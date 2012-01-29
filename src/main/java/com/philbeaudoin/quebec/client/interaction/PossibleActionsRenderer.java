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
import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.client.utils.Animation;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.AcceptPossibleActions;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.action.ActionSendWorkers;
import com.philbeaudoin.quebec.shared.action.GameAction;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.action.PossibleActionsComposite;
import com.philbeaudoin.quebec.shared.action.PossibleActionsVisitor;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.state.TileState;
import com.philbeaudoin.quebec.shared.utils.ArcTransform;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
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
    Tile destination = host.getDestinationTile();
    gameStateRenderer.highlightTile(destination);

    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    TileState origin = gameState.findTileUnderArchitect(playerColor);

    SceneNodeList arrows = new SceneNodeList();

    Transform architectFrom = gameStateRenderer.getArchitectOnPlayerTransform(
        playerColor, host.isNeutralArchitect());
    if (origin == null) {
      // Architect starts from the player zone.
      architectFrom = gameStateRenderer.getArchitectOnPlayerTransform(
          playerColor, host.isNeutralArchitect());
    } else {
      // Architect starts from its current tile.
      architectFrom = gameStateRenderer.getArchitectOnTileTransform(origin.getTile());
    }

    // Arrow to move architect.
    Transform architectTo = gameStateRenderer.getArchitectOnTileTransform(destination);
    arrows.add(new Arrow(architectFrom.getTranslation(0), architectTo.getTranslation(0)));

    // Arrow to move passive cubes to active.
    Transform cubeFrom = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, false);
    Transform cubeTo = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, true);
    arrows.add(new Arrow(cubeFrom.getTranslation(0), cubeTo.getTranslation(0)));

    // Create scale up animation of the tile.
    addTileInteraction(host, destination, arrows);
  }

  @Override
  public void visit(ActionSendWorkers host) {
    Tile destination = host.getDestinationTile();
    gameStateRenderer.highlightTile(destination);

    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    SceneNodeList arrows = new SceneNodeList();

    Transform cubesFrom = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, true);
    Transform cubesTo = gameStateRenderer.getTileTransform(destination);

    // Arrow to move cubes.
    arrows.add(new Arrow(cubesFrom.getTranslation(0), cubesTo.getTranslation(0)));

    addTileInteraction(host, destination, arrows);
  }

  private void addTileInteraction(GameAction action, Tile destination,
      SceneNodeList arrows) {
    // Create scale up animation of the tile.
    Transform destinationTransform = gameStateRenderer.getTileTransform(destination);
    SceneNode copiedTile = gameStateRenderer.copyTile(destination);
    ConstantTransform scaledDestination = new ConstantTransform(
        destinationTransform.getTranslation(0),
        destinationTransform.getScaling(0) * 1.08,
        destinationTransform.getRotation(0));
    Animation enterAnim = new Animation(copiedTile,
        new ArcTransform(destinationTransform, scaledDestination, 0, 1.0), 0.2);
    Animation leaveAnim = new Animation(copiedTile,
        new ArcTransform(scaledDestination, destinationTransform, 0, 1.0), 0.2);

    // Create interaction.
    Interaction interaction = factories.getInteraction(factories.getCircleTrigger(
        destinationTransform.getTranslation(0), 0.044), arrows, enterAnim, leaveAnim, action);
    gameStateRenderer.addInteraction(interaction);
  }
}
