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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import com.gwtplatform.dispatch.rpc.server.ExecutionContext;
import com.gwtplatform.dispatch.rpc.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.philbeaudoin.quebec.server.game.GameInfoEntity;
import com.philbeaudoin.quebec.server.game.GameManager;
import com.philbeaudoin.quebec.shared.action.CreateNewGameAction;
import com.philbeaudoin.quebec.shared.action.GameListResult;
import com.philbeaudoin.quebec.shared.action.JoinGameAction;

/**
 * Handles {@link CreateNewGameAction}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class JoinGameHandler  implements ActionHandler<JoinGameAction, GameListResult> {

  private final Provider<GameManager> gameManager;

  @Inject
  JoinGameHandler(Provider<GameManager> gameManager) {
    this.gameManager = gameManager;
  }

  @Override
  public GameListResult execute(final JoinGameAction action, ExecutionContext context)
      throws ActionException {
    GameInfoEntity updatedGame = gameManager.get().joinGame(action.getGameId());

    List<GameInfoEntity> gameInfoEntities = gameManager.get().listOpenGames();
    gameManager.get().ensureListContainsGame(gameInfoEntities, updatedGame);
    return gameManager.get().GameInfoEntitiesToGameListResult(gameInfoEntities);
  }

  @Override
  public Class<JoinGameAction> getActionType() {
    return JoinGameAction.class;
  }

  @Override
  public void undo(JoinGameAction action, GameListResult result, ExecutionContext context)
      throws ActionException {
    // Cannot undo.
  }

}
