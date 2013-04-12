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

package com.philbeaudoin.quebec.shared.game.action;

import java.io.Serializable;
import java.util.ArrayList;

import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.message.TextBoxInfo;

/**
 * A set of possible actions that can be taken in the current state of the game.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class PossibleActions implements Serializable {

  private TextBoxInfo textBoxInfo;
  private ArrayList<GameAction> gameActions = new ArrayList<GameAction>();
  private boolean canSelectBoardAction;

  /**
   * Creates a list of possible actions without an information message. Assumes the user can select
   * a board action, so the renderer should display a tooltip on the board action on hover.
   */
  public PossibleActions() {
    this((TextBoxInfo) null);
  }

  /**
   * Creates a list of possible actions with an information message.
   * @param textBoxInfo Information on the text box to display while the user selects the action.
   * @param canSelectBoardAction True if among the possible actions the user can select a board
   *     action. In this case, the renderer should display a tooltip on the board action on hover.
   */
  public PossibleActions(TextBoxInfo textBoxInfo, boolean canSelectBoardAction) {
    this.textBoxInfo = textBoxInfo;
    this.canSelectBoardAction = canSelectBoardAction;
  }

  /**
   * Creates a list of possible actions with an information message. Assumes the user can select
   * a board action, so the renderer should display a tooltip on the board action on hover.
   * @param textBoxInfo Information on the text box to display while the user selects the action.
   */
  public PossibleActions(TextBoxInfo textBoxInfo) {
    this(textBoxInfo, true);
  }

  /**
   * Creates a list of possible actions with an information message. Assumes the user can select
   * a board action, so the renderer should display a tooltip on the board action on hover.
   * @param message The message to display at the top while the user selects the action.
   */
  public PossibleActions(Message message) {
    this(new TextBoxInfo(message));
  }

  /**
   * The number of actions that can be taken.
   * @return The number of actions.
   */
  public int getNbActions() {
    return gameActions.size();
  }

  /**
   * Accepts a visitor.
   * @param visitor The visitor.
   */
  public void accept(GameActionVisitor visitor) {
    visitor.setPossibleActions(this);
    for (GameAction gameAction : gameActions) {
      gameAction.accept(visitor);
    }
  }

  /**
   * Adds an action to this list of possible actions.
   * @param gameAction The action to add.
   */
  public void add(GameAction gameAction) {
    gameActions.add(gameAction);
  }

  /**
   * Returns the message associated with this list of possible actions, or null if none.
   * @return The message, or null.
   */
  public TextBoxInfo getTextBoxInfo() {
    return textBoxInfo;
  }

  /**
   * Access a given action.
   * @param actionIndex The index of the action to access.
   * @return The action
   */
  public GameAction getAction(int actionIndex) {
    return gameActions.get(actionIndex);
  }

  public boolean getCanSelectBoardAction() {
    return canSelectBoardAction;
  }
}
