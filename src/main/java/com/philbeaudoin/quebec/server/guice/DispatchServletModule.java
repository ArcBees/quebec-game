/**
 * Copyright 2011 Philippe Beaudoin
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

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFactory;
import com.gwtplatform.dispatch.server.guice.DispatchServiceImpl;
import com.gwtplatform.dispatch.server.guice.HttpSessionSecurityCookieFilter;
import com.gwtplatform.dispatch.shared.ActionImpl;
import com.gwtplatform.dispatch.shared.SecurityCookie;
import com.philbeaudoin.quebec.shared.Constants;

/**
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class DispatchServletModule extends ServletModule {

  @Override
  public void configureServlets() {

    // Model object managers
    bind(ObjectifyFactory.class).in(Singleton.class);

    bindConstant().annotatedWith(SecurityCookie.class).to(Constants.securityCookieName);

    // TODO philippe.beaudoin@gmail.com
    // Uncomment when http://code.google.com/p/puzzlebazar/issues/detail?id=27 is unblocked.
    filter("*").through(HttpSessionSecurityCookieFilter.class);
    serve("/" + ActionImpl.DEFAULT_SERVICE_NAME).with(DispatchServiceImpl.class);
  }

}
