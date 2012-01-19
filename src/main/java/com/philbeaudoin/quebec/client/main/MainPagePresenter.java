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

import java.util.ArrayList;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;
import com.philbeaudoin.quebec.client.renderer.ChangeRenderer;
import com.philbeaudoin.quebec.client.renderer.ChangeRendererGenerator;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.RendererFactories;
import com.philbeaudoin.quebec.client.scene.Arrow;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.shared.CubeDestinationInfluenceZone;
import com.philbeaudoin.quebec.shared.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.GameController;
import com.philbeaudoin.quebec.shared.GameState;
import com.philbeaudoin.quebec.shared.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.NameTokens;
import com.philbeaudoin.quebec.shared.Player;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * This is the presenter of the main application page.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class MainPagePresenter extends
    Presenter<MainPagePresenter.MyView, MainPagePresenter.MyProxy> {

  public static final Object TYPE_RevealNewsContent = new Object();

  private final GameStateRenderer gameStateRenderer;

  private final SceneNodeList dynamicRoot = new SceneNodeList();

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

  // TODO Remove dependency on SpriteResources.
  @Inject
  public MainPagePresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
      final SpriteResources spriteResources, RendererFactories rendererFactories) {
    super(eventBus, view, proxy);
    view.setPresenter(this);
    gameStateRenderer = rendererFactories.createGameStateRenderer();

    ArrayList<Player> players = new ArrayList<Player>(5);
    players.add(new Player(PlayerColor.BLACK, "Filou"));
    players.add(new Player(PlayerColor.WHITE, "Emps"));
    players.add(new Player(PlayerColor.ORANGE, "Jerome"));
    players.add(new Player(PlayerColor.GREEN, "Claudiane"));
    players.add(new Player(PlayerColor.PINK, "Bob"));

    GameState gameState = new GameState();
    GameController gameController = new GameController();
    gameController.initGame(gameState, players);
    gameState.setPlayerCubesInInfluenceZone(InfluenceType.CITADEL, PlayerColor.BLACK, 8);
    gameStateRenderer.render(gameState);

    // Dummy setup for a test.
    GameStateChangeMoveCubes changeA = new GameStateChangeMoveCubes(3,
        new CubeDestinationPlayer(PlayerColor.WHITE, false),
        new CubeDestinationInfluenceZone(InfluenceType.RELIGIOUS, PlayerColor.WHITE));
    GameStateChangeMoveCubes changeB = new GameStateChangeMoveCubes(5,
        new CubeDestinationInfluenceZone(InfluenceType.CITADEL, PlayerColor.BLACK),
        new CubeDestinationInfluenceZone(InfluenceType.RELIGIOUS, PlayerColor.BLACK));
    GameStateChangeMoveCubes changeC = new GameStateChangeMoveCubes(3,
        new CubeDestinationPlayer(PlayerColor.WHITE, true),
        new CubeDestinationPlayer(PlayerColor.WHITE, false));
    GameStateChangeComposite change = new GameStateChangeComposite();
    change.add(changeA);
    change.add(changeB);
    change.add(changeC);
    ChangeRendererGenerator generator = rendererFactories.createChangeRendererGenerator();
    generator.visit(change);

    ChangeRenderer changeRenderer = generator.getChangeRenderer();
    changeRenderer.generateAnim(gameStateRenderer, dynamicRoot, 0.0);
    changeRenderer.undoAdditions(gameStateRenderer);

    Arrow arrow = new Arrow(new Vector2d(0.7, 0.1), new Vector2d(1.699, 0.2));
    gameStateRenderer.getRoot().add(arrow);
  }

  @Override
  protected void revealInParent() {
    RevealRootLayoutContentEvent.fire(this, this);
  }

  /**
   * Called whenever the mouse is moved inside the board canvas.
   *
   * @param x The X normalized mouse position.
   * @param y The Y normalized mouse position.
   */
  public void mouseMove(double x, double y) {
    /*
    if (highlightedTile != null) {
      MutableTransform transform =
          new MutableTransform(highlightedTile.getTransform());
      transform.setScaling(1.0);
      highlightedTile.setTransform(transform);
    }

    // Make position relative to the board center.
    double boardX = x - LEFT_COLUMN_WIDTH - Board.ASPECT_RATIO / 2.0;
    double boardY = y - 0.5;
    Vector2d loc = Board.locationForPosition(boardX, boardY);
    int column = loc.getColumn();
    int line = loc.getLine();
    if (column < 0 || column >= 18 || line < 0 || line >= 8) {
      highlightedTile = null;
      return;
    }
    highlightedTile = tileGrid[column][line];
    if (highlightedTile == null) {
      return;
    }
    Vector2d center = Board.positionForLocation(column, line);
    double distX = boardX - center.getX();
    double distY = boardY - center.getY();
    double dist = distX * distX + distY * distY;
    double scaling = Math.max(1.0, 1.5 - dist * 500.0);
    MutableTransform transform =
        new MutableTransform(highlightedTile.getTransform());
    transform.setScaling(scaling);
    highlightedTile.setTransform(transform);
    boardNodes.sendToFront(highlightedTile);
    */
  }

  public void drawStaticLayer(Context2d context) {
    gameStateRenderer.getRoot().draw(0, context);
  }

  public void drawDynamicLayer(double time, Context2d context) {
    dynamicRoot.draw(time, context);
  }
}
