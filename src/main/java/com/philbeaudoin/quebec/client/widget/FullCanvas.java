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

package com.philbeaudoin.quebec.client.widget;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * A canvas that always resizes to take as much room as possible within its parent while respecting
 * a specified aspect ratio.
 *
 * @author Philippe Beaudoin
 */
public class FullCanvas extends Composite implements RequiresResize, HasAllMouseHandlers,
    HasResizeHandlers {

  private final double targetAspectRatio;
  private final Canvas canvas;

  /**
   * A canvas that always resizes to take as much room as possible within its parent while
   * respecting a specified aspect ratio.
   * @param targetAspectRatio The desired aspect ratio of the canvas.
   */
  @UiConstructor
  public FullCanvas(double targetAspectRatio) {
    this.targetAspectRatio = targetAspectRatio;
    canvas = Canvas.createIfSupported();
    // TODO(beaudoin): Do something if canvas cannot be created.
    initWidget(canvas);
  }

  public Canvas asCanvas() {
    return canvas;
  }

  @Override
  protected void onLoad() {
    super.onLoad();
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      @Override
      public void execute() {
        onResize();
      }
    });
  }

  @Override
  public void onResize() {
    int width = getParent().getOffsetWidth();
    int height = getParent().getOffsetHeight();
    int top = 0;
    int left = 0;
    double aspectRatio = (double) width / (double) height;
    if (targetAspectRatio > aspectRatio) {
      // Constrain canvas width.
      top = height;
      height = (int) (width / targetAspectRatio);
      top = (top - height) / 2;
    } else {
      // Constrain canvas height.
      left = width;
      width = (int) (height * targetAspectRatio);
      left = (left - width) / 2;
    }
    canvas.getElement().getStyle().setPropertyPx("top", top);
    canvas.getElement().getStyle().setPropertyPx("left", left);
    canvas.setPixelSize(width, height);
    canvas.setCoordinateSpaceWidth(width);
    canvas.setCoordinateSpaceHeight(height);

    ResizeEvent.fire(this, width, height);
  }

  @Override
  public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
    return canvas.addMouseDownHandler(handler);
  }

  @Override
  public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
    return canvas.addMouseUpHandler(handler);
  }

  @Override
  public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
    return canvas.addMouseOutHandler(handler);
  }

  @Override
  public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
    return canvas.addMouseOverHandler(handler);
  }

  @Override
  public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
    return canvas.addMouseMoveHandler(handler);
  }

  @Override
  public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
    return canvas.addMouseWheelHandler(handler);
  }

  @Override
  public HandlerRegistration addResizeHandler(ResizeHandler handler) {
    return addHandler(handler, ResizeEvent.getType());
  }
}
