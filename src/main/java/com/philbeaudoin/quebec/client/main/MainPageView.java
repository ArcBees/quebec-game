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

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.philbeaudoin.quebec.client.sprites.SpriteList;
import com.philbeaudoin.quebec.client.sprites.SpriteResources;
import com.philbeaudoin.quebec.client.sprites.TileSprite;
import com.philbeaudoin.quebec.client.widget.FullCanvas;

/**
 * @author Philippe Beaudoin
 */
public class MainPageView extends ViewImpl implements MainPagePresenter.MyView {

  interface Binder extends UiBinder<Widget, MainPageView> { }
  protected static final Binder binder = GWT.create(Binder.class);

  // Timer refresh rate, in milliseconds.
  static final int refreshRate = 25;

  private final Widget widget;
  private final Canvas canvas;
  private final Context2d context;
  private final ImageElement board;
  private final SpriteList spriteList;

  @UiField
  FullCanvas fullCanvas;

  private MainPagePresenter presenter;

  @Inject
  public MainPageView(SpriteResources spriteResources, SpriteList spriteList) {
    widget = binder.createAndBindUi(this);
    canvas = fullCanvas.asCanvas();
    context = canvas.getContext2d();
    board = spriteResources.get(SpriteResources.Type.board).getElement();
    this.spriteList = spriteList;

    // setup timer
    final Timer timer = new Timer() {
      @Override
      public void run() {
        doUpdate();
      }
    };
    timer.scheduleRepeating(refreshRate);

    fullCanvas.addMouseMoveHandler(new MouseMoveHandler() {
      @Override
      public void onMouseMove(MouseMoveEvent event) {
        // Assumes the canvas is always wider than tall.
        double x = event.getRelativeX(fullCanvas.getElement()) / (double) canvas.getOffsetWidth();
        double y = event.getRelativeY(fullCanvas.getElement()) / (double) canvas.getOffsetWidth();
        presenter.mouseMove(x, y);
      }
    });
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @Override
  public void setPresenter(MainPagePresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void addTileSprite(TileSprite tileSprite) {
    spriteList.add(tileSprite);
  }

  @Override
  public void sendToFront(TileSprite highlightedSprite) {
    spriteList.sendToFront(highlightedSprite);
  }

  void doUpdate() {
    int width = canvas.getOffsetWidth();
    int height = canvas.getOffsetHeight();
    context.save();
    try {
      context.drawImage(board, 0, 0, width, height);
      context.scale(width, width); // Assumes the canvas is always wider than tall.
      spriteList.render(context);
    } finally {
      context.restore();
    }
  }
}
