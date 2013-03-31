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
import com.philbeaudoin.quebec.shared.user.UserInfoDto;

/**
 * A {@link GameInfoDto} with some extra information used in the the game list, for example in
 * the main view.
 * @author beaudoin
 *
 */
public class GameInfoForGameList implements GameInfo {

  public enum JoinState {
    CANNOT_JOIN,
    CAN_JOIN,
    JOINING
  }

  private final GameInfoDto gameInfoDto;
  private final int index;
  private JoinState joinState;

  /**
   * Create a {@link GameInfo} structure specifically to be used in a game list.
   * @param gameInfoDto The game info attached to this object.
   * @param index The index in the game list.
   * @param joinState Indicates whether the currently signed-in user can join, can't join, or is
   *     currently joining the game.
   */
  public GameInfoForGameList(GameInfoDto gameInfoDto, int index, JoinState joinState) {
    assert(gameInfoDto != null);
    this.gameInfoDto = gameInfoDto;
    this.index = index;
    this.joinState = joinState;
  }

  @Override public long getId() { return gameInfoDto.getId(); }
  @Override public int getNbPlayers() { return gameInfoDto.getNbPlayers(); }
  @Override public UserInfo getPlayerInfo(int index) { return gameInfoDto.getPlayerInfo(index); }
  @Override public Date getCreationDate() { return gameInfoDto.getCreationDate(); }
  public UserInfoDto getPlayerInfoDto(int index) { return gameInfoDto.getPlayerInfoDto(index); }
  public boolean isOpen() { return gameInfoDto.isOpen(); }
  public boolean canJoin(long playerId) { return gameInfoDto.canJoin(playerId); }

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
   * @return The joing state, indicating whether the currently signed-in user can join, can't join,
   *     or is currently joining the game.
   */
  public JoinState getJoinState() {
    return joinState;
  }
}