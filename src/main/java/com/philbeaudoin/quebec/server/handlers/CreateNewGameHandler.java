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
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.philbeaudoin.quebec.server.session.ServerSessionManager;
import com.philbeaudoin.quebec.shared.game.GameInfo;
import com.philbeaudoin.quebec.shared.game.GameInfoDto;
import com.philbeaudoin.quebec.shared.serveractions.CreateNewGameAction;
import com.philbeaudoin.quebec.shared.serveractions.GameListResult;
import com.philbeaudoin.quebec.shared.session.SessionInfo;
import com.philbeaudoin.quebec.shared.user.UserInfo;

/**
 * Handles {@link CreateNewGameAction}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class CreateNewGameHandler
    implements ActionHandler<CreateNewGameAction, GameListResult> {

  private final Provider<ServerSessionManager> serverSessionManager;

  @Inject
  CreateNewGameHandler(Provider<ServerSessionManager> serverSessionManager) {
    this.serverSessionManager = serverSessionManager;
  }

  @Override
  public GameListResult execute(final CreateNewGameAction action, ExecutionContext context)
      throws ActionException {
    SessionInfo sessionInfo = serverSessionManager.get().getSessionInfo();
    if (sessionInfo == null || !sessionInfo.isSignedIn()) {
      throw new ActionException("Must be signed in to create a game.");
    }
    // TODO(beaudoin): Fix, hacky, just to test.
    List<GameInfoDto> games = new ArrayList<GameInfoDto>(1);
    games.add(new GameInfoDto(new GameInfo() {
      @Override
      public UserInfo getPlayerInfo(int index) {
        return index == 0 ? serverSessionManager.get().getSessionInfo().getUserInfo() : null;
      }
      @Override
      public int getNbPlayers() {
        return action.getNbPlayers();
      }
    }));
    return new GameListResult(games);
  }

  @Override
  public Class<CreateNewGameAction> getActionType() {
    return CreateNewGameAction.class;
  }

  @Override
  public void undo(CreateNewGameAction action, GameListResult result, ExecutionContext context)
      throws ActionException {
    // Cannot undo.
  }

}
