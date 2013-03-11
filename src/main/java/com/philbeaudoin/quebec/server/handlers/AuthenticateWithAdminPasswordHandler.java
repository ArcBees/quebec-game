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

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.philbeaudoin.quebec.server.session.ServerSessionManager;
import com.philbeaudoin.quebec.server.session.SessionInfoEntity;
import com.philbeaudoin.quebec.shared.serveractions.AuthenticateWithAdminPasswordAction;
import com.philbeaudoin.quebec.shared.serveractions.SessionInfoResult;
import com.philbeaudoin.quebec.shared.session.SessionInfoDto;

/**
 * Handles {@link AuthenticateWithAdminPasswordAction}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class AuthenticateWithAdminPasswordHandler
    implements ActionHandler<AuthenticateWithAdminPasswordAction, SessionInfoResult> {

  private final Provider<ServerSessionManager> serverSessionManager;

  @Inject
  AuthenticateWithAdminPasswordHandler(Provider<ServerSessionManager> serverSessionManager) {
    this.serverSessionManager = serverSessionManager;
  }

  @Override
  public SessionInfoResult execute(AuthenticateWithAdminPasswordAction action, ExecutionContext context)
      throws ActionException {
    SessionInfoEntity entity = serverSessionManager.get().getSessionInfoEntityGivenAdminPassword(
        action.getAdminPassword());
    if (!entity.isAdmin()) {
      throw new ActionException("Invalid admin password.");
    }
    return new SessionInfoResult(new SessionInfoDto(entity));
  }

  @Override
  public Class<AuthenticateWithAdminPasswordAction> getActionType() {
    return AuthenticateWithAdminPasswordAction.class;
  }

  @Override
  public void undo(AuthenticateWithAdminPasswordAction action, SessionInfoResult result, ExecutionContext context)
      throws ActionException {
  }
}
