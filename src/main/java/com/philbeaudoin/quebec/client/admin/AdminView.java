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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * Administration page that an admin can use to edit properties.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class AdminView extends ViewImpl implements AdminPresenter.MyView {

  interface Binder extends UiBinder<Widget, AdminView> { }
  protected static final Binder binder = GWT.create(Binder.class);

  private final Widget widget;

  @UiField Button signOutButton;
  @UiField PasswordTextBox password1;
  @UiField PasswordTextBox password2;
  @UiField TextBox salt;
  @UiField CheckBox changeSaltCheckBox;
  @UiField TextBox googleOAuthClientSecret;
  @UiField Label messageLabel;
  @UiField Button changeSettingsButton;

  private AdminPresenter presenter;

  @Inject
  public AdminView() {
    widget = binder.createAndBindUi(this);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @Override
  public void setPresenter(AdminPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void resetAndFocus() {
    clear();
    signOutButton.setFocus(true);
  }

  @Override
  public void clear() {
    password1.setText("");
    password2.setText("");
    salt.setText("");
    changeSaltCheckBox.setValue(false);
    googleOAuthClientSecret.setText("");
    setMessage("");
  }

  @Override
  public void setMessage(String message) {
    messageLabel.setText(message);
  }

  @Override
  public String getPassword1() {
    return password1.getText();
  }

  @Override
  public String getPassword2() {
    return password2.getText();
  }

  @Override
  public String getSalt() {
    return salt.getText();
  }

  @Override
  public String getGoogleOAuthClientSecret() {
    return googleOAuthClientSecret.getText();
  }

  @Override
  public boolean getChangeSaltCheckBox() {
    return changeSaltCheckBox.getValue();
  }

  @UiHandler("signOutButton")
  void onSignOutClicked(ClickEvent event) {
    presenter.signOutAdmin();
  }

  @UiHandler("password1")
  void onPassword1KeyPress(KeyPressEvent event) {
    if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
      password2.setFocus(true);
    }
  }

  @UiHandler("password2")
  void onPassword2KeyPressed(KeyPressEvent event) {
    if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
      changeSettingsButton.setFocus(true);
    }
  }

  @UiHandler("salt")
  void onSaltKeyPressed(KeyPressEvent event) {
    if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
      changeSaltCheckBox.setFocus(true);
    }
  }

  @UiHandler("googleOAuthClientSecret")
  void onGoogleOAuthClientSecretKeyPressed(KeyPressEvent event) {
    if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
      changeSettingsButton.setFocus(true);
    }
  }

  @UiHandler("changeSettingsButton")
  void onPasswordClicked(ClickEvent event) {
    presenter.changeAdminSettings();
  }
}
