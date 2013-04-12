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

import java.util.ArrayList;

import com.philbeaudoin.quebec.shared.game.state.Tile;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.message.Message;

/**
 * An explicit action represented by a message and a sequence of game state changes. The user
 * generally triggers that action by clicking a button displaying the message. This specialized
 * version should highlight a number of tiles in the standard renderer.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class ActionExplicitHighlightTiles extends ActionExplicit {

  private ArrayList<Tile> tiles;

  public ActionExplicitHighlightTiles(Message message, ArrayList<Tile> tiles,
      GameStateChange action) {
    super(message, action);
    this.tiles = tiles;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private ActionExplicitHighlightTiles() {
  }

  @Override
  public void accept(GameActionVisitor visitor) {
    visitor.visit(this);
  }

  public ArrayList<Tile> getTiles() {
    return tiles;
  }
}