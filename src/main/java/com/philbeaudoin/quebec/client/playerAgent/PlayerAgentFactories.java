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

import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.player.PlayerLocalAi;
import com.philbeaudoin.quebec.shared.player.PlayerLocalUser;

/**
 * Factory methods of the various player agent classes, used in assisted injection.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface PlayerAgentFactories {
  PlayerAgentGenerator createPlayerAgentGenerator(GameController gameController);
  PlayerAgentLocalUser createPlayerAgentLocalUser(PlayerLocalUser host,
      GameController gameController);
  PlayerAgentLocalAi createPlayerAgentLocalAi(PlayerLocalAi host, GameController gameController);
  LocalUserInteractionGenerator createLocalUserInteractionGenerator(GameState gameState,
      GameStateRenderer gameStateRenderer, GameController gameController);
  LocalAiInteractionGenerator createLocalAiInteractionGenerator(GameState gameState,
      GameStateRenderer gameStateRenderer, GameController gameController);
}
