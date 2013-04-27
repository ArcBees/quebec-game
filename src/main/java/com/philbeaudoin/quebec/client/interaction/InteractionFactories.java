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

import java.util.List;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.MessageRenderer;
import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.action.ActionEmptyTileToZone;
import com.philbeaudoin.quebec.shared.game.action.ActionIncreaseStar;
import com.philbeaudoin.quebec.shared.game.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.game.action.ActionMoveCubes;
import com.philbeaudoin.quebec.shared.game.action.ActionPerformScoringPhase;
import com.philbeaudoin.quebec.shared.game.action.ActionSendCubesToZone;
import com.philbeaudoin.quebec.shared.game.action.ActionSendWorkers;
import com.philbeaudoin.quebec.shared.game.action.ActionTakeLeaderCard;
import com.philbeaudoin.quebec.shared.game.action.GameAction;
import com.philbeaudoin.quebec.shared.game.action.GameActionOnBoardAction;
import com.philbeaudoin.quebec.shared.game.action.HasBoardAction;
import com.philbeaudoin.quebec.shared.game.action.HasDestinationTile;
import com.philbeaudoin.quebec.shared.game.action.HasInfluenceZone;
import com.philbeaudoin.quebec.shared.game.action.HasLeaderCard;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Factory methods of the various interaction classes, used in assisted injection.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface InteractionFactories {
  InteractionMoveArchitect createInteractionMoveArchitect(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionMoveArchitect action,
      GameController gameController);
  InteractionMoveUnknownArchitect createInteractionMoveUnknownArchitect(
      GameState gameState,
      GameStateRenderer gameStateRenderer,
      @Assisted("a") ActionMoveArchitect actionArchitectA,
      @Assisted("b") ActionMoveArchitect actionArchitectB,
      GameController gameController);
  InteractionSendWorkers createInteractionSendWorkers(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionSendWorkers action,
      GameController gameController);
  InteractionSendCubesToZone createInteractionSendCubesToZone(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionSendCubesToZone action,
      GameController gameController);
  InteractionTakeLeaderCard createInteractionTakeLeaderCard(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionTakeLeaderCard host,
      GameController gameController);
  InteractionSelectBoardAction createInteractionSelectBoardAction(GameState gameState,
      GameStateRenderer gameStateRenderer, GameActionOnBoardAction action,
      GameController gameController);
  InteractionIncreaseStar createInteractionIncreaseStar(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionIncreaseStar action,
      GameController gameController);
  InteractionMoveCubesFromZone createInteractionMoveCubesFromZone(GameState gameState,
      GameStateRenderer gameStateRenderer, InfluenceType origin, Message message,
      List<Interaction> subinteractions);
  InteractionMoveCubesToZone createInteractionMoveCubesToZone(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionMoveCubes action,
      GameController gameController);
  InteractionEmptyTileToZone createInteractionEmptyTileToZone(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionEmptyTileToZone action,
      GameController gameController);
  InteractionPerformScoringPhase createInteractionPerformScoringPhase(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionPerformScoringPhase action,
      GameController gameController);
  InteractionText createInteractionText(GameState gameState,
      GameStateRenderer gameStateRenderer, MessageRenderer messageRenderer, Highlighter highlighter,
      SceneNode extras, Vector2d pos, GameAction action,
      GameController gameController);
  InteractionTargetTile createInteractionTargetTile(
      GameStateRenderer gameStateRenderer, HasDestinationTile target);
  InteractionTargetInfluenceZone createInteractionTargetInfluenceZone(
      GameStateRenderer gameStateRenderer, HasInfluenceZone target);
  InteractionTargetLeaderCard createInteractionTargetLeaderCard(
      GameStateRenderer gameStateRenderer, HasLeaderCard target);
  InteractionTargetBoardAction createInteractionTargetBoardAction(
      GameStateRenderer gameStateRenderer, HasBoardAction target);
  InteractionTargetText createInteractionTargetText(
      GameStateRenderer gameStateRenderer, MessageRenderer messageRenderer, Vector2d pos);
  ActionDescriptionInteraction createActionDescriptionInteraction(
      GameStateRenderer gameStateRenderer);
}
