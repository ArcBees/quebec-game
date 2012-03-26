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
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.RendererFactories;
import com.philbeaudoin.quebec.shared.NameTokens;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.player.AiBrainSimple;
import com.philbeaudoin.quebec.shared.player.Player;
import com.philbeaudoin.quebec.shared.player.PlayerLocalAi;
import com.philbeaudoin.quebec.shared.player.PlayerLocalUser;
import com.philbeaudoin.quebec.shared.state.GameControllerStandard;
import com.philbeaudoin.quebec.shared.state.GameControllerTutorial;
import com.philbeaudoin.quebec.shared.state.GameState;

/**
 * This is the presenter of the game itself.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GamePresenter extends
    Presenter<GamePresenter.MyView, GamePresenter.MyProxy> {

  private static final AiInfo[] AI_INFOS = {
    new AiInfo(PlayerColor.BLACK, "The Matrix"),
    new AiInfo(PlayerColor.PINK, "Johnny 5"),
    new AiInfo(PlayerColor.WHITE, "HAL"),
    new AiInfo(PlayerColor.ORANGE, "Skynet"),
    new AiInfo(PlayerColor.GREEN, "WOPR")
  };

  public static final Object TYPE_RevealNewsContent = new Object();

  private final GameStateRenderer gameStateRenderer;

  private boolean isTutorial;
  private int nbPlayers = 4;

  /**
   * The presenter's view.
   */
  public interface MyView extends View {
    void setPresenter(GamePresenter presenter);
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
      RendererFactories rendererFactories) {
    super(eventBus, view, proxy);
    view.setPresenter(this);
    gameStateRenderer = rendererFactories.createGameStateRenderer();
  }

  @Override
  protected void onReveal() {
    super.onReveal();
    GameState gameState;
    ArrayList<Player> players;
    if (isTutorial) {
      assert nbPlayers == 4;
      gameState = new GameState(new GameControllerTutorial());
      players = new ArrayList<Player>(nbPlayers);

      players.add(new PlayerLocalUser(PlayerColor.BLACK, "You"));
      for (int i = 1; i < nbPlayers; i++) {
        players.add(new PlayerLocalUser(AI_INFOS[i].color, "Opponent " + i));
      }

    } else {
      gameState = new GameState(new GameControllerStandard());

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
    gameState.initGame(players);
    gameStateRenderer.render(gameState);
  }

  @Override
  protected void revealInParent() {
    RevealRootLayoutContentEvent.fire(this, this);
  }

  @Override
  public void prepareFromRequest(PlaceRequest request) {
    try {
      isTutorial = !request.getParameter("t", "0").equals("0");
      if (isTutorial) {
        nbPlayers = 4;
      } else {
        nbPlayers = Integer.parseInt(request.getParameter("n", "4"));
      }
    } catch (NumberFormatException e) {
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
    gameStateRenderer.onMouseMove(x, y, time);
  }

  /**
   * Should be called whenever the mouse is clicked inside the board canvas.
   * @param x The X normalized mouse position.
   * @param y The Y normalized mouse position.
   * @param time The current time.
   */
  public void onMouseClick(double x, double y, double time) {
    gameStateRenderer.onMouseClick(x, y, time);
  }

  /**
   * Draws all the static layers inside a given context.
   * @param context The context to draw into.
   */
  public void drawStaticLayers(Context2d context) {
    gameStateRenderer.drawStaticLayers(context);
  }

  /**
   * Draws all the dynamic layers inside a given context.
   * @param context The context to draw into.
   */
  public void drawDynamicLayers(double time, Context2d context) {
    gameStateRenderer.drawDynamicLayers(time, context);
  }

  /**
   * Checks whether the static layers need to be redrawn.
   * @return True if the static layers need to be redrawn.
   */
  public boolean isRefreshNeeded() {
    return gameStateRenderer.isRefreshNeeded();
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
}

