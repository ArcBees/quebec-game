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
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.philbeaudoin.quebec.shared.game.GameInfo;
import com.philbeaudoin.quebec.shared.user.UserInfo;

/**
 * View of the main menu.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class MenuView extends ViewImpl implements MenuPresenter.MyView {

  interface Binder extends UiBinder<Widget, MenuView> { }
  protected static final Binder binder = GWT.create(Binder.class);

  private final Widget widget;

  @UiField FlowPanel createLinks;
  @UiField Hyperlink new3p;
  @UiField Hyperlink new4p;
  @UiField Hyperlink new5p;
  @UiField Hyperlink signAsDummy;  // TODO(beaudoin): Remove, only for testing.
  @UiField FlowPanel gameList;
  @UiField HTMLPanel googleButton;

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
  public void setCreateLinksVisible(boolean visible) {
    createLinks.setVisible(visible);
  }

  @Override
  public void setGoogleButtonVisible(boolean visible) {
    googleButton.setVisible(visible);
    if (visible) {
      // If initially rendered invisible, Google+ sets the width and height of sub-elements at 1px.
      // Remove that nonsense.
      removeSizeOfChildren(googleButton.getElement());
    }
  }

  @Override
  public void clearGames() {
    gameList.clear();
  }

  @Override
  public void addGame(GameInfo gameInfo, boolean canJoin) {
    String gameString = DateTimeFormat.getFormat(
        DateTimeFormat.PredefinedFormat.DATE_TIME_FULL).format(gameInfo.getCreationDate()) + " : ";
    for (int i = 0; i < gameInfo.getNbPlayers(); ++i) {
      if (i != 0)
        gameString += ", ";
      UserInfo user = gameInfo.getPlayerInfo(i);
      gameString += user == null ? "???" : 
        (user.getName() + (user.getEmail() == null ? "" : " (" + user.getEmail() + ")"));
    }
    FlowPanel div = new FlowPanel();
    div.add(new InlineLabel(gameString));
    if (canJoin) {
      div.add(new InlineHyperlink("Join", ""));
    }
    gameList.add(div);
  }

  /**
   * Remove the width and height style of all the children of a node, recursively.
   * @param element The node for which to remove width and height.
   */
  private void removeSizeOfChildren(Element element) {
    for (int i = 0; i < element.getChildCount(); ++i) {
      Node childNode = element.getChild(i);
      if (Element.is(childNode)) {
        Element child = Element.as(childNode);
        child.getStyle().clearWidth();
        child.getStyle().clearHeight();
        removeSizeOfChildren(child);
      }
    }
  }

  @Override
  public void displayError(String string) {
    Window.alert(string);
  }

  @Override
  public void renderGoogleSignIn() {
    renderGoogleSignInJSNI();
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

  public native void renderGoogleSignInJSNI() /*-{
    $wnd['gapi'] && $wnd.gapi['signin'] && $wnd.gapi.signin['go'] && $wnd.gapi.signin.go();
  }-*/;

  @UiHandler("new3p")
  void onNew3pPressed(ClickEvent event) {
    presenter.createNewGame(3);
  }
  @UiHandler("new4p")
  void onNew4pPressed(ClickEvent event) {
    presenter.createNewGame(4);
  }
  @UiHandler("new5p")
  void onNew5pPressed(ClickEvent event) {
    presenter.createNewGame(5);
  }
  @UiHandler("signAsDummy")
  void onSignAsDummyPressed(ClickEvent event) {
    presenter.signAsDummy();
  }
}
