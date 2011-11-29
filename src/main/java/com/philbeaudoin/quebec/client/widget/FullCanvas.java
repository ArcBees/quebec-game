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
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * This panel is only used to control other images.
 *
 * @author Philippe Beaudoin
 */
public class FullCanvas extends Composite implements RequiresResize {

  private final double targetAspectRatio;
  private final Canvas canvas;

  /**
   * Protected constructor. Use {@link #createIfSupported()} to create a Canvas.
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
  }
}
