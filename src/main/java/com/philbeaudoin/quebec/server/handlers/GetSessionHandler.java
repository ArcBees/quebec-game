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
import javax.inject.Provider;

import com.gwtplatform.dispatch.rpc.server.ExecutionContext;
import com.gwtplatform.dispatch.rpc.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.philbeaudoin.quebec.server.session.ServerSessionManager;
import com.philbeaudoin.quebec.shared.action.GetSessionAction;
import com.philbeaudoin.quebec.shared.action.SessionInfoResult;
import com.philbeaudoin.quebec.shared.session.SessionInfoDto;

/**
 * Handles {@link GetSessionAction}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GetSessionHandler implements ActionHandler<GetSessionAction, SessionInfoResult> {

  private final Provider<ServerSessionManager> serverSessionManager;

  @Inject
  GetSessionHandler(Provider<ServerSessionManager> serverSessionManager) {
    this.serverSessionManager = serverSessionManager;
  }

  @Override
  public SessionInfoResult execute(GetSessionAction action, ExecutionContext context)
      throws ActionException {
    return new SessionInfoResult(new SessionInfoDto(serverSessionManager.get().getSessionInfo()));
  }

  @Override
  public Class<GetSessionAction> getActionType() {
    return GetSessionAction.class;
  }

  @Override
  public void undo(GetSessionAction action, SessionInfoResult result, ExecutionContext context)
      throws ActionException {
    // Not undoable.
  }
}
