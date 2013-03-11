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
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
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

  @UiField
  HTMLPanel googleButton;

  private MenuPresenter presenter;

  @Inject
  public MenuView() {
    widget = binder.createAndBindUi(this);
    setup();
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @Override
  public void setPresenter(MenuPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void hideGoogleButton() {
    googleButton.setVisible(false);
  }

  @Override
  public void displayError(String string) {
    Window.alert(string);
  }

  // This method
  private void authorized(String code) {
    // Successfully authorized.
    // Hide the sign-in button now that the user is authorized, for example:
    presenter.googleAuthorize(code);
  }

  private void notAuthorized(String error) {
    presenter.errorGoogleAuthorize(error);
  }

  // Setup the native JS callback method (named "signInCallback") used by the Google+ sign-in
  // process. This method defers all treatment to the above
  public native void setup() /*-{
    var obj = this;
    $wnd.signInCallback = function(authResult) {
      if (authResult['code']) {
        obj.@com.philbeaudoin.quebec.client.menu.MenuView::authorized(Ljava/lang/String;)(
            authResult['code']);
      } else if (authResult['error']) {
        obj.@com.philbeaudoin.quebec.client.menu.MenuView::notAuthorized(Ljava/lang/String;)(
            authResult['error']);
      }
    }
  }-*/;
}
