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

import javax.inject.Provider;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;
import com.philbeaudoin.quebec.client.sprites.TileSprite;
import com.philbeaudoin.quebec.shared.Board;
import com.philbeaudoin.quebec.shared.BoardActionInfo;
import com.philbeaudoin.quebec.shared.NameTokens;
import com.philbeaudoin.quebec.shared.TileDeck;
import com.philbeaudoin.quebec.shared.TileInfo;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * This is the presenter of the main application page.
 *
 * @author Philippe Beaudoin
 */
public class MainPagePresenter extends
    Presenter<MainPagePresenter.MyView, MainPagePresenter.MyProxy> {

  public static final Object TYPE_RevealNewsContent = new Object();

  /**
   * The presenter's view.
   */
  public interface MyView extends View {
    void setPresenter(MainPagePresenter presenter);
    void addTileSprite(TileSprite tileSprite);
    void sendToFront(TileSprite highlightedSprite);
  }

  /**
   * The presenter's proxy.
   */
  @ProxyStandard
  @NameToken(NameTokens.mainPage)
  public interface MyProxy extends ProxyPlace<MainPagePresenter> {
  }

  private final TileSprite spriteGrid[][] = new TileSprite[18][8];

  private TileSprite highlightedSprite;

  @Inject
  public MainPagePresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
      Provider<TileSprite> spriteProvider) {
    super(eventBus, view, proxy);
    view.setPresenter(this);

    TileDeck tileDeck = new TileDeck();
    for (int column = 0; column < 18; ++column) {
      for (int line = 0; line < 8; ++line) {
        BoardActionInfo actionInfo = Board.actionInfoForTileLocation(column, line);
        if (actionInfo != null) {
          TileSprite tileSprite = spriteProvider.get();
          TileInfo tile = tileDeck.draw(actionInfo.getInfluenceType());
          tileSprite.setTile(tile.getInfluenceType(), tile.getCentury());
          Vector2d pos = Board.positionForLocation(column, line);
          tileSprite.setPos(pos.getX(), pos.getY());
          tileSprite.setAngle(Board.rotationAngleForLocation(column, line));
          spriteGrid[column][line] = tileSprite;
          view.addTileSprite(tileSprite);
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
    if (highlightedSprite != null) {
      highlightedSprite.setSizeFactor(1.0);
    }
    Vector2d loc = Board.locationForPosition(x, y);
    int column = loc.getColumn();
    int line = loc.getLine();
    if (column < 0 || column >= 18 || line < 0 || line >= 8) {
      highlightedSprite = null;
      return;
    }
    highlightedSprite = spriteGrid[column][line];
    if (highlightedSprite == null) {
      return;
    }
    Vector2d center = Board.positionForLocation(column, line);
    double distX = x - center.getX();
    double distY = y - center.getY();
    double dist = distX * distX + distY * distY;
    double scale = Math.max(1.0, 1.5 - dist * 500.0);
    highlightedSprite.setSizeFactor(scale);
    getView().sendToFront(highlightedSprite);
  }
}
