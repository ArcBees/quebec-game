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

package com.philbeaudoin.quebec.client.renderer;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.shared.state.LeaderCard;
import com.philbeaudoin.quebec.shared.statechange.LeaderDestinationBoard;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * An leader card destination within the board.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class SceneLeaderDestinationBoard implements SceneLeaderDestination {

  private final LeaderDestinationBoard leaderDestinationBoard;

  @Inject
  public SceneLeaderDestinationBoard(@Assisted LeaderDestinationBoard leaderDestinationBoard) {
    this.leaderDestinationBoard = leaderDestinationBoard;
  }

  @Override
  public LeaderCard getLeaderCard() {
    return leaderDestinationBoard.getLeaderCard();
  }

  @Override
  public Transform removeFrom(GameStateRenderer renderer) {
    return renderer.removeLeaderCardFromBoard(getLeaderCard());
  }

  @Override
  public Transform addTo(GameStateRenderer renderer) {
    return renderer.addLeaderCardToBoard(getLeaderCard());
  }
}
