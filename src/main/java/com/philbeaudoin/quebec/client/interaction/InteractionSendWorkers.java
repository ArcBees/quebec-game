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
import com.philbeaudoin.quebec.client.renderer.RendererFactories;
import com.philbeaudoin.quebec.client.scene.Arrow;
import com.philbeaudoin.quebec.client.scene.SceneNodeAnimation;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.client.scene.Sprite;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionSendWorkers;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.state.ActionType;
import com.philbeaudoin.quebec.shared.state.Board;
import com.philbeaudoin.quebec.shared.state.BoardAction;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.PlayerState;
import com.philbeaudoin.quebec.shared.state.TileState;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * This is an interaction with the game board for the action of sending workers to a tile.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionSendWorkers extends InteractionWithAction {

  private final SceneNodeList arrows;
  private final SceneNodeAnimation actionTileAnimation;

  @Inject
  public InteractionSendWorkers(Scheduler scheduler, SpriteResources spriteResources,
      RendererFactories rendererFactories, InteractionFactories interactionFactories,
      SceneNodeAnimation.Factory sceneNodeAnimationFactory, MessageRenderer messageRenderer,
      @Assisted GameState gameState, @Assisted GameStateRenderer gameStateRenderer,
      @Assisted ActionSendWorkers action) {
    super(scheduler, rendererFactories, gameState, gameStateRenderer,
        interactionFactories.createInteractionTargetTile(gameStateRenderer, action),
        createActionMessage(messageRenderer, gameState, action), action.execute(gameState));
    PlayerState currentPlayer = gameState.getCurrentPlayer();
    PlayerColor playerColor = currentPlayer.getPlayer().getColor();

    arrows = new SceneNodeList();
    Transform cubesFrom = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, true);
    Transform cubesTo = gameStateRenderer.getTileTransform(action.getDestinationTile());

    // Arrow to move cubes.
    arrows.add(new Arrow(cubesFrom.getTranslation(0), cubesTo.getTranslation(0)));

    // If the action is triggered, make sure we highlight it when needed.
    TileState tileState = gameState.getTileState(action.getDestinationTile());
    if (action.canExecuteBoardAction(gameState)) {
      // TODO(beaudoin): Fancier detection of whether the action is triggered or not?
      Vector2d tileLocation = tileState.getLocation();
      BoardAction boardAction = Board.actionForTileLocation(tileLocation.getColumn(),
          tileLocation.getLine());
      ConstantTransform fromTransform = new ConstantTransform(
          gameStateRenderer.getActionTransform(boardAction));
      ConstantTransform toTransform = new ConstantTransform(fromTransform.getTranslation(0),
          1.08, 0);
      actionTileAnimation = sceneNodeAnimationFactory.create(gameStateRenderer, fromTransform,
          toTransform, new Sprite(spriteResources.getAction(boardAction.getActionType())));
    } else {
      actionTileAnimation = null;
    }
  }

  @Override
  protected void doMouseEnter(double x, double y, double time) {
    // Do the action animation before so that it doesn't hide the architect pawn.
    if (actionTileAnimation != null) {
      actionTileAnimation.startAnim(time);
    }
    super.doMouseEnter(x, y, time);
    gameStateRenderer.addToAnimationGraph(arrows);
  }

  @Override
  protected void doMouseLeave(double x, double y, double time) {
    // Do the action animation before so that it doesn't hide the architect pawn.
    if (actionTileAnimation != null) {
      actionTileAnimation.stopAnim(time);
    }
    super.doMouseLeave(x, y, time);
    arrows.setParent(null);
  }

  private static MessageRenderer createActionMessage(MessageRenderer messageRenderer,
      GameState gameState, ActionSendWorkers action) {
    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    TileState tileState = gameState.getTileState(action.getDestinationTile());
    if (action.canExecuteBoardAction(gameState)) {
      ActionType actionType = Board.actionForTileLocation(tileState.getLocation().getColumn(),
          tileState.getLocation().getLine()).getActionType();
      new Message.SendActiveCubesToThisTileAndExecuteAction(tileState.getCubesPerSpot(),
          playerColor, actionType).accept(messageRenderer);
    } else {
      new Message.SendActiveCubesToThisTile(tileState.getCubesPerSpot(), playerColor).accept(
          messageRenderer);
    }
    return messageRenderer;
  }
}
