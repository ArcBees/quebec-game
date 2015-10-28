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

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.gwtplatform.dispatch.rpc.server.guice.DispatchServiceImpl;
import com.gwtplatform.dispatch.rpc.server.guice.HttpSessionSecurityCookieFilter;
import com.gwtplatform.dispatch.rpc.shared.ActionImpl;
import com.gwtplatform.dispatch.shared.SecurityCookie;
import com.philbeaudoin.quebec.server.database.ObjectifyServiceWrapper;
import com.philbeaudoin.quebec.server.database.ObjectifyServiceWrapperImpl;
import com.philbeaudoin.quebec.server.game.GameManager;
import com.philbeaudoin.quebec.server.game.GameManagerImpl;
import com.philbeaudoin.quebec.server.session.ServerSessionManager;
import com.philbeaudoin.quebec.server.session.ServerSessionManagerImpl;
import com.philbeaudoin.quebec.server.user.OAuthManager;
import com.philbeaudoin.quebec.server.user.OAuthManagerImpl;
import com.philbeaudoin.quebec.server.user.UserManager;
import com.philbeaudoin.quebec.server.user.UserManagerImpl;
import com.philbeaudoin.quebec.shared.Constants;
import com.philbeaudoin.quebec.shared.game.state.JavaRandomShuffler;
import com.philbeaudoin.quebec.shared.game.state.Shuffler;

/**
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class DispatchServletModule extends ServletModule {

  @Override
  public void configureServlets() {

    // Model object managers
    bind(HttpTransport.class).to(NetHttpTransport.class);
    bind(JsonFactory.class).to(JacksonFactory.class);
    bind(ObjectifyFilter.class).in(Singleton.class);
    bind(ObjectifyServiceWrapper.class).to(ObjectifyServiceWrapperImpl.class);
    bind(ServerSessionManager.class).to(ServerSessionManagerImpl.class);
    bind(GameManager.class).to(GameManagerImpl.class);
    bind(UserManager.class).to(UserManagerImpl.class);
    bind(OAuthManager.class).to(OAuthManagerImpl.class);
    bind(Shuffler.class).to(JavaRandomShuffler.class);

    bindConstant().annotatedWith(SecurityCookie.class).to(Constants.securityCookieName);

    filter("/*").through(ObjectifyFilter.class);
    filter("*").through(HttpSessionSecurityCookieFilter.class);
    serve("/" + ActionImpl.DEFAULT_SERVICE_NAME).with(DispatchServiceImpl.class);
  }

}
