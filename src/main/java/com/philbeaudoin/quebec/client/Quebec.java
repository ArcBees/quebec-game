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

package com.philbeaudoin.quebec.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

import com.gwtplatform.mvp.client.DelayedBindRegistry;
import com.philbeaudoin.quebec.client.gin.QuebecGinjector;

/**
 * Entry point of the application.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class Quebec implements EntryPoint {
  public final QuebecGinjector ginjector = GWT.create(QuebecGinjector.class);

  public void onModuleLoad() {
    DelayedBindRegistry.bind(ginjector);
    ginjector.getResources().style().ensureInjected();
    ginjector.getPlaceManager().revealCurrentPlace();
  }

}
