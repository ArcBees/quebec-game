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
import com.philbeaudoin.quebec.server.exceptions.OperationNotAllowedException;
import com.philbeaudoin.quebec.server.session.ServerSessionManager;
import com.philbeaudoin.quebec.server.user.OAuthManager;
import com.philbeaudoin.quebec.shared.serveractions.ChangeAdminSettingsAction;
import com.philbeaudoin.quebec.shared.serveractions.VoidResult;
import com.philbeaudoin.quebec.shared.session.SessionInfo;

/**
 * Handles {@link ChangeAdminSettingsAction}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ChangeAdminSettingsHandler
    implements ActionHandler<ChangeAdminSettingsAction, VoidResult> {

  private final Provider<OAuthManager> oAuthManager;
  private final Provider<ServerSessionManager> serverSessionManager;

  @Inject
  ChangeAdminSettingsHandler(Provider<OAuthManager> oAuthManager,
      Provider<ServerSessionManager> serverSessionManager) {
    this.oAuthManager = oAuthManager;
    this.serverSessionManager = serverSessionManager;
  }

  @Override
  public VoidResult execute(ChangeAdminSettingsAction action, ExecutionContext context)
      throws ActionException {
    SessionInfo sessionInfo = serverSessionManager.get().getSessionInfo();
    if (sessionInfo == null || !sessionInfo.isAdmin()) {
      throw new ActionException("Must be admin to change admin settings.");
    }
    try {
      if (action.getSalt() != null) {
        serverSessionManager.get().saveAdminPasswordAndSalt(
            action.getAdminPassword(),
            action.getSalt());
      } else {
        if (action.getAdminPassword() != null) {
          serverSessionManager.get().saveAdminPassword(action.getAdminPassword());
        }
      }
    } catch(OperationNotAllowedException e) {
      throw new ActionException("Must be admin to change admin password.");
    }

    try {
      if (action.getGoogleOAuthClientSecret() != null) {
        oAuthManager.get().saveClientSecret(action.getGoogleOAuthClientSecret());
      }
    } catch(OperationNotAllowedException e) {
      throw new ActionException("Must be admin to change OAuth client secret.");
    }

    return new VoidResult();
  }

  @Override
  public Class<ChangeAdminSettingsAction> getActionType() {
    return ChangeAdminSettingsAction.class;
  }

  @Override
  public void undo(ChangeAdminSettingsAction action, VoidResult result, ExecutionContext context)
      throws ActionException {
  }

}
