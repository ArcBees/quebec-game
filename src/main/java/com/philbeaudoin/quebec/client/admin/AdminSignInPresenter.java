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

package com.philbeaudoin.quebec.client.admin;

import com.google.gwt.core.client.Scheduler;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;
import com.philbeaudoin.quebec.client.events.RemoveHandlerCommand;
import com.philbeaudoin.quebec.client.session.ClientSessionManager;
import com.philbeaudoin.quebec.client.session.events.AuthenticateWithAdminPassword;
import com.philbeaudoin.quebec.client.session.events.AuthenticateWithAdminPassword.ErrorEvent;
import com.philbeaudoin.quebec.client.session.events.SessionStateChanged;
import com.philbeaudoin.quebec.shared.NameTokens;

/**
 * This is the presenter of the menu page.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class AdminSignInPresenter extends
    Presenter<AdminSignInPresenter.MyView, AdminSignInPresenter.MyProxy>
    implements SessionStateChanged.Handler, AuthenticateWithAdminPassword.ErrorHandler {

  public static final Object TYPE_RevealNewsContent = new Object();

  /**
   * The presenter's view.
   */
  public interface MyView extends View {
    void setPresenter(AdminSignInPresenter presenter);
    void setError(String error);
    void clear();
    void resetAndFocus();
  }

  /**
   * The presenter's proxy.
   */
  @ProxyStandard
  @NameToken(NameTokens.adminSignInPage)
  public interface MyProxy extends ProxyPlace<AdminSignInPresenter> {
  }

  private final PlaceManager placeManager;
  private final Scheduler scheduler;
  private final ClientSessionManager sessionManager;

  private HandlerRegistration sessionStateChangedRegistration;

  @Inject
  public AdminSignInPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
      PlaceManager placeManager, Scheduler scheduler, ClientSessionManager sessionManager) {
    super(eventBus, view, proxy);
    this.placeManager = placeManager;
    this.scheduler = scheduler;
    this.sessionManager = sessionManager;
    view.setPresenter(this);
  }

  @Override
  public void onBind() {
    registerHandler(getEventBus().addHandler(AuthenticateWithAdminPassword.ErrorEvent.TYPE, this));
  }

  @Override
  public void onReveal() {
    super.onReveal();
    assert(sessionStateChangedRegistration == null);
    getView().resetAndFocus();
    if (sessionManager.isAdmin()) {
      // Already admin, don't stop here.
      navigateToAdminPage();
    } else {
      // Wait until we become admin somehow.
      sessionStateChangedRegistration = getEventBus().addHandler(
          SessionStateChanged.Event.TYPE, this);
    }
  }

  @Override
  public void onHide() {
    super.onHide();
    getView().clear();
    if (sessionStateChangedRegistration != null) {
      scheduler.scheduleDeferred(new RemoveHandlerCommand(sessionStateChangedRegistration));
    }
    sessionStateChangedRegistration = null;
  }

  @Override
  protected void revealInParent() {
    RevealRootLayoutContentEvent.fire(this, this);
  }

  @Override
  public void onSessionStateChanged(SessionStateChanged.Event event) {
    if (sessionManager.isAdmin()) {
      navigateToAdminPage();
    }
  }

  @Override
  public void onAuthenticateWithAdminPasswordError(ErrorEvent event) {
    getView().setError(event.getError());
  }

  public void authenticateWithAdminPassword(String adminPassword) {
    getEventBus().fireEventFromSource(new AuthenticateWithAdminPassword.Event(adminPassword), this);
  }

  private void navigateToAdminPage() {
    placeManager.revealPlace(new PlaceRequest(NameTokens.adminPage));
  }
}
