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
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.RendererFactories;
import com.philbeaudoin.quebec.client.scene.Arrow;
import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.client.scene.Sprite;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionSendWorkers;
import com.philbeaudoin.quebec.shared.state.Board;
import com.philbeaudoin.quebec.shared.state.BoardAction;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.PlayerState;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.state.TileState;
import com.philbeaudoin.quebec.shared.utils.ArcTransform;
import com.philbeaudoin.quebec.shared.utils.Callback;
import com.philbeaudoin.quebec.shared.utils.CallbackRegistration;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * This is an interaction with the game board for the action of sending workers to a tile.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionSendWorkers extends InteractionWithTile {

  private static final double BUMP_ANIM_DURATION = 0.2;

  private final SceneNodeList arrows;
  private final Transform actionDestinationTransform;
  private final Transform actionScaledTransform;
  private final SceneNode animatedActionTile;

  private CallbackRegistration actionBumpAnimRegistration;

  private InteractionSendWorkers(Scheduler scheduler, SpriteResources spriteResources,
      RendererFactories rendererFactories, GameState gameState, GameStateRenderer gameStateRenderer,
      ActionSendWorkers action, Tile destinationTile) {
    super(scheduler, rendererFactories, gameState, gameStateRenderer, action, destinationTile);
    PlayerState currentPlayer = gameState.getCurrentPlayer();
    PlayerColor playerColor = currentPlayer.getPlayer().getColor();

    arrows = new SceneNodeList();
    Transform cubesFrom = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, true);
    Transform cubesTo = gameStateRenderer.getTileTransform(destinationTile);

    // Arrow to move cubes.
    arrows.add(new Arrow(cubesFrom.getTranslation(0), cubesTo.getTranslation(0)));

    // If the action is triggered, make sure we highlight it when needed.
    TileState tileState = gameState.getTileState(action.getDestinationTile());
    if (tileState.getArchitect() != playerColor) {
      // TODO: Fancier detection of whether the action is triggered or not?
      Vector2d tileLocation = tileState.getLocation();
      BoardAction boardAction = Board.actionForTileLocation(tileLocation.getColumn(),
          tileLocation.getLine());
      actionDestinationTransform = new ConstantTransform(
          gameStateRenderer.getActionTransform(boardAction));
      actionScaledTransform = new ConstantTransform(actionDestinationTransform.getTranslation(0),
          1.08, 0);
      animatedActionTile = new Sprite(spriteResources.getAction(boardAction.getActionType()));
    } else {
      actionDestinationTransform = null;
      actionScaledTransform = null;
      animatedActionTile = null;
    }
  }

  @Inject
  public InteractionSendWorkers(Scheduler scheduler, SpriteResources spriteResources,
      RendererFactories rendererFactories, @Assisted GameState gameState,
      @Assisted GameStateRenderer gameStateRenderer, @Assisted ActionSendWorkers action) {
    this(scheduler, spriteResources, rendererFactories, gameState, gameStateRenderer, action,
        action.getDestinationTile());
  }

  @Override
  protected void doMouseEnter(double x, double y, double time) {
    if (animatedActionTile != null) {
      // Bump up action.
      ensureActionBumpAnimUnregistered();
      animatedActionTile.setTransform(new ArcTransform(actionDestinationTransform,
          actionScaledTransform, time, time + BUMP_ANIM_DURATION));
      gameStateRenderer.addToAnimationGraph(animatedActionTile);
    }
    super.doMouseEnter(x, y, time);
    gameStateRenderer.addToAnimationGraph(arrows);
  }

  @Override
  protected void doMouseLeave(double x, double y, double time) {
    if (animatedActionTile != null) {
      // Bump down.
      ensureActionBumpAnimUnregistered();
      animatedActionTile.setTransform(new ArcTransform(actionScaledTransform,
          actionDestinationTransform, time, time + BUMP_ANIM_DURATION));
      gameStateRenderer.addToAnimationGraph(animatedActionTile);

      actionBumpAnimRegistration = animatedActionTile.addAnimationCompletedCallback(new Callback() {
        @Override
        public void execute() {
          scheduler.scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
              animatedActionTile.setParent(null);
              ensureActionBumpAnimUnregistered();
            }
          });
        }
      });
    }
    super.doMouseLeave(x, y, time);
    arrows.setParent(null);
  }

  private void ensureActionBumpAnimUnregistered() {
    if (actionBumpAnimRegistration != null) {
      actionBumpAnimRegistration.unregister();
      actionBumpAnimRegistration = null;
    }
  }
}
