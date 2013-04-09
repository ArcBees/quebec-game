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

import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeNextPlayer;
import com.philbeaudoin.quebec.shared.message.Message;

/**
 * An explicit action represented by a message and a sequence of game state changes. The user
 * generally triggers that action by clicking a button displaying the message.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ActionExplicit implements GameAction {

  public static ActionExplicit createSkipAction() {
    return new ActionExplicit(new Message.Text("skip"), new GameStateChangeNextPlayer());
  }

  private Message message;
  private GameStateChange action;
  private boolean automatic;

  public ActionExplicit(Message message, GameStateChange action) {
    this(message, action, false);
  }

  public ActionExplicit(Message message, GameStateChange action, boolean automatic) {
    assert action != null;
    this.message = message;
    this.action = action;
    this.automatic = automatic;
  }

  /**
   * For serialization only.
   */
  protected ActionExplicit() {
  }

  @Override
  public GameStateChange execute(GameController gameController, GameState gameState) {
    GameStateChangeComposite result = new GameStateChangeComposite();
    result.add(action);
    return result;
  }

  @Override
  public boolean isAutomatic() {
    return automatic;
  }

  @Override
  public void accept(GameActionVisitor visitor) {
    visitor.visit(this);
  }

  /**
   * Access the message that should be displayed in the button representing that action.
   * @return The message.
   */
  public Message getMessage() {
    return message;
  }
}