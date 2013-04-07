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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.philbeaudoin.quebec.server.user.UserInfoEntity;
import com.philbeaudoin.quebec.shared.game.GameInfo;
import com.philbeaudoin.quebec.shared.user.UserInfo;

@Entity
@Cache
public class GameInfoEntity implements GameInfo {
  @Id Long id;
  int nbPlayers;
  @Index Date creationDate;
  @Load List<Ref<UserInfoEntity>> players = new ArrayList<Ref<UserInfoEntity>>();
  int currentPlayerIndex;

  public GameInfoEntity(int nbPlayers, long creationTime) {
    this.nbPlayers = nbPlayers;
    this.creationDate = new Date(creationTime);
    currentPlayerIndex = -1;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")  private GameInfoEntity() {
    this.nbPlayers = 0;
  }

  @Override
  public long getId() {
    return id == null ? -1 : id;
  }

  @Override
  public int getNbPlayers() {
    return nbPlayers;
  }

  @Override
  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public UserInfo getPlayerInfo(int index) {
    return index < players.size() ? players.get(index).get() : null;
  }

  @Override
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  public int getNbEmptySeats() {
    return nbPlayers - players.size();
  }
  
  public void addPlayer(UserInfoEntity userInfoEntity) {
    if (players.size() >= nbPlayers) {
      throw new RuntimeException("");
    }
    players.add(Ref.create(userInfoEntity));
    if (currentPlayerIndex < 0)
      currentPlayerIndex = 0;
  }
}
