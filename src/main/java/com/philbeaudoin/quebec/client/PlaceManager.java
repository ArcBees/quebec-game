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

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManagerImpl;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;
import com.philbeaudoin.quebec.shared.NameTokens;

/**
 * The place manager for Quebec.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class PlaceManager extends PlaceManagerImpl {
  @Inject
  public PlaceManager(EventBus eventBus, TokenFormatter tokenFormatter) {
    super(eventBus, tokenFormatter);
  }

  @Override
  public void revealDefaultPlace() {
    // Using false as a second parameter ensures that the URL in the browser bar
    // is not updated, so the user is able to leave the application using the
    // browser's back navigation button.
    revealPlace(new PlaceRequest(NameTokens.getMainPage()), false);
  }
}
