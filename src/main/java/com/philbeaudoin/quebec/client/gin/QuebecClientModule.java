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

package com.philbeaudoin.quebec.client.gin;

import com.gwtplatform.dispatch.shared.SecurityCookie;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.philbeaudoin.quebec.client.PlaceManager;
import com.philbeaudoin.quebec.client.main.MainPagePresenter;
import com.philbeaudoin.quebec.client.main.MainPageView;
import com.philbeaudoin.quebec.shared.Constants;

/**
 * @author Philippe Beaudoin
 */
public class QuebecClientModule extends AbstractPresenterModule {

  @Override
  protected void configure() {
    bindConstant().annotatedWith(SecurityCookie.class).to(Constants.securityCookieName);

    install(new DefaultModule(PlaceManager.class));

    bindPresenter(MainPagePresenter.class, MainPagePresenter.MyView.class, MainPageView.class,
        MainPagePresenter.MyProxy.class);
  }
}
