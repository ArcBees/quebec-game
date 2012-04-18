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

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.player.PlayerLocalAi;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;

/**
 * The player agent of an Artificial Intelligence playing locally.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class PlayerAgentLocalAi implements PlayerAgent {

  private final PlayerAgentFactories playerAgentFactories;
  private final PlayerLocalAi player;

  @Inject
  PlayerAgentLocalAi(PlayerAgentFactories playerAgentFactories, @Assisted PlayerLocalAi player) {
    this.playerAgentFactories = playerAgentFactories;
    this.player = player;
  }

  @Override
  public void renderInteractions(GameState gameState, GameStateRenderer gameStateRenderer) {
    // Render the possible actions.
    PossibleActions possibleActions = gameState.getPossibleActions();
    if (possibleActions != null) {
      LocalAiInteractionGenerator generator =
          playerAgentFactories.createLocalAiInteractionGenerator(gameState, gameStateRenderer);
      possibleActions.accept(generator);
      gameStateRenderer.setShowActionDescriptionOnHover(false);
      if (!generator.isManualMove()) {
        // Move automatically.
        final GameStateChange gameStateChange = player.getMove(gameState);
        if (gameStateChange != null) {
          gameStateRenderer.generateAnimFor(gameState, gameStateChange);
        }
      } else {
        generator.generateInteractions();
      }
    }
  }
}
