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
import com.philbeaudoin.quebec.client.scene.Sprite;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.shared.UserPreferences;
import com.philbeaudoin.quebec.shared.utils.ArcTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * A change renderer that can apply a
 * {@link com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeMoveCubes
 * GameStateChangeMoveCubes} to a scene graph.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ChangeRendererMoveArchitect implements ChangeRenderer {

  private final SpriteResources spriteResources;
  private final UserPreferences userPreferences;
  private final SceneArchitectDestination from;
  private final SceneArchitectDestination to;

  @Inject
  ChangeRendererMoveArchitect(
      SpriteResources spriteResources,
      UserPreferences userPreferences,
      @Assisted("from") SceneArchitectDestination from,
      @Assisted("to") SceneArchitectDestination to) {
    this.spriteResources = spriteResources;
    this.userPreferences = userPreferences;
    this.from = from;
    this.to = to;
    assert from.getArchitectColor() == to.getArchitectColor();
  }

  @Override
  public void applyAnimChanges(GameStateRenderer renderer) {
    from.removeFrom(renderer);
    to.addTo(renderer);
  }

  @Override
  public void undoAdditions(GameStateRenderer renderer) {
    to.removeFrom(renderer);
  }

  @Override
  public void generateAnim(GameStateRenderer renderer, double startingTime) {
    Transform startTransform = from.removeFrom(renderer);
    Transform finishTransform = to.addTo(renderer);
    double endingTime = startingTime + userPreferences.getAnimDuration();
    Sprite sprite = new Sprite(spriteResources.getPawn(from.getArchitectColor()),
        new ArcTransform(startTransform, finishTransform, startingTime, endingTime));
    renderer.addToAnimationGraph(sprite);
  }

}
