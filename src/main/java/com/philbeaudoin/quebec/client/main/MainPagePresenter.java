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
import com.philbeaudoin.quebec.client.sprites.PlayerZone;
import com.philbeaudoin.quebec.client.sprites.RenderableList;
import com.philbeaudoin.quebec.client.sprites.Sprite;
import com.philbeaudoin.quebec.client.sprites.SpriteResources;
import com.philbeaudoin.quebec.client.utils.CubeGrid;
import com.philbeaudoin.quebec.client.utils.PawnStack;
import com.philbeaudoin.quebec.shared.Board;
import com.philbeaudoin.quebec.shared.BoardActionInfo;
import com.philbeaudoin.quebec.shared.InfluenceType;
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
      new Transformation(new Vector2d(LEFT_COLUMN_WIDTH + 0.5 * Board.ASPECT_RATIO, 0.5)));
  private final RenderableList tileGrid[][] = new RenderableList[18][8];
  private RenderableList highlightedTile;

  @Inject
  public MainPagePresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
      final SpriteResources spriteResources) {
    super(eventBus, view, proxy);
    view.setPresenter(this);

    PlayerZone playerZone = new PlayerZone(PlayerColor.BLACK, "Filou", LEFT_COLUMN_WIDTH, 0.15,
        new Transformation());
    playerZone.generateCubes(spriteResources);
    root.add(playerZone);
    playerZone = new PlayerZone(PlayerColor.WHITE, "Emps", LEFT_COLUMN_WIDTH, 0.15,
        new Transformation(new Vector2d(0, 0.15)));
    playerZone.generateCubes(spriteResources);
    root.add(playerZone);
    playerZone = new PlayerZone(PlayerColor.ORANGE, "Jérôme", LEFT_COLUMN_WIDTH, 0.15,
        new Transformation(new Vector2d(0, 0.30)));
    playerZone.generateCubes(spriteResources);
    root.add(playerZone);
    playerZone = new PlayerZone(PlayerColor.GREEN, "Claudiane", LEFT_COLUMN_WIDTH, 0.15,
        new Transformation(new Vector2d(0, 0.45)));
    playerZone.generateCubes(spriteResources);
    root.add(playerZone);
    playerZone = new PlayerZone(PlayerColor.PINK, "Bob", LEFT_COLUMN_WIDTH, 0.15,
        new Transformation(new Vector2d(0, 0.60)));
    playerZone.generateCubes(spriteResources);
    root.add(playerZone);

    root.add(boardRenderables);
    Sprite boardSprite = new Sprite(spriteResources.get(SpriteResources.Type.board));
    boardRenderables.add(boardSprite);

    RenderableList scoreRenderable = new RenderableList(
        new Transformation(getScorePosition(18)));
    PawnStack pawnStack = new PawnStack(5);
    for (PlayerColor playerColor : PlayerColor.values()) {
      if (playerColor == PlayerColor.NONE) {
        continue;
      }
      Sprite pawnSprite = new Sprite(spriteResources.getPawn(playerColor),
          new Transformation(pawnStack.getPosition(playerColor.ordinal() - 1)));
      scoreRenderable.add(pawnSprite);
    }
    boardRenderables.add(scoreRenderable);

    for (InfluenceType influenceType : InfluenceType.values()) {
      double x = 0.068 * influenceType.ordinal() + 0.09;
      Sprite card = new Sprite(spriteResources.getLeader(influenceType),
          new Transformation(new Vector2d(x, -0.3)));
      boardRenderables.add(card);
    }
/*
    CubeGrid zoneGrid = new CubeGrid(25, 5, 0.03);
    for(InfluenceType influenceType : InfluenceType.values()) {
      Vector2d translation;
      if (influenceType == InfluenceType.CITADEL) {
        translation = new Vector2d(0.45, -0.05);
      } else {
        int index = influenceType.ordinal();
        translation = new Vector2d(0.51 * ((index % 2 == 0) ? 1 : -1),
                                   0.35 * ((index / 2 == 0) ? 1 : -1));
      }
      RenderableList influenceRenderables = new RenderableList(
          new Transformation(translation));
      for (PlayerColor playerColor : PlayerColor.values()) {
        if (playerColor == PlayerColor.NONE) {
          continue;
        }
        for (int i = 0; i < 25; ++i) {
          Sprite cube = new Sprite(spriteResources.getCube(playerColor),
              new Transformation(zoneGrid.getPosition(i, playerColor.ordinal() - 1)));
          influenceRenderables.add(cube);
        }
      }
      boardRenderables.add(influenceRenderables);
    }
*/
    CubeGrid cubeGrid = new CubeGrid(3, 1);

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
          RenderableList cubes = new RenderableList(
              new Transformation(new Vector2d(-0.0225, 0), 1.0, -tileTransformation.getRotation()));
          tileRenderables.add(cubes);
          Sprite cubeSprite = new Sprite(spriteResources.getCube(PlayerColor.WHITE),
              new Transformation(cubeGrid.getPosition(0, 0)));
          cubes.add(cubeSprite);
          cubeSprite = new Sprite(spriteResources.getCube(PlayerColor.WHITE),
              new Transformation(cubeGrid.getPosition(1, 0)));
          cubes.add(cubeSprite);
          cubeSprite = new Sprite(spriteResources.getCube(PlayerColor.WHITE),
              new Transformation(cubeGrid.getPosition(2, 0)));
          cubes.add(cubeSprite);
          RenderableList pawn = new RenderableList(
              new Transformation(new Vector2d(0, -0.0225), 1.0, -tileTransformation.getRotation()));
          tileRenderables.add(pawn);
          Sprite pawnSprite = new Sprite(spriteResources.getPawn(PlayerColor.BLACK),
              new Transformation(new Vector2d(0, -0.01)));
          pawn.add(pawnSprite);
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

  private Vector2d getScorePosition(int score) {
    int moduloScore = score % 50;
    double x = 0.614 - (Math.min(28, moduloScore)) * 0.0439;
    double y = 0.442 - (Math.max(0, moduloScore - 28)) * 0.0416;
    if ((score / 50) % 2 == 1) {
      x *= -1.0;
      y *= -1.0;
      x -= 0.001;
      y -= 0.03;
    }
    return new Vector2d(x, y);
  }
}
