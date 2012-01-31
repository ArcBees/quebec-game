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

import java.util.List;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.scene.Sprite;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.UserPreferences;
import com.philbeaudoin.quebec.shared.utils.ArcTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * A change renderer that can apply a
 * {@link com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveCubes GameStateChangeMoveCubes}
 * to a scene graph.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ChangeRendererMoveCubes implements ChangeRenderer {

  private final SpriteResources spriteResources;
  private final UserPreferences userPreferences;
  private final int nbCubes;
  private final SceneCubeDestination from;
  private final SceneCubeDestination to;

  @Inject
  ChangeRendererMoveCubes(
      SpriteResources spriteResources,
      UserPreferences userPreferences,
      @Assisted int nbCubes,
      @Assisted("from") SceneCubeDestination from,
      @Assisted("to") SceneCubeDestination to) {
    this.spriteResources = spriteResources;
    this.userPreferences = userPreferences;
    this.nbCubes = nbCubes;
    this.from = from;
    this.to = to;
    assert from.getPlayerColor() == to.getPlayerColor();
  }

  @Override
  public void applyRemovals(GameStateRenderer renderer) {
    from.removeFrom(nbCubes, renderer);
  }

  @Override
  public void applyAdditions(GameStateRenderer renderer) {
    to.addTo(nbCubes, renderer);
  }

  @Override
  public void undoRemovals(GameStateRenderer renderer) {
    from.addTo(nbCubes, renderer);
  }

  @Override
  public void undoAdditions(GameStateRenderer renderer) {
    to.removeFrom(nbCubes, renderer);
  }

  @Override
  public void generateAnim(GameStateRenderer renderer, double startingTime) {
    List<Transform> startTransforms = from.removeFrom(nbCubes, renderer);
    List<Transform> finishTransforms = to.addTo(nbCubes, renderer);
    assert startTransforms.size() == finishTransforms.size();
    assert startTransforms.size() == nbCubes;
    double endingTime = startingTime + userPreferences.getAnimDuration();
    PlayerColor playerColor = from.getPlayerColor();
    for (int i = 0; i < nbCubes; ++i) {
      Sprite cube = new Sprite(spriteResources.getCube(playerColor),
          new ArcTransform(startTransforms.get(i), finishTransforms.get(i),
              startingTime, endingTime));
      renderer.addToAnimationGraph(cube);
    }
  }

}
