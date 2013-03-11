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
import com.philbeaudoin.quebec.shared.serveractions.SessionInfoResult;
import com.philbeaudoin.quebec.shared.serveractions.SignOutAdminAction;
import com.philbeaudoin.quebec.shared.session.SessionInfoDto;

/**
 * Handles {@link SignOutAdmindAction}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class SignOutAdminHandler
    implements ActionHandler<SignOutAdminAction, SessionInfoResult> {

  private final Provider<ServerSessionManager> serverSessionManager;

  @Inject
  SignOutAdminHandler(Provider<ServerSessionManager> serverSessionManager) {
    this.serverSessionManager = serverSessionManager;
  }

  @Override
  public SessionInfoResult execute(SignOutAdminAction action, ExecutionContext context)
      throws ActionException {
    return new SessionInfoResult(new SessionInfoDto(serverSessionManager.get().signOutAdmin()));
  }

  @Override
  public Class<SignOutAdminAction> getActionType() {
    return SignOutAdminAction.class;
  }

  @Override
  public void undo(SignOutAdminAction action, SessionInfoResult result, ExecutionContext context)
      throws ActionException {
  }
}
