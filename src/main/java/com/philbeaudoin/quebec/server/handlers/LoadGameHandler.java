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

import javax.inject.Inject;

import com.gwtplatform.dispatch.rpc.server.ExecutionContext;
import com.gwtplatform.dispatch.rpc.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.philbeaudoin.quebec.server.game.GameManager;
import com.philbeaudoin.quebec.shared.action.GameStateResult;
import com.philbeaudoin.quebec.shared.action.LoadGameAction;

/**
 * Handles {@link LoadGameAction}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class LoadGameHandler implements ActionHandler<LoadGameAction, GameStateResult> {

  private final GameManager gameManager;

  @Inject
  LoadGameHandler(GameManager gameManager) {
    this.gameManager = gameManager;
  }

  @Override
  public GameStateResult execute(final LoadGameAction action, ExecutionContext context)
      throws ActionException {
    return new GameStateResult(gameManager.loadGame(action.getGameId()));
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
