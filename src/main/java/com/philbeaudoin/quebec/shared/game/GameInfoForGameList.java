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
 * A {@link GameInfoDto} with some extra information used in the the game list, for example in
 * the main view.
 */
public class GameInfoForGameList implements GameInfo {

  /**
   * The state of the game in the game list that indicate which action the currently signed-in user
   * can perform on it. Computed by the presenter and used to exchange information with the view.
   */
  public enum State {
    NO_ACTION,  // The user can't perform any action on the game.
    CAN_JOIN,   // The user can join the game.
    JOINING,    // The game is currently being joined by the user.
    CAN_PLAY,   // The user can play a move in this game.
    CAN_VIEW    // The user can view the current state of the game.
  }

  private final GameInfoDto gameInfoDto;
  private final int index;
  private State state;

  /**
   * Create a {@link GameInfo} structure specifically to be used in a game list.
   * @param gameInfoDto The game info attached to this object.
   * @param index The index in the game list.
   * @param state Indicates the state of the game, which can be used to infer which action the
   *     currently signed-in user can perform on this game.
   */
  public GameInfoForGameList(GameInfoDto gameInfoDto, int index, State state) {
    assert(gameInfoDto != null);
    this.gameInfoDto = gameInfoDto;
    this.index = index;
    this.state = state;
  }

  @Override public long getId() { return gameInfoDto.getId(); }
  @Override public int getNbPlayers() { return gameInfoDto.getNbPlayers(); }
  @Override public UserInfo getPlayerInfo(int index) { return gameInfoDto.getPlayerInfo(index); }
  @Override public Date getCreationDate() { return gameInfoDto.getCreationDate(); }
  @Override public int getCurrentPlayerIndex() { return gameInfoDto.getCurrentPlayerIndex(); }

  /**
   * @return The attached game info.
   */
  public GameInfoDto getGameInfoDto() {
    return gameInfoDto;
  }

  /**
   * @return The index of this game in the game list.
   */
  public int getIndex() {
    return index;
  }

  /**
   * @return The state of the game, which can be used to infer which action the currently signed-in
   *     user can perform on this game.
   */
  public State getState() {
    return state;
  }
}