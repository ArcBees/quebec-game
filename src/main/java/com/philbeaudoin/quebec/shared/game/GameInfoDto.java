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

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.philbeaudoin.quebec.shared.user.UserInfo;
import com.philbeaudoin.quebec.shared.user.UserInfoDto;

public class GameInfoDto implements GameInfo, IsSerializable {

  private long id;
  ArrayList<UserInfoDto> players;
  Date creationDate;
  int currentPlayerIndex;

  public GameInfoDto(GameInfo gameInfo) {
    assert(gameInfo != null);
    this.id = gameInfo.getId();
    this.players = new ArrayList<UserInfoDto>(gameInfo.getNbPlayers());
    for (int i = 0; i < gameInfo.getNbPlayers(); ++i) {
      UserInfo userInfo = gameInfo.getPlayerInfo(i);
      this.players.add(userInfo == null ? null : new UserInfoDto(userInfo));
    }
    this.creationDate = new Date(gameInfo.getCreationDate().getTime());
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private GameInfoDto() {
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public int getNbPlayers() {
    return players.size();
  }

  @Override
  public UserInfo getPlayerInfo(int index) {
    return players.get(index);
  }

  @Override
  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  public UserInfoDto getPlayerInfoDto(int index) {
    return players.get(index);
  }

  /**
   * @return True if there are still some empty seat in this game.
   */
  public boolean isOpen() {
    for (int i = 0; i < getNbPlayers(); ++i) {
      if (getPlayerInfo(i) == null) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true if the player with the specified id can join this game. The player can join if
   * there is a spot still open in the game and he has not joined yet.
   * @param playerId The id of the player for whom we want to check.
   * @return True if the player with the specified id can join.
   */
  public boolean canJoin(long playerId) {
    boolean isOpenForPlayer = false;
    for (int i = 0; i < getNbPlayers(); ++i) {
      if (getPlayerInfo(i) == null) {
        isOpenForPlayer = true;
      } else if (getPlayerInfo(i).getId() == playerId) {
        return false;
      }
    }
    return isOpenForPlayer;
  }

  /**
   * Returns true if it's currently the move of the player with the specified id.
   * @param playerId The id of the player for whom we want to check.
   * @return True if it's currently the move of the player with the specified id.
   */
  public boolean isMoveOfPlayer(long playerId) {
    if (isOpen() || getCurrentPlayerIndex() == -1)
      return false;
    UserInfoDto userInfo = getPlayerInfoDto(getCurrentPlayerIndex());
    assert (userInfo != null);
    return userInfo.getId() == playerId;
  }

  /**
   * Returns true if the specified player can view the game. Any player can view any closed game
   * at the moment.
   * @param playerId The id of the player for whom we want to check.
   * @return True if the player with the specified id can view the game.
   */
  public boolean canView(long playerId) {
    return !isOpen();
  }
}