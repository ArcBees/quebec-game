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

import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.action.ActionSendOneWorker;
import com.philbeaudoin.quebec.shared.action.ActionSendWorkers;
import com.philbeaudoin.quebec.shared.action.ActionTakeLeaderCard;
import com.philbeaudoin.quebec.shared.state.GameState;

/**
 * Factory methods of the various renderer classes, used in assisted injection.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface InteractionFactories {
  InteractionGenerator createInteractionRenderer(GameState gameState,
      GameStateRenderer gameStateRenderer);
  InteractionMoveArchitect createInteractionMoveArchitect(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionMoveArchitect action);
  InteractionSendWorkers createInteractionSendWorkers(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionSendWorkers action);
  InteractionSendOneWorker createInteractionSendOneWorker(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionSendOneWorker host);
  InteractionTakeLeaderCard createInteractionTakeLeaderCard(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionTakeLeaderCard host);
}