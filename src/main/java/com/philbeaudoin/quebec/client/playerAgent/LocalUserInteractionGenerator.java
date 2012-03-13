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

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.interaction.Helpers;
import com.philbeaudoin.quebec.client.interaction.InteractionFactories;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.MessageRenderer;
import com.philbeaudoin.quebec.client.scene.Arrow;
import com.philbeaudoin.quebec.client.scene.ComplexText;
import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionActivateCubes;
import com.philbeaudoin.quebec.shared.action.ActionEmptyTileToZone;
import com.philbeaudoin.quebec.shared.action.ActionIncreaseStar;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.action.ActionMoveCubes;
import com.philbeaudoin.quebec.shared.action.ActionPerformScoringPhase;
import com.philbeaudoin.quebec.shared.action.ActionScorePoints;
import com.philbeaudoin.quebec.shared.action.ActionSelectBoardAction;
import com.philbeaudoin.quebec.shared.action.ActionSendCubesToZone;
import com.philbeaudoin.quebec.shared.action.ActionSendWorkers;
import com.philbeaudoin.quebec.shared.action.ActionExplicit;
import com.philbeaudoin.quebec.shared.action.ActionTakeLeaderCard;
import com.philbeaudoin.quebec.shared.action.GameAction;
import com.philbeaudoin.quebec.shared.action.GameActionVisitor;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Use this class to generate the list of
 * {@link com.philbeaudoin.quebec.client.interaction.Interaction Interaction} corresponding to a
 * given {@link com.philbeaudoin.quebec.shared.action.GameAction GameAction} for a local user. All
 * the generated interactions will be added to the provided {@link GameStateRenderer}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class LocalUserInteractionGenerator implements GameActionVisitor {

  // Constants to specify text interaction location
  private static final double TEXT_PADDING = 0.03;
  private static final double TEXT_CENTER_X = GameStateRenderer.TEXT_CENTER;
  private static final double TEXT_CENTER_Y = GameStateRenderer.TEXT_LINE_2;

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
  private final ArrayList<ActionSelectBoardAction> selectBoardActionActions =
      new ArrayList<ActionSelectBoardAction>();
  private final ArrayList<ActionIncreaseStar> increaseStarActions =
      new ArrayList<ActionIncreaseStar>();
  private final ArrayList<ActionEmptyTileToZone> emptyTileToZoneActions =
      new ArrayList<ActionEmptyTileToZone>();
  private final ArrayList<ActionPerformScoringPhase> performScoringPhaseActions =
      new ArrayList<ActionPerformScoringPhase>();
  private final ArrayList<ActionMoveCubes> moveCubesActions = new ArrayList<ActionMoveCubes>();
  private final ArrayList<TextInteraction> textInteractions = new ArrayList<TextInteraction>();

  private PossibleActions generatingActions;

  @Inject
  LocalUserInteractionGenerator(InteractionFactories factories,
      Provider<MessageRenderer> messageRendererProvider,
      @Assisted GameState gameState,
      @Assisted GameStateRenderer gameStateRenderer) {
    this.factories = factories;
    this.messageRendererProvider = messageRendererProvider;
    this.gameState = gameState;
    this.gameStateRenderer = gameStateRenderer;
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
    for (ActionSelectBoardAction action : selectBoardActionActions) {
      gameStateRenderer.addInteraction(factories.createInteractionSelectBoardAction(gameState,
          gameStateRenderer, action));
    }
    selectBoardActionActions.clear();
    for (ActionIncreaseStar action : increaseStarActions) {
      gameStateRenderer.addInteraction(factories.createInteractionIncreaseStar(gameState,
          gameStateRenderer, action));
    }
    increaseStarActions.clear();
    for (ActionEmptyTileToZone action : emptyTileToZoneActions) {
      gameStateRenderer.addInteraction(factories.createInteractionEmptyTileToZone(gameState,
          gameStateRenderer, action));
    }
    emptyTileToZoneActions.clear();
    for (ActionPerformScoringPhase action : performScoringPhaseActions) {
      gameStateRenderer.addInteraction(factories.createInteractionPerformScoringPhase(gameState,
          gameStateRenderer, action));
    }
    performScoringPhaseActions.clear();
    generateMoveCubeInteractions();

    // Generate text interactions last, since other interactions may create them.
    generateTextInteractions();

    // Generate the message associated with the possible actions, if any.
    // TODO(beaudoin): Extract to a common class, duplicate in LocalAiInteractionGenerator.
    assert generatingActions != null;
    Message message = generatingActions.getMessage();
    if (message != null) {
      MessageRenderer messageRenderer = messageRendererProvider.get();
      message.accept(messageRenderer);
      gameStateRenderer.addToAnimationGraph(new ComplexText(messageRenderer.getComponents(),
          new ConstantTransform(new Vector2d(
              GameStateRenderer.TEXT_CENTER, GameStateRenderer.TEXT_LINE_1))));
    }
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

    PlayerColor currentPlayer = gameState.getCurrentPlayer().getPlayer().getColor();
    if (pairedMoveArchitects.size() == 1 && pairedMoveArchitects.get(0).isPair()) {
      // Case where we have only one pair, let the user select which architect to move.
      PairedMoveArchitect pair = pairedMoveArchitects.get(0);
      PlayerColor a1Color = pair.a1.isNeutralArchitect() ? PlayerColor.NEUTRAL : currentPlayer;
      PlayerColor a2Color = pair.a2.isNeutralArchitect() ? PlayerColor.NEUTRAL : currentPlayer;
      MessageRenderer messageRendererA1 = messageRendererProvider.get();
      MessageRenderer messageRendererA2 = messageRendererProvider.get();
      if (pair.destinationTile != null) {
        // Destination is another tile.
        new Message.MoveArchitect(a1Color).accept(messageRendererA1);
        new Message.MoveArchitect(a2Color).accept(messageRendererA2);
      } else {
        // Destination is outside the board.
        new Message.RemoveNeutralArchitect().accept(pair.a1.isNeutralArchitect() ?
            messageRendererA1 : messageRendererA2);
        new Message.MoveArchitectOut(currentPlayer).accept(pair.a1.isNeutralArchitect() ?
            messageRendererA2 : messageRendererA1);
      }
      textInteractions.add(new TextInteraction(messageRendererA1,
          Helpers.createArchitectArrow(gameState, gameStateRenderer, pair.a1),
          pair.a1));
      textInteractions.add(new TextInteraction(messageRendererA2,
          Helpers.createArchitectArrow(gameState, gameStateRenderer, pair.a2),
          pair.a2));
    } else {
      // Many pairs (or singles) let the user select the destination tile.
      for (PairedMoveArchitect pair : pairedMoveArchitects) {
        if (pair.isPair()) {
          assert pair.destinationTile != null;  // Move outside the board are only possible alone.
          // Not sure which architect to move.
          gameStateRenderer.addInteraction(factories.createInteractionMoveUnknownArchitect(
              gameState, gameStateRenderer, pair.a1, pair.a2));
        } else {
          // Only one architect to move.
          if (pair.destinationTile != null) {
            // Destination is another tile.
            gameStateRenderer.addInteraction(factories.createInteractionMoveArchitect(gameState,
                gameStateRenderer, pair.a1));
          } else {
            // Destination is outside the board.
            MessageRenderer messageRenderer = messageRendererProvider.get();
            new Message.MoveArchitectOut(currentPlayer).accept(messageRenderer);
            textInteractions.add(new TextInteraction(messageRenderer,
                Helpers.createArchitectArrow(gameState, gameStateRenderer, pair.a1),
                pair.a1));
          }
        }
      }
    }
  }

  private void generateMoveCubeInteractions() {
    // Look at move cubhe action and group them together when they start from the same location.
    ArrayList<GroupedMoveCubes> groupedMoveCubes = new ArrayList<GroupedMoveCubes>();

    for (ActionMoveCubes action : moveCubesActions) {
      boolean found = false;
      for (GroupedMoveCubes group : groupedMoveCubes) {
        if (group.originZone == action.getInfluenceZoneFrom()) {
          assert action.getNbCubes() == group.nbCubes;
          group.actions.add(action);
          found = true;
          break;
        }
      }
      if (!found) {
        groupedMoveCubes.add(new GroupedMoveCubes(action));
      }
    }
    moveCubesActions.clear();

    if (groupedMoveCubes.size() == 1) {
      // Case where we have only one group, let the user select the destination zone.
      GroupedMoveCubes group = groupedMoveCubes.get(0);
      for (ActionMoveCubes action : group.actions) {
        gameStateRenderer.addInteraction(factories.createInteractionMoveCubesToZone(
            gameState, gameStateRenderer, action));
      }
    } else {
      PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
      // Many groups, let the user select the origin zone.
      for (GroupedMoveCubes group : groupedMoveCubes) {
        PossibleActions possibleActions = new PossibleActions(
            new Message.MoveCubesSelectDestination(group.nbCubes, playerColor, group.originZone));
        for (ActionMoveCubes action : group.actions) {
          possibleActions.add(action);
        }
        gameStateRenderer.addInteraction(factories.createInteractionMoveCubesFromZone(
            gameState, gameStateRenderer, group.originZone, possibleActions));
      }
    }
  }

  private void generateTextInteractions() {
    // We only want padding between components, so remove one to compensate from the fact that we
    // add one to many in the loop.
    double totalWidth = -TEXT_PADDING;
    for (TextInteraction textInteraction : textInteractions) {
      totalWidth += textInteraction.messageRenderer.calculateApproximateSize().getX() +
          TEXT_PADDING;
    }

    double x = TEXT_CENTER_X - totalWidth / 2.0;
    for (TextInteraction textInteraction : textInteractions) {
      double width = textInteraction.messageRenderer.calculateApproximateSize().getX();
      Vector2d pos = new Vector2d(x + width / 2.0, TEXT_CENTER_Y);
      gameStateRenderer.addInteraction(factories.createInteractionText(gameState,
        gameStateRenderer, textInteraction.messageRenderer, textInteraction.extras, pos,
        textInteraction.action));
      x += width + TEXT_PADDING;
    }

    textInteractions.clear();
  }

  @Override
  public void setPossibleActions(PossibleActions generatingActions) {
    assert this.generatingActions == null;
    this.generatingActions = generatingActions;
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
  public void visit(ActionSelectBoardAction host) {
    selectBoardActionActions.add(host);
  }

  @Override
  public void visit(ActionIncreaseStar host) {
    increaseStarActions.add(host);
  }

  @Override
  public void visit(ActionMoveCubes host) {
    moveCubesActions.add(host);
  }

  @Override
  public void visit(ActionScorePoints host) {
    MessageRenderer messageRenderer = messageRendererProvider.get();
    new Message.ScorePoints(host.getNbPoints()).accept(messageRenderer);
    textInteractions.add(new TextInteraction(messageRenderer, null, host));
  }

  @Override
  public void visit(ActionExplicit host) {
    MessageRenderer messageRenderer = messageRendererProvider.get();
    host.getMessage().accept(messageRenderer);
    textInteractions.add(new TextInteraction(messageRenderer, null, host));
  }

  @Override
  public void visit(ActionPerformScoringPhase host) {
    performScoringPhaseActions.add(host);
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

  @Override
  public void visit(ActionEmptyTileToZone host) {
    emptyTileToZoneActions.add(host);
  }

  private static class PairedMoveArchitect {
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

  private static class GroupedMoveCubes {
    final int nbCubes;
    final InfluenceType originZone;
    final ArrayList<ActionMoveCubes> actions = new ArrayList<ActionMoveCubes>();
    public GroupedMoveCubes(ActionMoveCubes action) {
      nbCubes = action.getNbCubes();
      originZone = action.getInfluenceZoneFrom();
      actions.add(action);
    }
  }

  private static class TextInteraction {
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