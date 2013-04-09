/**
 * Copyright 2013 Philippe Beaudoin
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

package com.philbeaudoin.quebec.server.handlers;

import java.util.ArrayList;

import javax.inject.Inject;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.philbeaudoin.quebec.server.game.GameControllerServer;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.GameStateResult;
import com.philbeaudoin.quebec.shared.action.LoadGameAction;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.player.AiBrainSimple;
import com.philbeaudoin.quebec.shared.player.Player;
import com.philbeaudoin.quebec.shared.player.PlayerLocalAi;
import com.philbeaudoin.quebec.shared.player.PlayerLocalUser;

/**
 * Handles {@link LoadGameAction}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class LoadGameHandler implements ActionHandler<LoadGameAction, GameStateResult> {

  private final GameControllerServer gameControllerServer;

  @Inject
  LoadGameHandler(GameControllerServer gameControllerServer) {
    this.gameControllerServer = gameControllerServer;
  }

  @Override
  public GameStateResult execute(final LoadGameAction action, ExecutionContext context)
      throws ActionException {
    // TODO(beaudoin): Just creating a dummy 4 player game here.
    GameState gameState = new GameState();
    ArrayList<Player> players = new ArrayList<Player>(4);
    players.add(new PlayerLocalUser(PlayerColor.BLACK, "You"));
    players.add(new PlayerLocalAi(PlayerColor.PINK, "Johnny 5 Server", new AiBrainSimple()));
    players.add(new PlayerLocalAi(PlayerColor.WHITE, "HAL Server", new AiBrainSimple()));
    players.add(new PlayerLocalAi(PlayerColor.ORANGE, "Skynet Server", new AiBrainSimple()));
    // Client-only game. We can start it right away.
    gameControllerServer.initGame(gameState, players);
    return new GameStateResult(gameState);
  }

  @Override
  public Class<LoadGameAction> getActionType() {
    return LoadGameAction.class;
  }

  @Override
  public void undo(LoadGameAction action, GameStateResult result, ExecutionContext context)
      throws ActionException {
    // Cannot undo.
  }

}
