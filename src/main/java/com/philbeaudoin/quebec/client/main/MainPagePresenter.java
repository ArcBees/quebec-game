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

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;
import com.philbeaudoin.quebec.client.sprites.RenderableList;
import com.philbeaudoin.quebec.client.sprites.Sprite;
import com.philbeaudoin.quebec.client.sprites.SpriteResources;
import com.philbeaudoin.quebec.shared.Board;
import com.philbeaudoin.quebec.shared.BoardActionInfo;
import com.philbeaudoin.quebec.shared.NameTokens;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.TileDeck;
import com.philbeaudoin.quebec.shared.TileInfo;
import com.philbeaudoin.quebec.shared.utils.MutableTransformation;
import com.philbeaudoin.quebec.shared.utils.Transformation;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * This is the presenter of the main application page.
 *
 * @author Philippe Beaudoin
 */
public class MainPagePresenter extends
    Presenter<MainPagePresenter.MyView, MainPagePresenter.MyProxy> {

  public static final Object TYPE_RevealNewsContent = new Object();
  public static final double LEFT_COLUMN_WIDTH = 0.38209;

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

  private final RenderableList root = new RenderableList();
  private final RenderableList boardRenderables = new RenderableList(
      new Transformation(new Vector2d(LEFT_COLUMN_WIDTH + 0.5 * Board.ASPECT_RATIO, 0.5), 1.0, 0));
  private final RenderableList tileGrid[][] = new RenderableList[18][8];
  private RenderableList highlightedTile;

  @Inject
  public MainPagePresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
      final SpriteResources spriteResources) {
    super(eventBus, view, proxy);
    view.setPresenter(this);

    root.add(boardRenderables);
    Sprite boardSprite = new Sprite(spriteResources.get(SpriteResources.Type.board));
    boardRenderables.add(boardSprite);

    TileDeck tileDeck = new TileDeck();
    for (int column = 0; column < 18; ++column) {
      for (int line = 0; line < 8; ++line) {
        BoardActionInfo actionInfo = Board.actionInfoForTileLocation(column, line);
        if (actionInfo != null) {
          TileInfo tile = tileDeck.draw(actionInfo.getInfluenceType());
          Transformation tileTransformation = getTileTransformation(column, line, 1.0);
          RenderableList tileRenderables = new RenderableList(tileTransformation);
          Sprite tileSprite = new Sprite(spriteResources.getTile(tile.getInfluenceType(),
              tile.getCentury()));
          tileRenderables.add(tileSprite);
          Sprite cubeSprite = new Sprite(spriteResources.getCube(PlayerColor.WHITE),
              new Transformation(new Vector2d(-0.0225, 0), 1.0, -tileTransformation.getRotation()));
          tileRenderables.add(cubeSprite);
          cubeSprite = new Sprite(spriteResources.getCube(PlayerColor.PINK),
              new Transformation(new Vector2d(0.0225, 0), 1.0, -tileTransformation.getRotation()));
          tileRenderables.add(cubeSprite);
          cubeSprite = new Sprite(spriteResources.getCube(PlayerColor.BLACK),
              new Transformation(new Vector2d(0, 0.0225), 1.0, -tileTransformation.getRotation()));
          tileRenderables.add(cubeSprite);
          boardRenderables.add(tileRenderables);
          tileGrid[column][line] = tileRenderables;
        }
      }
    }
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
    if (highlightedTile != null) {
      MutableTransformation transformation =
          new MutableTransformation(highlightedTile.getTransformation());
      transformation.setScaling(1.0);
      highlightedTile.setTransformation(transformation);
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
    MutableTransformation transformation =
        new MutableTransformation(highlightedTile.getTransformation());
    transformation.setScaling(scaling);
    highlightedTile.setTransformation(transformation);
    boardRenderables.sendToFront(highlightedTile);
  }

  public void render(Context2d context) {
    root.render(context);
  }

  private Transformation getTileTransformation(int column, int line, double scaling) {
    Vector2d translation = Board.positionForLocation(column, line);
    double rotation = Board.rotationAngleForLocation(column, line);
    return new Transformation(translation, scaling, rotation);
  }
}
