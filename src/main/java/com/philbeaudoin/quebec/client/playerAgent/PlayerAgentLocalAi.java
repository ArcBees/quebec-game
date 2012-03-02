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
import javax.inject.Provider;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.MessageRenderer;
import com.philbeaudoin.quebec.client.scene.ComplexText;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.player.PlayerLocalAi;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * The player agent of an Artificial Intelligence playing locally.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class PlayerAgentLocalAi implements PlayerAgent {

  private final PlayerAgentFactories playerAgentFactories;
  private final Provider<MessageRenderer> messageRendererProvider;
  private final PlayerLocalAi player;

  @Inject
  PlayerAgentLocalAi(PlayerAgentFactories playerAgentFactories,
      Provider<MessageRenderer> messageRendererProvider, @Assisted PlayerLocalAi player) {
    this.playerAgentFactories = playerAgentFactories;
    this.messageRendererProvider = messageRendererProvider;
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
      if (!generator.isManualMove()) {
        // Move automatically.
        final GameStateChange gameStateChange = player.getMove(gameState);
        if (gameStateChange != null) {
          gameStateRenderer.generateAnimFor(gameState, gameStateChange);
        }
      } else {
        // TODO(beaudoin): Duplicated in PlayerAgentLocalUser, extract.
        Message message = possibleActions.getMessage();
        if (message != null) {
          MessageRenderer messageRenderer = messageRendererProvider.get();
          message.accept(messageRenderer);
          gameStateRenderer.addToAnimationGraph(new ComplexText(messageRenderer.getComponents(),
              new ConstantTransform(new Vector2d(
                  GameStateRenderer.TEXT_CENTER, GameStateRenderer.TEXT_LINE_1))));
        }
      }
    }
  }
}
