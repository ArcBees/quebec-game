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

package com.philbeaudoin.quebec.client.gin;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.client.gin.DispatchAsyncModule;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.philbeaudoin.quebec.client.admin.AdminPresenter;
import com.philbeaudoin.quebec.client.admin.AdminSignInPresenter;
import com.philbeaudoin.quebec.client.main.GamePresenter;
import com.philbeaudoin.quebec.client.menu.MenuPresenter;
import com.philbeaudoin.quebec.client.resources.Resources;

/**
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@GinModules({ DispatchAsyncModule.class, QuebecClientModule.class })
public interface QuebecGinjector extends Ginjector {
  PlaceManager getPlaceManager();
  EventBus getEventBus();
  Resources getResources();
  DispatchAsync getDispatcher();

  Provider<GamePresenter> getGamePresenter();
  Provider<MenuPresenter> getMenuPresenter();
  Provider<AdminSignInPresenter> getAdminSignInPresenter();
  Provider<AdminPresenter> getAdminPresenter();
}
