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
import java.util.List;

import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.MessageRenderer;
import com.philbeaudoin.quebec.client.scene.Arrow;
import com.philbeaudoin.quebec.client.scene.ComplexText;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * This is an interaction with the game board for the action of moving one's architect.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionMoveUnknownArchitect extends InteractionWithSubinteraction {

  private final GameStateRenderer gameStateRenderer;
  private final SceneNodeList extras;

  @Inject
  public InteractionMoveUnknownArchitect(Scheduler scheduler,
      InteractionFactories interactionFactories, Provider<MessageRenderer> messageRendererProvider,
      @Assisted GameState gameState,
      @Assisted GameStateRenderer gameStateRenderer,
      @Assisted("a") ActionMoveArchitect actionArchitectA,
      @Assisted("b") ActionMoveArchitect actionArchitectB,
      @Assisted GameController gameController) {
    super(scheduler, gameState, gameStateRenderer,
        interactionFactories.createInteractionTargetTile(gameStateRenderer, actionArchitectA),
        createSubinteactions(messageRendererProvider, interactionFactories, gameState,
            gameStateRenderer, actionArchitectA, actionArchitectB, gameController));

    this.gameStateRenderer = gameStateRenderer;
    assert actionArchitectA.getDestinationTile() == actionArchitectB.getDestinationTile();
    PlayerColor playerColor = gameState.getCurrentPlayer().getColor();
    extras = new SceneNodeList();

    // Text at the top
    MessageRenderer messageRenderer = messageRendererProvider.get();
    new Message.MoveEitherArchitect(playerColor, PlayerColor.NEUTRAL).accept(messageRenderer);
    extras.add(new ComplexText(messageRenderer.getComponents(),
        new ConstantTransform(new Vector2d(0.8, 0.18))));

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

  private static List<Interaction> createSubinteactions(
      Provider<MessageRenderer> messageRendererProvider,
      InteractionFactories factories,
      GameState gameState,
      GameStateRenderer gameStateRenderer,
      ActionMoveArchitect actionArchitectA,
      ActionMoveArchitect actionArchitectB,
      GameController gameController) {

    List<MessageRenderer> messageRenderers = new ArrayList<MessageRenderer>(2);
    messageRenderers.add(messageRendererProvider.get());
    messageRenderers.add(messageRendererProvider.get());

    generateSelectArchitectMessageRenderers(actionArchitectA, actionArchitectB,
        messageRenderers.get(0), messageRenderers.get(1), gameState.getCurrentPlayer().getColor());

    List<Vector2d> positions = Helpers.calculateTextInteractionLocations(messageRenderers);
    assert positions.size() == 2;
    List<Interaction> subinteractions = new ArrayList<Interaction>(2);
    subinteractions.add(factories.createInteractionText(gameState, gameStateRenderer,
        messageRenderers.get(0), null,
        Helpers.createArchitectArrow(gameState, gameStateRenderer, actionArchitectA),
        positions.get(0), actionArchitectA, gameController));
    subinteractions.add(factories.createInteractionText(gameState, gameStateRenderer,
        messageRenderers.get(1), null,
        Helpers.createArchitectArrow(gameState, gameStateRenderer, actionArchitectB),
        positions.get(1), actionArchitectB, gameController));

    return subinteractions;
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


  /**
   * Generates two {@link TextIn
   */
  public static void generateSelectArchitectMessageRenderers(
      ActionMoveArchitect actionArchitectA,
      ActionMoveArchitect actionArchitectB,
      MessageRenderer messageRendererA,
      MessageRenderer messageRendererB,
      PlayerColor currentPlayer) {
    // Case where we have only one pair, let the user select which architect to move.
    PlayerColor colorA = actionArchitectA.isNeutralArchitect() ? PlayerColor.NEUTRAL : currentPlayer;
    PlayerColor colorB = actionArchitectB.isNeutralArchitect() ? PlayerColor.NEUTRAL : currentPlayer;
    if (actionArchitectA.getDestinationTile() != null) {
      // Destination is another tile.
      new Message.MoveArchitect(colorA).accept(messageRendererA);
      new Message.MoveArchitect(colorB).accept(messageRendererB);
    } else {
      // Destination is outside the board.
      new Message.RemoveNeutralArchitect().accept(actionArchitectA.isNeutralArchitect() ?
          messageRendererA : messageRendererA);
      new Message.MoveArchitectOut(currentPlayer).accept(actionArchitectA.isNeutralArchitect() ?
          messageRendererB : messageRendererB);
    }
  }
}
