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

package com.philbeaudoin.quebec.client.playerAgent;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.player.PlayerLocalAi;
import com.philbeaudoin.quebec.shared.player.PlayerLocalUser;
import com.philbeaudoin.quebec.shared.player.PlayerVisitor;

/**
 * Use this class to generate a {@link PlayerAgent} corresponding to a given
 * {@link com.philbeaudoin.quebec.shared.player.Player Player}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class PlayerAgentGenerator implements PlayerVisitor<PlayerAgent> {

  private final PlayerAgentFactories factories;
  private final GameController gameController;

  @Inject
  PlayerAgentGenerator(PlayerAgentFactories factories, @Assisted GameController gameController) {
    this.factories = factories;
    this.gameController = gameController;
  }

  @Override
  public PlayerAgent visit(PlayerLocalUser host) {
    return factories.createPlayerAgentLocalUser(host, gameController);
  }

  @Override
  public PlayerAgent visit(PlayerLocalAi host) {
    return factories.createPlayerAgentLocalAi(host, gameController);
  }
}
