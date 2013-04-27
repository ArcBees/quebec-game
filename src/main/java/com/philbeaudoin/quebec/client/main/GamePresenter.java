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

package com.philbeaudoin.quebec.client.main;

import java.util.ArrayList;

import com.google.gwt.canvas.dom.client.Context2d;
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
import com.philbeaudoin.quebec.client.game.GameControllerFactories;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.session.ClientSessionManager;
import com.philbeaudoin.quebec.shared.NameTokens;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.GameStateResult;
import com.philbeaudoin.quebec.shared.action.LoadGameAction;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.player.AiBrainSimple;
import com.philbeaudoin.quebec.shared.player.Player;
import com.philbeaudoin.quebec.shared.player.PlayerLocalAi;
import com.philbeaudoin.quebec.shared.player.PlayerLocalUser;

/**
 * This is the presenter of the game itself.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GamePresenter extends
    Presenter<GamePresenter.MyView, GamePresenter.MyProxy> {

  public static final String GAME_ID_KEY = "g";
  public static final String NUMBER_OF_PLAYERS_KEY = "n";
  public static final String TUTORIAL_KEY = "t";

  private static final AiInfo[] AI_INFOS = {
    new AiInfo(PlayerColor.BLACK, "The Matrix"),
    new AiInfo(PlayerColor.PINK, "Johnny 5"),
    new AiInfo(PlayerColor.WHITE, "HAL"),
    new AiInfo(PlayerColor.ORANGE, "Skynet"),
    new AiInfo(PlayerColor.GREEN, "WOPR")
  };

  public static final Object TYPE_RevealNewsContent = new Object();

  private final PlaceManager placeManager;
  private final DispatchAsync dispatcher;
  private final ClientSessionManager sessionManager;
  private final GameStateRenderer gameStateRenderer;
  private final GameControllerFactories gameControllerFactories;

  private boolean isTutorial;
  private long gameId = -1;
  private int nbPlayers = 4;
  private GameController gameController;

  /**
   * The presenter's view.
   */
  public interface MyView extends View {
    void setPresenter(GamePresenter presenter);
    void displayError(String message);
  }

  /**
   * The presenter's proxy.
   */
  @ProxyStandard
  @NameToken(NameTokens.gamePage)
  public interface MyProxy extends ProxyPlace<GamePresenter> {
  }

  @Inject
  public GamePresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
      PlaceManager placeManager, DispatchAsync dispatcher, ClientSessionManager sessionManager,
      GameStateRenderer gameStateRenderer,
      GameControllerFactories gameControllerFactories) {
    super(eventBus, view, proxy);
    view.setPresenter(this);
    this.placeManager = placeManager;
    this.dispatcher = dispatcher;
    this.sessionManager = sessionManager;
    this.gameStateRenderer = gameStateRenderer;
    this.gameControllerFactories = gameControllerFactories;
  }

  @Override
  protected void onReveal() {
    super.onReveal();
    GameState gameState = null;
    ArrayList<Player> players = null;
    if (isTutorial) {
      assert nbPlayers == 4;
      gameId = -1;
      gameController = gameControllerFactories.createGameControllerTutorial(gameStateRenderer);
      gameState = new GameState();
      players = new ArrayList<Player>(nbPlayers);

      players.add(new PlayerLocalUser(PlayerColor.BLACK, "You"));
      for (int i = 1; i < nbPlayers; i++) {
        players.add(new PlayerLocalUser(AI_INFOS[i].color, "Opponent " + i));
      }
    } else if (gameId != -1) {
      // We have a game ID, this is a server game, load it.
      if (!sessionManager.isInitialized()) {
        placeManager.revealDefaultPlace();
        return;
      }
      gameController = gameControllerFactories.createGameControllerClient(gameStateRenderer);
      dispatcher.execute(new LoadGameAction(gameId), new AsyncGameStateCallback());
    } else {
      gameController = gameControllerFactories.createGameControllerClient(gameStateRenderer);
      gameState = new GameState();

      if (nbPlayers < 3) {
        nbPlayers = 3;
      } else if (nbPlayers > 5) {
        nbPlayers = 5;
      }
      players = new ArrayList<Player>(nbPlayers);

      players.add(new PlayerLocalUser(PlayerColor.BLACK, "You"));
      for (int i = 1; i < nbPlayers; i++) {
        players.add(new PlayerLocalAi(AI_INFOS[i].color, AI_INFOS[i].name, new AiBrainSimple()));
      }
    }
    if (gameState != null) {
      assert(gameController != null);
      // Client-only game. We can start it right away.
      gameController.initGame(gameState, players);
      gameController.setGameState(gameState);
    }
  }

  @Override
  protected void revealInParent() {
    RevealRootLayoutContentEvent.fire(this, this);
  }

  @Override
  public void prepareFromRequest(PlaceRequest request) {
    try {
      isTutorial = !request.getParameter(TUTORIAL_KEY, "0").equals("0");
      gameId = -1;
      String gameIdString = request.getParameter(GAME_ID_KEY, "-1");
      String nbPlayersString = request.getParameter(NUMBER_OF_PLAYERS_KEY, "4");
      if (isTutorial) {
        nbPlayers = 4;
      } else if (!gameIdString.equals("-1")) {
        gameId = Integer.parseInt(gameIdString);
      } else {
        nbPlayers = Integer.parseInt(nbPlayersString);
      }
    } catch (NumberFormatException e) {
      isTutorial = true;
      nbPlayers = 4;
    }
  }

  /**
   * Should be called whenever the mouse is moved inside the board canvas.
   * @param x The X normalized mouse position.
   * @param y The Y normalized mouse position.
   * @param time The current time.
   */
  public void onMouseMove(double x, double y, double time) {
    if (gameStateRenderer != null) {
      gameStateRenderer.onMouseMove(x, y, time);
    }
  }

  /**
   * Should be called whenever the mouse is clicked inside the board canvas.
   * @param x The X normalized mouse position.
   * @param y The Y normalized mouse position.
   * @param time The current time.
   */
  public void onMouseClick(double x, double y, double time) {
    if (gameStateRenderer != null) {
      gameStateRenderer.onMouseClick(x, y, time);
    }
  }

  /**
   * Draws all the static layers inside a given context.
   * @param context The context to draw into.
   */
  public void drawStaticLayers(Context2d context) {
    if (gameStateRenderer != null) {
      gameStateRenderer.drawStaticLayers(context);
    }
  }

  /**
   * Draws all the dynamic layers inside a given context.
   * @param context The context to draw into.
   */
  public void drawDynamicLayers(double time, Context2d context) {
    if (gameStateRenderer != null) {
      gameStateRenderer.drawDynamicLayers(time, context);
    }
  }

  /**
   * Checks whether the static layers need to be redrawn.
   * @return True if the static layers need to be redrawn.
   */
  public boolean isRefreshNeeded() {
    return gameStateRenderer != null && gameStateRenderer.isRefreshNeeded();
  }

  /**
   * To keep a list of AIs to instantiate.
   */
  private static class AiInfo {
    final PlayerColor color;
    final String name;
    AiInfo(PlayerColor color, String name) {
      this.color = color;
      this.name = name;
    }
  }

  /**
   * Simple {@link AsyncCallback} that recreates the game upon success.
   */
  private class AsyncGameStateCallback implements AsyncCallback<GameStateResult> {
    @Override
    public void onFailure(Throwable caught) {
      getView().displayError(caught.getMessage());
    }
    @Override
    public void onSuccess(GameStateResult result) {
      GameState gameState = result.getGameState();

      if (gameState != null) {
        assert(gameController != null);
        gameController.setGameState(gameState);
      }
    }
  }
}

