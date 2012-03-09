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

package com.philbeaudoin.quebec.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * Main view, containing the board and the player information bars. The canvas is automatically
 * scaled to fill the entire window. It is mapped to a virtual coordinate space with range:
 *   (0, 1.7) x (0, 1)
 * This horizontal range is determined by the aspect ratio specified in MainPageView.ui.xml.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class MenuView extends ViewImpl implements MenuPresenter.MyView {

  interface Binder extends UiBinder<Widget, MenuView> { }
  protected static final Binder binder = GWT.create(Binder.class);

  private final Widget widget;

  @Inject
  public MenuView() {
    widget = binder.createAndBindUi(this);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }
}
