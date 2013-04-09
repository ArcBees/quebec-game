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
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.action.GameAction;
import com.philbeaudoin.quebec.shared.game.action.PossibleActions;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.player.PlayerLocalAi;

/**
 * The player agent of an Artificial Intelligence playing locally.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class PlayerAgentLocalAi implements PlayerAgent {

  private final PlayerAgentFactories playerAgentFactories;
  private final PlayerLocalAi player;
  private final GameController gameController;

  @Inject
  PlayerAgentLocalAi(PlayerAgentFactories playerAgentFactories, @Assisted PlayerLocalAi player,
      @Assisted GameController gameController) {
    this.playerAgentFactories = playerAgentFactories;
    this.player = player;
    this.gameController = gameController;
  }

  @Override
  public void renderInteractions(GameState gameState,
      GameStateRenderer gameStateRenderer) {
    // Render the possible actions.
    PossibleActions possibleActions = gameState.getPossibleActions();
    if (possibleActions != null) {
      LocalAiInteractionGenerator generator =
          playerAgentFactories.createLocalAiInteractionGenerator(gameState, gameStateRenderer);
      possibleActions.accept(generator);
      gameStateRenderer.setShowActionDescriptionOnHover(false);
      if (!generator.isManualMove()) {
        // Move automatically.
        final GameAction gameAction = player.getMove(gameController, gameState);
        if (gameAction != null) {
          gameStateRenderer.generateAnimFor(gameState, gameAction);
        }
      } else {
        generator.generateInteractions();
      }
    }
  }
}
