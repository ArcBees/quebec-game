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

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Serialize;
import com.philbeaudoin.quebec.shared.game.state.GameState;

@Entity
@Cache
public class GameEntity {
  @Parent Key<GameInfoEntity> owner;
  @Id Long id;
  @Serialize(zip=true) GameState gameState;

  public GameEntity(Key<GameInfoEntity> owner, GameState gameState) {
    this.owner = owner;
    this.gameState = gameState;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")  private GameEntity() {
  }

  public GameState getGameState() {
    return gameState;
  }
}
