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
import javax.inject.Provider;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.MessageRenderer;
import com.philbeaudoin.quebec.client.scene.Arrow;
import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionActivateCubes;
import com.philbeaudoin.quebec.shared.action.ActionIncreaseStar;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.action.ActionScorePoints;
import com.philbeaudoin.quebec.shared.action.ActionSelectBoadAction;
import com.philbeaudoin.quebec.shared.action.ActionSendCubesToZone;
import com.philbeaudoin.quebec.shared.action.ActionSendWorkers;
import com.philbeaudoin.quebec.shared.action.ActionSkip;
import com.philbeaudoin.quebec.shared.action.ActionTakeLeaderCard;
import com.philbeaudoin.quebec.shared.action.GameAction;
import com.philbeaudoin.quebec.shared.action.GameActionVisitor;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Use this class to generate the list of {@link Interaction} corresponding to a given
 * {@link com.philbeaudoin.quebec.shared.action.GameAction GameAction}. All the generated
 * interactions will be added to the provided{@link GameStateRenderer}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionGenerator implements GameActionVisitor {

  // Constants to specify text interaction location
  private static final double TEXT_PADDING = 0.03;
  private static final double TEXT_CENTER_X = 1.05;
  private static final double TEXT_CENTER_Y = 0.136;

  private final InteractionFactories factories;
  private final Provider<MessageRenderer> messageRendererProvider;
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
  private final ArrayList<ActionIncreaseStar> increaseStarActions =
      new ArrayList<ActionIncreaseStar>();
  private final ArrayList<TextInteraction> textInteractions = new ArrayList<TextInteraction>();

  @Inject
  InteractionGenerator(InteractionFactories factories,
      Provider<MessageRenderer> messageRendererProvider,
      @Assisted GameState gameState,
      @Assisted GameStateRenderer gameStateRenderer) {
    this.factories = factories;
    this.messageRendererProvider = messageRendererProvider;
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
    for (ActionIncreaseStar action : increaseStarActions) {
      gameStateRenderer.addInteraction(factories.createInteractionIncreaseStar(gameState,
          gameStateRenderer, action));
    }
    increaseStarActions.clear();
    generateTextInteractions();
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
    moveArchitectActions.clear();

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

  private void generateTextInteractions() {
    // We only want padding between components, so remove one to compensate from the fact that we
    // add one to many in the loop.
    double totalWidth = -TEXT_PADDING;
    for (TextInteraction textInteraction : textInteractions) {
      totalWidth += textInteraction.messageRenderer.calculateApproximateWidth() + TEXT_PADDING;
    }

    double x = TEXT_CENTER_X - totalWidth / 2.0;
    for (TextInteraction textInteraction : textInteractions) {
      double width = textInteraction.messageRenderer.calculateApproximateWidth();
      Vector2d pos = new Vector2d(x + width / 2.0, TEXT_CENTER_Y);
      gameStateRenderer.addInteraction(factories.createInteractionText(gameState,
        gameStateRenderer, textInteraction.messageRenderer, textInteraction.extras, pos,
        textInteraction.action));
      x += width + TEXT_PADDING;
    }

    textInteractions.clear();
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
  public void visit(ActionIncreaseStar host) {
    increaseStarActions.add(host);
  }

  @Override
  public void visit(ActionScorePoints host) {
    MessageRenderer messageRenderer = messageRendererProvider.get();
    new Message.ScorePoints(host.getNbPoints()).accept(messageRenderer);
    textInteractions.add(new TextInteraction(messageRenderer, null, host));
  }

  @Override
  public void visit(ActionSkip host) {
    MessageRenderer messageRenderer = messageRendererProvider.get();
    new Message.Skip().accept(messageRenderer);
    textInteractions.add(new TextInteraction(messageRenderer, null, host));
  }

  @Override
  public void visit(ActionActivateCubes host) {
    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    Vector2d from = gameStateRenderer.getPlayerCubeZoneTransform(
        playerColor, false).getTranslation(0);
    Vector2d to = gameStateRenderer.getPlayerCubeZoneTransform(
        playerColor, true).getTranslation(0);

    MessageRenderer messageRenderer = messageRendererProvider.get();
    new Message.ActivateCubes(host.getNbCubes(),
        gameState.getCurrentPlayer().getPlayer().getColor()).accept(messageRenderer);
    textInteractions.add(new TextInteraction(messageRenderer, new Arrow(from, to), host));
  }

  private class TextInteraction {
    final MessageRenderer messageRenderer;
    final SceneNode extras;
    final GameAction action;
    TextInteraction(MessageRenderer messageRenderer, SceneNode extras, GameAction action) {
      this.messageRenderer = messageRenderer;
      this.extras = extras;
      this.action = action;
    }
  }
}
