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
import com.philbeaudoin.quebec.client.scene.ComplexText;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeQueuePossibleActions;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * This is an interaction with the game board for the action of moving one's architect.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionMoveUnknownArchitect extends InteractionWithAction {

  private final GameStateRenderer gameStateRenderer;
  private final SceneNodeList extras;

  @Inject
  public InteractionMoveUnknownArchitect(Scheduler scheduler, RendererFactories rendererFactories,
      InteractionFactories interactionFactories, MessageRenderer messageRenderer,
      @Assisted GameState gameState,
      @Assisted GameStateRenderer gameStateRenderer,
      @Assisted("a") ActionMoveArchitect actionArchitectA,
      @Assisted("b") ActionMoveArchitect actionArchitectB) {
    super(scheduler, rendererFactories, gameState, gameStateRenderer,
        interactionFactories.createInteractionTargetTile(gameStateRenderer, actionArchitectA),
        createGameStateChange(gameState, actionArchitectA, actionArchitectB));

    this.gameStateRenderer = gameStateRenderer;
    assert actionArchitectA.getDestinationTile() == actionArchitectB.getDestinationTile();
    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    extras = new SceneNodeList();

    // Text at the top
    new Message.MoveEitherArchitect(playerColor, PlayerColor.NEUTRAL).accept(messageRenderer);
    extras.add(new ComplexText(messageRenderer.getComponents(),
        new ConstantTransform(new Vector2d(0.8, 0.19))));

    // Arrow to move architect.
    Transform to = gameStateRenderer.getArchitectSlotOnTileTransform(
        actionArchitectA.getDestinationTile());

    Vector2d fromPos = new Vector2d(0.8, 0.195);
    Vector2d toPos = to.getTranslation(0);
    extras.add(new Arrow(fromPos, toPos));

    // Arrow to move passive cubes to active, if needed.
    if (gameState.getCurrentPlayer().getNbPassiveCubes() > 0) {
      Transform cubeFrom = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, false);
      Transform cubeTo = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, true);
      extras.add(new Arrow(cubeFrom.getTranslation(0), cubeTo.getTranslation(0)));
    }
  }

  private static GameStateChangeQueuePossibleActions createGameStateChange(
      GameState gameState, ActionMoveArchitect actionArchitectA,
      ActionMoveArchitect actionArchitectB) {

    PossibleActions possibleActions = new PossibleActions();
    possibleActions.add(actionArchitectA);
    possibleActions.add(actionArchitectB);
    return new GameStateChangeQueuePossibleActions(possibleActions);
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
}
