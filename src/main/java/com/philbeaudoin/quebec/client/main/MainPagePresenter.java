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

package com.philbeaudoin.quebec.client.main;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;
import com.philbeaudoin.quebec.shared.NameTokens;

/**
 * This is the presenter of the main application page.
 *
 * @author Philippe Beaudoin
 */
public class MainPagePresenter
extends Presenter<MainPagePresenter.MyView, MainPagePresenter.MyProxy> {

  public static final Object TYPE_RevealNewsContent = new Object();

  /**
   * The presenter's view.
   */
  public interface MyView extends View { }

  /**
   * The presenter's proxy.
   */
  @ProxyStandard
  @NameToken(NameTokens.mainPage)
  public interface MyProxy extends ProxyPlace<MainPagePresenter> { }

  @Inject
  public MainPagePresenter(
      final EventBus eventBus,
      final MyView view,
      final MyProxy proxy) {
    super(eventBus, view, proxy);
  }

  @Override
  protected void revealInParent() {
    RevealRootLayoutContentEvent.fire(this, this);
  }
}
