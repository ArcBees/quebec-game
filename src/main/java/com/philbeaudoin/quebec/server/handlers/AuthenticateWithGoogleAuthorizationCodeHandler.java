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
import com.philbeaudoin.quebec.server.oauth.OAuthManager;
import com.philbeaudoin.quebec.server.session.ServerSessionManager;
import com.philbeaudoin.quebec.server.user.UserInfoEntity;
import com.philbeaudoin.quebec.shared.serveractions.AuthenticateWithGoogleAuthorizationCodeAction;
import com.philbeaudoin.quebec.shared.serveractions.SessionInfoResult;
import com.philbeaudoin.quebec.shared.session.SessionInfoDto;

/**
 * Handles {@link AuthenticateWithGoogleAuthorizationCodeAction}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class AuthenticateWithGoogleAuthorizationCodeHandler
    implements ActionHandler<AuthenticateWithGoogleAuthorizationCodeAction, SessionInfoResult> {

  private final Provider<OAuthManager> oAuthManager;
  private final Provider<ServerSessionManager> serverSessionManager;
  
  @Inject
  AuthenticateWithGoogleAuthorizationCodeHandler(Provider<OAuthManager> oAuthManager,
      Provider<ServerSessionManager> serverSessionManager) {
    this.oAuthManager = oAuthManager;
    this.serverSessionManager = serverSessionManager;
  }

  @Override
  public SessionInfoResult execute(AuthenticateWithGoogleAuthorizationCodeAction action,
      ExecutionContext context) throws ActionException {
    UserInfoEntity userInfoEntity = oAuthManager.get().authenticate(action.getAuthorizationCode());
    return new SessionInfoResult(
        new SessionInfoDto(serverSessionManager.get().attachUserInfoToSession(userInfoEntity)));
  }

  @Override
  public Class<AuthenticateWithGoogleAuthorizationCodeAction> getActionType() {
    return AuthenticateWithGoogleAuthorizationCodeAction.class;
  }

  @Override
  public void undo(AuthenticateWithGoogleAuthorizationCodeAction action, SessionInfoResult result,
      ExecutionContext context)  throws ActionException {
    // Not undoable.
  }

}
