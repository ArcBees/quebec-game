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

package com.philbeaudoin.quebec.server.game;

import java.util.List;

import com.gwtplatform.dispatch.shared.ActionException;
import com.philbeaudoin.quebec.shared.game.GameInfo;

/**
 * Manages information relative to a game on the server.
 * 
 * @author beaudoin
 */
public interface GameManager {

  /**
   * Creates a new game and stored it in the database. The current session user is automatically
   * added as the first player.
   * @param nbPlayers The total number of players for the game.
   * @return The newly created game.
   * @throws ActionException If the game cannot be created.
   */
  GameInfoEntity createNewGame(int nbPlayers) throws ActionException;

  /**
   * List all the games for which some slots are open, with a set maximum.
   * @return List of open games.
   */
  List<GameInfoEntity> listOpenGames();

  /**
   * Checks that the game passed as second parameter is found in the list of games passed in the
   * first parameter. If it is not found it is inserted at the top of the list.
   * @param games The list of games.
   * @param game The game that must be found, or be inserted, in the list of games.
   */
  void ensureListContainsGame(List<GameInfoEntity> games, GameInfoEntity game);

  /**
   * Given some game information to be transmitted back, anonymize it to remove sensitive fields
   * based on the currently signed-in user.
   * @param gameInfo The game information to anonymize.
   * @return The anonymized game info.
   */
  GameInfo anonymizeGameInfo(GameInfo gameInfo);
}
