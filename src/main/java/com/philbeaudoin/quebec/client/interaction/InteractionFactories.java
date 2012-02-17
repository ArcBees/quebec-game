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

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.action.ActionSendCubesToZone;
import com.philbeaudoin.quebec.shared.action.ActionSendWorkers;
import com.philbeaudoin.quebec.shared.action.ActionTakeLeaderCard;
import com.philbeaudoin.quebec.shared.action.GameAction;
import com.philbeaudoin.quebec.shared.action.GameActionOnBoardAction;
import com.philbeaudoin.quebec.shared.action.HasBoardAction;
import com.philbeaudoin.quebec.shared.action.HasDestinationTile;
import com.philbeaudoin.quebec.shared.action.HasInfluenceZone;
import com.philbeaudoin.quebec.shared.action.HasLeaderCard;
import com.philbeaudoin.quebec.shared.state.GameState;

/**
 * Factory methods of the various renderer classes, used in assisted injection.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface InteractionFactories {
  InteractionGenerator createInteractionGenerator(GameState gameState,
      GameStateRenderer gameStateRenderer);
  InteractionMoveArchitect createInteractionMoveArchitect(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionMoveArchitect action);
  InteractionMoveArchitectTo createInteractionMoveArchitectTo(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionMoveArchitect action);
  InteractionMoveUnknownArchitect createInteractionMoveUnknownArchitect(GameState gameState,
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
  InteractionText createInteractionText(GameState gameState, GameStateRenderer gameStateRenderer,
      String text, GameAction action);
  InteractionTargetTile createInteractionTargetTile(
      GameStateRenderer gameStateRenderer, HasDestinationTile target);
  InteractionTargetInfluenceZone createInteractionTargetInfluenceZone(
      GameStateRenderer gameStateRenderer, HasInfluenceZone target);
  InteractionTargetLeaderCard createInteractionTargetLeaderCard(
      GameStateRenderer gameStateRenderer, HasLeaderCard target);
  InteractionTargetArchitect createInteractionTargetArchitect(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionMoveArchitect action);
  InteractionTargetBoardAction createInteractionTargetBoardAction(
      GameStateRenderer gameStateRenderer, HasBoardAction target);
  InteractionTargetText createInteractionTargetText(
      GameStateRenderer gameStateRenderer, String text);
}
