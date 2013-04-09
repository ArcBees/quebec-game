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

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;
import com.philbeaudoin.quebec.client.main.GamePresenter;
import com.philbeaudoin.quebec.client.session.ClientSessionManager;
import com.philbeaudoin.quebec.client.session.events.AuthenticateWithDummy;
import com.philbeaudoin.quebec.client.session.events.AuthenticateWithGoogleAuthorizationCode;
import com.philbeaudoin.quebec.client.session.events.AuthenticateWithGoogleAuthorizationCode.ErrorEvent;
import com.philbeaudoin.quebec.client.session.events.SessionStateChanged;
import com.philbeaudoin.quebec.client.session.events.SessionStateChanged.Event;
import com.philbeaudoin.quebec.shared.NameTokens;
import com.philbeaudoin.quebec.shared.action.CreateNewGameAction;
import com.philbeaudoin.quebec.shared.action.GameListResult;
import com.philbeaudoin.quebec.shared.action.JoinGameAction;
import com.philbeaudoin.quebec.shared.action.ListGamesAction;
import com.philbeaudoin.quebec.shared.game.GameInfoDto;
import com.philbeaudoin.quebec.shared.game.GameInfoForGameList;
import com.philbeaudoin.quebec.shared.user.UserInfo;

/**
 * This is the presenter of the menu page.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class MenuPresenter extends
    Presenter<MenuPresenter.MyView, MenuPresenter.MyProxy> implements SessionStateChanged.Handler,
    AuthenticateWithGoogleAuthorizationCode.ErrorHandler {

  public static final Object TYPE_RevealNewsContent = new Object();

  /**
   * The presenter's view.
   */
  public interface MyView extends View {
    void setPresenter(MenuPresenter presenter);
    void setCreateLinksVisible(boolean visible);
    void setGoogleButtonVisible(boolean visibile);
    void displayError(String string);
    void renderGoogleSignIn();
    void clearGames();
    void addGame(GameInfoForGameList gameInfo);
    void changeGameInfo(GameInfoForGameList gameInfo);
  }

  /**
   * The presenter's proxy.
   */
  @ProxyStandard
  @NameToken(NameTokens.menuPage)
  public interface MyProxy extends ProxyPlace<MenuPresenter> {
  }

  private final PlaceManager placeManager;
  private final DispatchAsync dispatcher;
  private final ClientSessionManager sessionManager;

  @Inject
  public MenuPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
      PlaceManager placeManager, DispatchAsync dispatcher, ClientSessionManager sessionManager) {
    super(eventBus, view, proxy);
    this.placeManager = placeManager;
    this.dispatcher = dispatcher;
    this.sessionManager = sessionManager;
    view.setPresenter(this);
  }

  @Override
  public void onBind() {
    addRegisteredHandler(SessionStateChanged.Event.TYPE, this);
  }

  @Override
  public void onReveal() {
    super.onReveal();
    // Don't show the google sign-in button at first. It tries to auto-sign and if it succeeds it
    // flashes which is annoying. Instead, start by hiding it and only show it if sign-in failed.
    getView().setGoogleButtonVisible(false);
    getView().renderGoogleSignIn();
    getView().clearGames();
    refreshGames();
  }

  @Override
  protected void revealInParent() {
    RevealRootLayoutContentEvent.fire(this, this);
  }

  @Override
  public void onHide() {
    super.onHide();
    getView().clearGames();
  }

  public void googleAuthorize(String code) {
    // At that point the button may have been made visible again, so hide it.
    getView().setGoogleButtonVisible(false);
    getEventBus().fireEventFromSource(new AuthenticateWithGoogleAuthorizationCode.Event(code), this);
  }

  public void errorGoogleAuthorize(String error) {
    // Sign-in failed, which may mean the user has to click through again. Show the button.
    getView().setGoogleButtonVisible(true);
  }

  // TODO(beaudoin): Remove only for testing.
  public void signAsDummy() {
    getEventBus().fireEventFromSource(new AuthenticateWithDummy.Event(), this);
  }

  @Override
  public void onAuthenticateWithGoogleAuthorizationCodeError(ErrorEvent event) {
    // Sign-in failed, which may mean the user has to click through again. Show the button.
    getView().setGoogleButtonVisible(true);
  }

  @Override
  public void onSessionStateChanged(Event event) {
    getView().setGoogleButtonVisible(!event.getSessionInfo().isSignedIn());
    getView().setCreateLinksVisible(event.getSessionInfo().isSignedIn());
    refreshGames();
  }

  public void refreshGames() {
    if (sessionManager.isInitialized()) {
      dispatcher.execute(new ListGamesAction(), new AsyncGameListCallback());
    }
  }

  public void createNewGame(int nbPlayers) {
    if (nbPlayers < 3 || nbPlayers > 5) {
      getView().displayError("Error! Only games with 3, 4 or 5 players allowed.");
      return;
    }
    if (!sessionManager.isSignedIn()) {
      getView().displayError("Error! Must be signed in to create a game.");
      return;
    }
    dispatcher.execute(new CreateNewGameAction(nbPlayers), new AsyncGameListCallback());
  }

  public void joinGame(GameInfoForGameList gameInfo) {
    if (checkSignedIn()) {
      getView().changeGameInfo(new GameInfoForGameList(gameInfo.getGameInfoDto(),
          gameInfo.getIndex(), GameInfoForGameList.State.JOINING));
      dispatcher.execute(new JoinGameAction(gameInfo.getId()), new AsyncGameListCallback());
    }
  }

  public void playGame(GameInfoForGameList gameInfo) {
    if (checkSignedIn()) {
      PlaceRequest myRequest = new PlaceRequest(NameTokens.gamePage);
      myRequest = myRequest.with(GamePresenter.GAME_ID_KEY, "1");
      placeManager.revealPlace( myRequest );
    }
  }

  public void viewGame(GameInfoForGameList gameInfo) {
    if (checkSignedIn()) {
      PlaceRequest myRequest = new PlaceRequest(NameTokens.gamePage);
      myRequest = myRequest.with(GamePresenter.GAME_ID_KEY, "1");
      placeManager.revealPlace( myRequest );
    }
  }

  private boolean checkSignedIn() {
    if (!sessionManager.isSignedIn()) {
      getView().displayError("Error! Must be signed in to join a game.");
      return false;
    }
    return true;
  }

  private void updateGameList(ArrayList<GameInfoDto> games) {
    getView().clearGames();
    UserInfo userInfo = sessionManager.getUserInfo();
    int index = 0;
    for (GameInfoDto gameInfo : games) {
      GameInfoForGameList.State state = GameInfoForGameList.State.NO_ACTION;
      if (userInfo != null) {
        if (gameInfo.isMoveOfPlayer(userInfo.getId())) {
          state = GameInfoForGameList.State.CAN_PLAY;
        } else if (gameInfo.canView(userInfo.getId())) {
          state = GameInfoForGameList.State.CAN_VIEW;
        } else if (gameInfo.canJoin(userInfo.getId())) {
          state = GameInfoForGameList.State.CAN_JOIN;
        }
      }
      getView().addGame(new GameInfoForGameList(gameInfo, index, state));
      index++;
    }
  }

  /**
   * Simple {@link AsyncCallback} that refreshes the game list upon success.
   */
  private class AsyncGameListCallback implements AsyncCallback<GameListResult> {
    @Override
    public void onFailure(Throwable caught) {
      getView().displayError(caught.getMessage());
    }
    @Override
    public void onSuccess(GameListResult result) {
      updateGameList(result.getGames());
    }
  }
}
