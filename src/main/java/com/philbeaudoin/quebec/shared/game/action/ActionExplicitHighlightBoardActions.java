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

import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.message.Message;

/**
 * An explicit action represented by a message and a sequence of game state changes. The user
 * generally triggers that action by clicking a button displaying the message. When rendered, this
 * specific action should highlight all the board actions.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ActionExplicitHighlightBoardActions extends ActionExplicit {

  public ActionExplicitHighlightBoardActions(Message message, GameStateChange action) {
    super(message, action);
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private ActionExplicitHighlightBoardActions() {
  }

  @Override
  public void accept(GameActionVisitor visitor) {
    visitor.visit(this);
  }
}