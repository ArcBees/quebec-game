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
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.philbeaudoin.quebec.client.sprites.RenderableList;
import com.philbeaudoin.quebec.client.sprites.SpriteResources;
import com.philbeaudoin.quebec.client.widget.FullCanvas;

/**
 * Main view, containing the board and the player information bars. The canvas is automatically
 * scaled to fill the entire window. It is mapped to a virtual coordinate space with range:
 *   (0, 1.7) x (0, 1)
 * This horizontal range is determined by the aspect ratio specified in MainPageView.ui.xml.
 *
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

  @UiField
  FullCanvas fullCanvas;

  private MainPagePresenter presenter;

  @Inject
  public MainPageView(SpriteResources spriteResources, RenderableList renderableList) {
    widget = binder.createAndBindUi(this);
    canvas = fullCanvas.asCanvas();
    context = canvas.getContext2d();

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
        double height = canvas.getOffsetHeight();
        double x = event.getRelativeX(fullCanvas.getElement()) / height;
        double y = event.getRelativeY(fullCanvas.getElement()) / height;
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

  void doUpdate() {
    if (presenter != null) {
      int height = canvas.getOffsetHeight();
      context.save();
      try {
        context.scale(height, height);
        presenter.render(context);
      } finally {
        context.restore();
      }
    }
  }
}
