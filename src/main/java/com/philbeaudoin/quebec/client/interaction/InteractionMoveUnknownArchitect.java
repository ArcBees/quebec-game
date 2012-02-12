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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.scene.Arrow;
import com.philbeaudoin.quebec.client.scene.ComplexText;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * This is an interaction with the game board for the action of moving one's architect.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionMoveUnknownArchitect extends InteractionImpl {

  private final Scheduler scheduler;
  private final InteractionFactories interactionFactories;
  private final SpriteResources spriteResources;
  private final GameState gameState;
  private final GameStateRenderer gameStateRenderer;
  private final ActionMoveArchitect actionRealArchitect;
  private final ActionMoveArchitect actionNeutralArchitect;
  private final SceneNodeList extras;

  @Inject
  public InteractionMoveUnknownArchitect(Scheduler scheduler,
      InteractionFactories interactionFactories, SpriteResources spriteResources,
      @Assisted GameState gameState,
      @Assisted GameStateRenderer gameStateRenderer,
      @Assisted("real") ActionMoveArchitect actionRealArchitect,
      @Assisted("neutral") ActionMoveArchitect actionNeutralArchitect) {
    super(interactionFactories.createInteractionTargetTile(gameStateRenderer, actionRealArchitect));

    this.scheduler = scheduler;
    this.interactionFactories = interactionFactories;
    this.spriteResources = spriteResources;
    this.gameState = gameState;
    this.gameStateRenderer = gameStateRenderer;
    this.actionRealArchitect = actionRealArchitect;
    this.actionNeutralArchitect = actionNeutralArchitect;
    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    extras = new SceneNodeList();

    // TODO(beaudoin): Internationalize.
    // Text at the top
    ArrayList<ComplexText.Component> components = new ArrayList<ComplexText.Component>();
    components.add(new ComplexText.TextComponent("Move "));
    components.add(new ComplexText.SpriteComponent(spriteResources.getPawn(playerColor), 0.6, 0));
    components.add(new ComplexText.TextComponent(" or "));
    components.add(new ComplexText.SpriteComponent(spriteResources.getPawn(PlayerColor.NEUTRAL),
        0.6, 0));
    extras.add(new ComplexText(components, new ConstantTransform(new Vector2d(1.05, 0.1))));

    // Arrow to move architect.
    Transform to = gameStateRenderer.getArchitectSlotOnTileTransform(
        actionRealArchitect.getDestinationTile());

    Vector2d fromPos = new Vector2d(1.05, 0.105);
    Vector2d toPos = to.getTranslation(0);
    extras.add(new Arrow(fromPos, toPos));

    // Arrow to move passive cubes to active, if needed.
    if (gameState.getCurrentPlayer().getNbPassiveCubes() > 0) {
      Transform cubeFrom = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, false);
      Transform cubeTo = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, true);
      extras.add(new Arrow(cubeFrom.getTranslation(0), cubeTo.getTranslation(0)));
    }
  }

  @Override
  protected void doMouseEnter(double x, double y, double time) {
    super.doMouseEnter(x, y, time);
    gameStateRenderer.addToAnimationGraph(extras);
  }

  @Override
  protected void doMouseLeave(double x, double y, double time) {
    super.doMouseLeave(x, y, time);
    extras.setParent(null);
  }

  @Override
  public void onMouseClick(double x, double y, double time) {
    if (getTrigger().triggerAt(x, y)) {
      gameStateRenderer.clearAnimationGraph();

      scheduler.scheduleDeferred(new ScheduledCommand() {
        @Override
        public void execute() {
          gameStateRenderer.clearInteractions();
          gameStateRenderer.removeAllHighlights();

          // TODO(beaudoin): Internationalize.
          // Text at the top
          PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
          ArrayList<ComplexText.Component> components = new ArrayList<ComplexText.Component>();
          components.add(new ComplexText.TextComponent("Select "));
          components.add(new ComplexText.SpriteComponent(spriteResources.getPawn(playerColor), 0.6,
              0));
          components.add(new ComplexText.TextComponent(" or "));
          components.add(new ComplexText.SpriteComponent(
              spriteResources.getPawn(PlayerColor.NEUTRAL), 0.6, 0));
          gameStateRenderer.addToAnimationGraph(new ComplexText(components,
              new ConstantTransform(new Vector2d(1.05, 0.1))));

          Interaction interaction = interactionFactories.createInteractionMoveArchitectTo(
              gameState, gameStateRenderer, actionRealArchitect);
          gameStateRenderer.addInteraction(interaction);
          interaction.highlight();
          interaction = interactionFactories.createInteractionMoveArchitectTo(
              gameState, gameStateRenderer, actionNeutralArchitect);
          gameStateRenderer.addInteraction(interaction);
          interaction.highlight();
        }
      });
    }
  }
}
