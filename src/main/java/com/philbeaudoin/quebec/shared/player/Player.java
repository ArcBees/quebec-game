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

package com.philbeaudoin.quebec.shared.player;

import java.io.Serializable;

import com.philbeaudoin.quebec.shared.PlayerColor;

/**
 * Information on a player taking part in a game.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface Player extends Serializable {

  /**
   * Access the name of this player.
   * @return The player name.
   */
  String getName();

  /**
   * Access the color of this player.
   * @return The player color.
   */
  PlayerColor getColor();

  /**
   * Accepts a visitor.
   * @param visitor The visitor to accept.
   * @return A visitor-dependent object.
   */
  <T> T accept(PlayerVisitor<T> visitor);
}