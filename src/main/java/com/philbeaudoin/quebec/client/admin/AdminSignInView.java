/**
 * Copyright 2013 Philippe Beaudoin
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

package com.philbeaudoin.quebec.client.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * Simple page that the administrator can use to sign-in.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class AdminSignInView extends ViewImpl implements AdminSignInPresenter.MyView {

  interface Binder extends UiBinder<Widget, AdminSignInView> { }
  protected static final Binder binder = GWT.create(Binder.class);

  private final Widget widget;

  @UiField PasswordTextBox passwordTextBox;
  @UiField Label errorLabel;

  private AdminSignInPresenter presenter;

  @Inject
  public AdminSignInView() {
    widget = binder.createAndBindUi(this);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @Override
  public void setPresenter(AdminSignInPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void resetAndFocus() {
    clear();
    passwordTextBox.setFocus(true);
  }

  @Override
  public void clear() {
    passwordTextBox.setText("");
    setError("");
  }

  @Override
  public void setError(String error) {
    this.errorLabel.setText(error);
  }

  @UiHandler("passwordTextBox")
  void onKeyPress(KeyPressEvent event) {
    if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
      presenter.authenticateWithAdminPassword(passwordTextBox.getText());
    }
  }
}
