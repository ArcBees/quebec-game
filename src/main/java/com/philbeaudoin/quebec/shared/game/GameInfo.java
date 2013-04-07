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

package com.philbeaudoin.quebec.shared.game;

import java.util.Date;

import com.philbeaudoin.quebec.shared.user.UserInfo;

/**
 * All the data corresponding to a game
 * 
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface GameInfo {
  /**
   * @return the unique game ID
   */
  long getId();

  /**
   * @return The number of players in the game.
   */
  int getNbPlayers();

  /**
   * @param index The index of the player to retrieves, must be less than {@link #getNbPlayers()}.
   * @return Information, filtered to contain only the public data, on the given player.
   */
  UserInfo getPlayerInfo(int index);

  /**
   * @return The date at which this game was created.
   */
  Date getCreationDate();

  /**
   * Access the index of the currently active player. Returns -1 if there are no currently active
   * player, for example if no players have registered for the game or if the game has ended. If the
   * game is under way this returns the index of the player who should play his move now. The game
   * is only under way if getPlayerInfo() returns non-null for every player and this returns
   * something else than -1.
   * @return The index of the currently active player or -1 if there are no active player.
   */
  int getCurrentPlayerIndex();
}
