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
import com.philbeaudoin.quebec.server.user.UserManager;
import com.philbeaudoin.quebec.shared.action.AuthenticateWithGoogleAuthorizationCodeAction;
import com.philbeaudoin.quebec.shared.action.SessionInfoResult;
import com.philbeaudoin.quebec.shared.session.SessionInfoDto;

/**
 * Handles {@link AuthenticateWithGoogleAuthorizationCodeAction}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class AuthenticateWithGoogleAuthorizationCodeHandler
    implements ActionHandler<AuthenticateWithGoogleAuthorizationCodeAction, SessionInfoResult> {

  private final Provider<UserManager> userManager;
  
  @Inject
  AuthenticateWithGoogleAuthorizationCodeHandler(Provider<UserManager> userManager) {
    this.userManager = userManager;
  }

  @Override
  public SessionInfoResult execute(AuthenticateWithGoogleAuthorizationCodeAction action,
      ExecutionContext context) throws ActionException {
    return new SessionInfoResult(new SessionInfoDto(
        userManager.get().signIntoSessionWithGoogleAuthenticationCode(
            action.getAuthorizationCode())));
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
