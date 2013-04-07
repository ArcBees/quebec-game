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

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.shared.state.GameController;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeFlipTile;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeIncreaseStarToken;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Factory methods of the various renderer classes, used in assisted injection.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface RendererFactories {
  GameStateRenderer createGameStateRenderer(GameController gameController);
  BoardRenderer createBoardRenderer(double leftPosition);
  ScoreRenderer createScoreRenderer();
  PlayerStateRenderer createPlayerStateRenderer(Vector2d size, Transform transform,
      ScoreRenderer scoreRenderer);

  ChangeRendererMoveCubes createChangeRendererMoveCubes(int nbCubes,
      @Assisted("from") SceneCubeDestination from, @Assisted("to") SceneCubeDestination to);
  ChangeRendererMoveArchitect createChangeRendererMoveArchitect(
      @Assisted("from") SceneArchitectDestination from,
      @Assisted("to") SceneArchitectDestination to);
  ChangeRendererMoveLeader createChangeRendererMoveLeader(
      @Assisted("from") SceneLeaderDestination from, @Assisted("to") SceneLeaderDestination to);
  ChangeRendererFlipTile createChangeRendererFlipTile(GameStateChangeFlipTile host);
  ChangeRendererIncreaseStarToken createChangeRendererIncreaseStarToken(
      GameStateChangeIncreaseStarToken host);
}
