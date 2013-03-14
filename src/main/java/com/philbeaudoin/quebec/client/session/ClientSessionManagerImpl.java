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

package com.philbeaudoin.quebec.client.session;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.philbeaudoin.quebec.client.session.events.AuthenticateWithAdminPassword;
import com.philbeaudoin.quebec.client.session.events.AuthenticateWithGoogleAuthorizationCode;
import com.philbeaudoin.quebec.client.session.events.AuthenticateWithGoogleAuthorizationCode.Event;
import com.philbeaudoin.quebec.client.session.events.SessionStateChanged;
import com.philbeaudoin.quebec.client.session.events.SignOutAdmin;
import com.philbeaudoin.quebec.shared.serveractions.AuthenticateWithAdminPasswordAction;
import com.philbeaudoin.quebec.shared.serveractions.AuthenticateWithGoogleAuthorizationCodeAction;
import com.philbeaudoin.quebec.shared.serveractions.GetSessionAction;
import com.philbeaudoin.quebec.shared.serveractions.SessionInfoResult;
import com.philbeaudoin.quebec.shared.serveractions.SignOutAdminAction;
import com.philbeaudoin.quebec.shared.session.SessionInfo;
import com.philbeaudoin.quebec.shared.session.SessionInfoDto;
import com.philbeaudoin.quebec.shared.user.UserInfo;

/**
 * Implementation of {@link ClientSessionManager}.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ClientSessionManagerImpl implements ClientSessionManager, SignOutAdmin.Handler,
    AuthenticateWithAdminPassword.Handler, AuthenticateWithGoogleAuthorizationCode.Handler {

  private final static int INITIAL_RETRY_DELAY_MS = 2000;
  private final static int RETRY_DELAY_PUSHBACK_MULTIPLIER = 2;
  private final static int MAX_RETRY_DELAY_MS = 10 * 60 * 1000;  // 10 minutes
  private final static double RETRY_DELAY_FUZZ_PERCENT = 0.5;

  private final EventBus eventBus;
  private final DispatchAsync dispatcher;
  private final Scheduler scheduler;

  private SessionInfo sessionInfo;
  private int retryDelayMs;

  @Inject
  public ClientSessionManagerImpl(EventBus eventBus, DispatchAsync dispatcher, Scheduler scheduler) {
    this.eventBus = eventBus;
    this.dispatcher = dispatcher;
    this.scheduler = scheduler;

    eventBus.addHandler(SignOutAdmin.Event.TYPE, this);
    eventBus.addHandler(AuthenticateWithAdminPassword.Event.TYPE, this);
    eventBus.addHandler(AuthenticateWithGoogleAuthorizationCode.Event.TYPE, this);
    getSessionFromServer();
  }

  @Override
  public boolean isAdmin() {
    return sessionInfo != null ? sessionInfo.isAdmin() : false;
  }

  @Override
  public UserInfo getUserInfo() {
    return sessionInfo != null ? sessionInfo.getUserInfo() : null;
  }

  @Override
  public boolean isSignedIn() {
    return getUserInfo() != null;
  }

  @Override
  public void onSignOutAdmin(SignOutAdmin.Event event) {
    dispatcher.execute(new SignOutAdminAction(),
                       new AsyncCallback<SessionInfoResult>() {
      @Override
      public void onFailure(Throwable caught) {
        eventBus.fireEventFromSource(new SignOutAdmin.ErrorEvent(caught.getMessage()),
                                     ClientSessionManagerImpl.this);
        scheduleGetSessionFromServer();
      }
      @Override
      public void onSuccess(SessionInfoResult result) {
        setSessionInfo(result.getSessionInfoDto());
      }
    });
  }

  @Override
  public void onAuthenticateWithAdminPassword(AuthenticateWithAdminPassword.Event event) {
    dispatcher.execute(new AuthenticateWithAdminPasswordAction(event.getPassword()),
                       new AsyncCallback<SessionInfoResult>() {
      @Override
      public void onFailure(Throwable caught) {
        clearSessionInfo();
        eventBus.fireEventFromSource(new AuthenticateWithAdminPassword.ErrorEvent(caught.getMessage()),
                                     ClientSessionManagerImpl.this);
        getSessionFromServer();
      }
      @Override
      public void onSuccess(SessionInfoResult result) {
        setSessionInfo(result.getSessionInfoDto());
      }
    });
  }

  @Override
  public void onAuthenticateWithGoogleAuthorizationCode(Event event) {
    dispatcher.execute(new AuthenticateWithGoogleAuthorizationCodeAction(event.getCode()),
                       new AsyncCallback<SessionInfoResult>() {
      @Override
      public void onFailure(Throwable caught) {
        clearSessionInfo();
        eventBus.fireEventFromSource(
            new AuthenticateWithGoogleAuthorizationCode.ErrorEvent(caught.getMessage()),
            ClientSessionManagerImpl.this);
        getSessionFromServer();
      }
      @Override
      public void onSuccess(SessionInfoResult result) {
        setSessionInfo(result.getSessionInfoDto());
      }
    });
  }

  private void clearSessionInfo() {
    setSessionInfo(new SessionInfoDto());
  }

  private void setSessionInfo(SessionInfo sessionInfo) {
    this.sessionInfo = sessionInfo;
    eventBus.fireEventFromSource(new SessionStateChanged.Event(ClientSessionManagerImpl.this),
        ClientSessionManagerImpl.this);

  }
  private void scheduleGetSessionFromServer() {
    scheduler.scheduleFixedDelay(new RepeatingCommand() {
      @Override
      public boolean execute() {
        getSessionFromServer();
        return false;  // Execute only once. If the get fails, it will be scheduled again.
      }
    }, retryDelayMs);
  }

  private void getSessionFromServer() {
    dispatcher.execute(new GetSessionAction(), new AsyncCallback<SessionInfoResult>() {
      @Override
      public void onFailure(Throwable caught) {
        clearSessionInfo();
        scheduleGetSessionFromServer();
        // Pushback the retry delay.
        retryDelayMs *= RETRY_DELAY_PUSHBACK_MULTIPLIER;
        retryDelayMs = Math.max(retryDelayMs, MAX_RETRY_DELAY_MS);
        retryDelayMs += Math.random() * RETRY_DELAY_FUZZ_PERCENT * retryDelayMs;
      }
      @Override
      public void onSuccess(SessionInfoResult result) {
        setSessionInfo(result.getSessionInfoDto());
        // Clear the retry delay.
        retryDelayMs = INITIAL_RETRY_DELAY_MS;
      }
    });
  }
}
