/**
 * Copyright 2011 Philippe Beaudoin
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
import com.philbeaudoin.quebec.shared.CubeDestinationInfluenceZone;
import com.philbeaudoin.quebec.shared.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.CubeDestinationTile;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Factory methods of the various renderer classes, used in assisted injection.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface RendererFactories {
  GameStateRenderer createGameStateRenderer();
  BoardRenderer createBoardRenderer(double leftPosition);
  ScoreRenderer createScoreRenderer();
  PlayerStateRenderer createPlayerStateRenderer(Vector2d size, Transform transform,
      ScoreRenderer scoreRenderer);

  ChangeRendererGenerator createChangeRendererGenerator();
  ChangeRendererComposite createChangeRendererComposite();
  ChangeRendererMoveCubes createChangeRendererMoveCubes(int nbCubes,
      @Assisted("from") SceneCubeDestination from, @Assisted("to") SceneCubeDestination to);
  SceneCubeDestinationGenerator createSceneGraphCubeDestinationGenerator();
  SceneCubeDestinationInfluenceZone createSceneCubeDestinationInfluenceZone(
      CubeDestinationInfluenceZone cubeDestinationInfluenceZone);
  SceneCubeDestinationPlayer createSceneCubeDestinationPlayer(
      CubeDestinationPlayer cubeDestinationPlayer);
  SceneCubeDestinationTile createSceneCubeDestinationTile(CubeDestinationTile cubeDestinationTile);
}
