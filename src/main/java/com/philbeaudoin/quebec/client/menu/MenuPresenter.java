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

package com.philbeaudoin.quebec.client.menu;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;
import com.philbeaudoin.quebec.client.session.events.AuthenticateWithGoogleAuthorizationCode;
import com.philbeaudoin.quebec.shared.NameTokens;

/**
 * This is the presenter of the menu page.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class MenuPresenter extends
    Presenter<MenuPresenter.MyView, MenuPresenter.MyProxy> {

  public static final Object TYPE_RevealNewsContent = new Object();

  /**
   * The presenter's view.
   */
  public interface MyView extends View {
    void setPresenter(MenuPresenter presenter);
    void setGoogleButtonVisibile(boolean visibile);
    void displayError(String string);
    void renderGoogleSignIn();
  }

  /**
   * The presenter's proxy.
   */
  @ProxyStandard
  @NameToken(NameTokens.menuPage)
  public interface MyProxy extends ProxyPlace<MenuPresenter> {
  }

  @Inject
  public MenuPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy) {
    super(eventBus, view, proxy);
    view.setPresenter(this);
  }

  @Override
  public void onReveal() {
    super.onReveal();
    // Don't show the google sign-in button at first. It tries to auto-sign and if it succeeds it
    // flashes which is annoying. Instead, start by hiding it and only show it if sign-in failed.
    getView().setGoogleButtonVisibile(false);
    getView().renderGoogleSignIn();
  }

  @Override
  protected void revealInParent() {
    RevealRootLayoutContentEvent.fire(this, this);
  }

  public void googleAuthorize(String code) {
    // At that point the button may have been made visible again, so hide it.
    getView().setGoogleButtonVisibile(false);
    getEventBus().fireEventFromSource(new AuthenticateWithGoogleAuthorizationCode.Event(code), this);
  }

  public void errorGoogleAuthorize(String error) {
    // Sign-in failed, which may mean the user has to click through again. Show the button.
    getView().setGoogleButtonVisibile(true);
  }
}
