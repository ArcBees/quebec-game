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
import com.philbeaudoin.quebec.shared.action.ActionEmptyTileToZone;
import com.philbeaudoin.quebec.shared.action.ActionIncreaseStar;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.action.ActionMoveCubes;
import com.philbeaudoin.quebec.shared.action.ActionPerformScoringPhase;
import com.philbeaudoin.quebec.shared.action.ActionSendCubesToZone;
import com.philbeaudoin.quebec.shared.action.ActionSendWorkers;
import com.philbeaudoin.quebec.shared.action.ActionTakeLeaderCard;
import com.philbeaudoin.quebec.shared.action.GameAction;
import com.philbeaudoin.quebec.shared.action.GameActionOnBoardAction;
import com.philbeaudoin.quebec.shared.action.HasBoardAction;
import com.philbeaudoin.quebec.shared.action.HasDestinationTile;
import com.philbeaudoin.quebec.shared.action.HasInfluenceZone;
import com.philbeaudoin.quebec.shared.action.HasLeaderCard;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Factory methods of the various interaction classes, used in assisted injection.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface InteractionFactories {
  InteractionMoveArchitect createInteractionMoveArchitect(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionMoveArchitect action);
  InteractionMoveUnknownArchitect createInteractionMoveUnknownArchitect(
      GameState gameState,
      GameStateRenderer gameStateRenderer,
      @Assisted("a") ActionMoveArchitect actionArchitectA,
      @Assisted("b") ActionMoveArchitect actionArchitectB);
  InteractionSendWorkers createInteractionSendWorkers(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionSendWorkers action);
  InteractionSendCubesToZone createInteractionSendCubesToZone(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionSendCubesToZone action);
  InteractionTakeLeaderCard createInteractionTakeLeaderCard(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionTakeLeaderCard host);
  InteractionSelectBoardAction createInteractionSelectBoardAction(GameState gameState,
      GameStateRenderer gameStateRenderer, GameActionOnBoardAction action);
  InteractionIncreaseStar createInteractionIncreaseStar(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionIncreaseStar action);
  InteractionMoveCubesFromZone createInteractionMoveCubesFromZone(GameState gameState,
      GameStateRenderer gameStateRenderer, InfluenceType origin, Message message,
      List<Interaction> subinteractions);
  InteractionMoveCubesToZone createInteractionMoveCubesToZone(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionMoveCubes action);
  InteractionEmptyTileToZone createInteractionEmptyTileToZone(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionEmptyTileToZone action);
  InteractionPerformScoringPhase createInteractionPerformScoringPhase(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionPerformScoringPhase action);
  InteractionText createInteractionText(GameState gameState,
      GameStateRenderer gameStateRenderer, MessageRenderer messageRenderer, Highlighter highlighter,
      SceneNode extras, Vector2d pos, GameAction action);
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
