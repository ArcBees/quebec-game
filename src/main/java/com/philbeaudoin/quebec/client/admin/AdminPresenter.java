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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.dispatch.shared.DispatchAsync;
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
import com.philbeaudoin.quebec.client.session.events.SessionStateChanged;
import com.philbeaudoin.quebec.client.session.events.SignOutAdmin;
import com.philbeaudoin.quebec.shared.NameTokens;
import com.philbeaudoin.quebec.shared.serveractions.ChangeAdminSettingsAction;
import com.philbeaudoin.quebec.shared.serveractions.VoidResult;

/**
 * This is the presenter of the menu page.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class AdminPresenter extends Presenter<AdminPresenter.MyView, AdminPresenter.MyProxy> 
    implements SessionStateChanged.Handler {

  public static final Object TYPE_RevealNewsContent = new Object();

  /**
   * The presenter's view.
   */
  public interface MyView extends View {
    void setPresenter(AdminPresenter presenter);

    void setMessage(String message);

    void clear();

    void resetAndFocus();

    String getPassword1();
    String getPassword2();
    String getSalt();
    String getGoogleOAuthClientSecret();
    boolean getChangeSaltCheckBox();
  }

  /**
   * The presenter's proxy.
   */
  @ProxyStandard
  @NameToken(NameTokens.adminPage)
  public interface MyProxy extends ProxyPlace<AdminPresenter> {
  }

  private final DispatchAsync dispatcher;
  private final PlaceManager placeManager;
  private final Scheduler scheduler;
  private final ClientSessionManager sessionManager;

  private HandlerRegistration sessionStateChangedRegistration;

  @Inject
  public AdminPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
      DispatchAsync dispatcher, PlaceManager placeManager, Scheduler scheduler,
      ClientSessionManager sessionManager) {
    super(eventBus, view, proxy);
    this.dispatcher = dispatcher;
    this.placeManager = placeManager;
    this.scheduler = scheduler;
    this.sessionManager = sessionManager;
    view.setPresenter(this);
  }

  @Override
  public void onReveal() {
    super.onReveal();
    assert(sessionStateChangedRegistration == null);
    getView().resetAndFocus();
    if (!sessionManager.isAdmin()) {
      // Not admin, go back.
      navigateToAdminSignInPage();
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
    if (!sessionManager.isAdmin()) {
      navigateToAdminSignInPage();
    }
  }

  public void changeAdminSettings() {
    String password1 = getView().getPassword1();
    String password2 = getView().getPassword2();
    boolean shouldChangeSalt = getView().getChangeSaltCheckBox();
    String salt = getView().getSalt();
    String googleOAuthClientSecret = getView().getGoogleOAuthClientSecret();

    if (!password1.equals(password2)) {
      getView().setMessage("Passwords don't match!");
      return;
    }

    if (password1.length() == 0)
      password1 = null;
    if (password2.length() == 0)
      password2 = null;
    if (salt.length() == 0)
      salt = null;
    if (googleOAuthClientSecret.length() == 0)
      googleOAuthClientSecret = null;

    if (salt != null && !shouldChangeSalt) {
      getView().setMessage("You must check the salt check box to change it.");
      return;
    }

    if (salt == null && shouldChangeSalt) {
      getView().setMessage("You cannot use an empty salt.");
      return;
    }

    if (salt != null && password1 == null) {
      getView().setMessage("You must change the password if you change the salt.");
      return;
    }

    dispatcher.execute(new ChangeAdminSettingsAction(password1, salt, googleOAuthClientSecret),
                       new AsyncCallback<VoidResult>() {
      @Override
      public void onFailure(Throwable caught) {
        getView().setMessage(caught.getMessage());
      }
      @Override
      public void onSuccess(VoidResult result) {
        getView().resetAndFocus();
        getView().setMessage("Settings successfully changed.");
      }
    });
  }

  public void signOutAdmin() {
    getEventBus().fireEventFromSource(new SignOutAdmin.Event(), this);
  }

  private void navigateToAdminSignInPage() {
    placeManager.revealPlace(new PlaceRequest(NameTokens.adminSignInPage));
  }
}
