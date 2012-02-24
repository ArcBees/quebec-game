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

import javax.inject.Provider;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.RendererFactories;
import com.philbeaudoin.quebec.shared.NameTokens;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.player.AiBrainSimple;
import com.philbeaudoin.quebec.shared.player.AiBrainSimple2;
import com.philbeaudoin.quebec.shared.player.Player;
import com.philbeaudoin.quebec.shared.player.PlayerLocalAi;
import com.philbeaudoin.quebec.shared.player.PlayerLocalUser;
import com.philbeaudoin.quebec.shared.state.GameState;

/**
 * This is the presenter of the main application page.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class MainPagePresenter extends
    Presenter<MainPagePresenter.MyView, MainPagePresenter.MyProxy> {

  public static final Object TYPE_RevealNewsContent = new Object();

  private final GameStateRenderer gameStateRenderer;
  private final GameState gameState;

  /**
   * The presenter's view.
   */
  public interface MyView extends View {
    void setPresenter(MainPagePresenter presenter);
  }

  /**
   * The presenter's proxy.
   */
  @ProxyStandard
  @NameToken(NameTokens.mainPage)
  public interface MyProxy extends ProxyPlace<MainPagePresenter> {
  }

  @Inject
  public MainPagePresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
      RendererFactories rendererFactories, Provider<GameState> gameStateProvider) {
    super(eventBus, view, proxy);
    view.setPresenter(this);
    gameStateRenderer = rendererFactories.createGameStateRenderer();

    gameState = gameStateProvider.get();

    ArrayList<Player> players = new ArrayList<Player>(5);
    players.add(new PlayerLocalUser(PlayerColor.BLACK, "You"));
    players.add(new PlayerLocalAi(PlayerColor.WHITE, "HAL", new AiBrainSimple()));
    players.add(new PlayerLocalAi(PlayerColor.ORANGE, "Skynet", new AiBrainSimple2()));
    players.add(new PlayerLocalAi(PlayerColor.GREEN, "WOPR", new AiBrainSimple()));
    players.add(new PlayerLocalAi(PlayerColor.PINK, "The Matrix", new AiBrainSimple()));

    gameState.initGame(players);
    gameStateRenderer.render(gameState);
  }

  @Override
  protected void revealInParent() {
    RevealRootLayoutContentEvent.fire(this, this);
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
}
