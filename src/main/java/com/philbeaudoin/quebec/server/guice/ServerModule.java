/**
 * Copyright 2012 Philippe Beaudoin
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

package com.philbeaudoin.quebec.server.guice;

import com.gwtplatform.dispatch.server.guice.HandlerModule;
import com.philbeaudoin.quebec.server.handlers.AuthenticateWithDummyHandler;
import com.philbeaudoin.quebec.server.handlers.ChangeAdminSettingsHandler;
import com.philbeaudoin.quebec.server.handlers.CreateNewGameHandler;
import com.philbeaudoin.quebec.server.handlers.GetSessionHandler;
import com.philbeaudoin.quebec.server.handlers.AuthenticateWithAdminPasswordHandler;
import com.philbeaudoin.quebec.server.handlers.AuthenticateWithGoogleAuthorizationCodeHandler;
import com.philbeaudoin.quebec.server.handlers.JoinGameHandler;
import com.philbeaudoin.quebec.server.handlers.ListGamesHandler;
import com.philbeaudoin.quebec.server.handlers.SignOutAdminHandler;
import com.philbeaudoin.quebec.shared.serveractions.AuthenticateWithDummyAction;
import com.philbeaudoin.quebec.shared.serveractions.ChangeAdminSettingsAction;
import com.philbeaudoin.quebec.shared.serveractions.CreateNewGameAction;
import com.philbeaudoin.quebec.shared.serveractions.GetSessionAction;
import com.philbeaudoin.quebec.shared.serveractions.AuthenticateWithAdminPasswordAction;
import com.philbeaudoin.quebec.shared.serveractions.AuthenticateWithGoogleAuthorizationCodeAction;
import com.philbeaudoin.quebec.shared.serveractions.JoinGameAction;
import com.philbeaudoin.quebec.shared.serveractions.ListGamesAction;
import com.philbeaudoin.quebec.shared.serveractions.SignOutAdminAction;

/**
 * Module which binds the handlers and configurations.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ServerModule extends HandlerModule {

  @Override
  protected void configureHandlers() {
    bindHandler(ChangeAdminSettingsAction.class, ChangeAdminSettingsHandler.class);
    bindHandler(GetSessionAction.class, GetSessionHandler.class);
    bindHandler(AuthenticateWithAdminPasswordAction.class, AuthenticateWithAdminPasswordHandler.class);
    bindHandler(AuthenticateWithGoogleAuthorizationCodeAction.class, AuthenticateWithGoogleAuthorizationCodeHandler.class);
    bindHandler(AuthenticateWithDummyAction.class, AuthenticateWithDummyHandler.class);
    bindHandler(SignOutAdminAction.class, SignOutAdminHandler.class);
    bindHandler(ListGamesAction.class, ListGamesHandler.class);
    bindHandler(CreateNewGameAction.class, CreateNewGameHandler.class);
    bindHandler(JoinGameAction.class, JoinGameHandler.class);
  }
}
