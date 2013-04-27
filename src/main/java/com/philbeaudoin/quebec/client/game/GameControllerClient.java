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

package com.philbeaudoin.quebec.client.game;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.philbeaudoin.quebec.client.playerAgent.PlayerAgentFactories;
import com.philbeaudoin.quebec.client.playerAgent.PlayerAgentGenerator;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.GameControllerBasic;
import com.philbeaudoin.quebec.shared.game.action.GameAction;
import com.philbeaudoin.quebec.shared.game.action.GameActionLifecycle;
import com.philbeaudoin.quebec.shared.game.action.GameActionLifecycleActor;
import com.philbeaudoin.quebec.shared.game.action.PossibleActions;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.player.Player;
import com.philbeaudoin.quebec.shared.utils.Callback;

/**
 * A controller to manipulate the state of a game client-side. It sends actions to the server.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameControllerClient implements GameController {

  private final GameControllerBasic gameControllerBasic;
  private final DispatchAsync dispatcher;
  private final PlayerAgentGenerator playerAgentGenerator;
  private final GameStateRenderer gameStateRenderer;

  @Inject
  GameControllerClient(GameControllerBasic gameControllerBasic,
      DispatchAsync dispatcher,
      PlayerAgentFactories playerAgentFactories,
      @Assisted GameStateRenderer gameStateRenderer) {
    this.gameControllerBasic = gameControllerBasic;
    this.dispatcher = dispatcher;
    this.playerAgentGenerator = playerAgentFactories.createPlayerAgentGenerator(this);
    this.gameStateRenderer = gameStateRenderer;
  }

  @Override
  public void initGame(GameState gameState, List<Player> players) {
    gameControllerBasic.initGame(gameState, players);
  }

  @Override
  public void configurePossibleActions(GameState gameState) {
    gameControllerBasic.configurePossibleActions(gameState);
  }

  @Override
  public void getPossibleMoveArchitectActions(GameState gameState,
      PossibleActions possibleActions) {
    gameControllerBasic.getPossibleMoveArchitectActions(gameState, possibleActions);
  }

  @Override
  public void prepareNextCentury(GameState gameState) {
    gameControllerBasic.prepareNextCentury(gameState);
  }

  @Override
  public void performAction(GameState gameState, GameAction gameAction) {
    // TODO(beaudoin): Remove code duplication between this and the GameControllerTutorial.
    final GameActionLifecycle gameActionLifecycle = new GameActionLifecycle();
    final GameStateChange gameStateChange = gameAction.execute(this, gameState);
    final GameState stateAfter = new GameState(gameState);
    gameStateChange.apply(this, stateAfter);

    gameActionLifecycle.addActor(gameStateRenderer.createAnimationActor(stateAfter,
        gameStateChange));
    gameActionLifecycle.addActor(new GameActionLifecycleActor() {
      @Override
      public void onStart(Callback completedCallback) {
        completedCallback.execute();
      }

      @Override
      public void onFinalize(Callback completedCallback) {
        completedCallback.execute();
      }

      @Override
      public void onComplete() {
        gameStateRenderer.renderInteractions(stateAfter, playerAgentGenerator);
      }
    });

    gameActionLifecycle.start();
  }

  @Override
  public void setGameState(GameState gameState) {
    // TODO(beaudoin): Remove code duplication between this and the GameControllerTutorial.
    gameStateRenderer.render(gameState);
    gameStateRenderer.renderInteractions(gameState, playerAgentGenerator);
  }
}