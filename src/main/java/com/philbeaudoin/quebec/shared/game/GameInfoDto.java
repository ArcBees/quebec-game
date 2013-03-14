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

import com.google.gwt.user.client.rpc.IsSerializable;
import com.philbeaudoin.quebec.shared.user.UserInfo;
import com.philbeaudoin.quebec.shared.user.UserInfoDto;

public class GameInfoDto implements GameInfo, IsSerializable {

  ArrayList<UserInfoDto> players;

  public GameInfoDto(GameInfo gameInfo) {
    this.players = new ArrayList<UserInfoDto>(gameInfo.getNbPlayers());
    for (int i = 0; i < gameInfo.getNbPlayers(); ++i) {
      this.players.add(new UserInfoDto(gameInfo.getPlayerInfo(i)));
    }
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private GameInfoDto() {
  }

  @Override
  public int getNbPlayers() {
    return players.size();
  }

  @Override
  public UserInfo getPlayerInfo(int index) {
    return players.get(index);
  }
}