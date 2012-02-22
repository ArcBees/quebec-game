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
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.UserPreferences;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeIncreaseStarToken;
import com.philbeaudoin.quebec.shared.utils.ArcTransform;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * A change renderer that can apply a
 * {@link com.philbeaudoin.quebec.shared.statechange.GameStateChangeIncreaseStarToken
 * GameStateChangeIncreaseStarToken} to a scene graph.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ChangeRendererIncreaseStarToken implements ChangeRenderer {

  private final SpriteResources spriteResources;
  private final UserPreferences userPreferences;
  private final GameStateChangeIncreaseStarToken change;

  @Inject
  ChangeRendererIncreaseStarToken(
      SpriteResources spriteResources,
      UserPreferences userPreferences,
      @Assisted GameStateChangeIncreaseStarToken change) {
    this.spriteResources = spriteResources;
    this.userPreferences = userPreferences;
    this.change = change;
  }

  @Override
  public void applyAnimChanges(GameStateRenderer renderer) {
  }

  @Override
  public void undoAdditions(GameStateRenderer renderer) {
  }

  @Override
  public void generateAnim(GameStateRenderer renderer, double startingTime) {
    renderer.removeStarTokenFrom(change.getTile());
    PlayerColor starTokenColor = change.getStarTokenColor();
    int nbStars = change.getNbStarsAfter();
    Transform finishTransform = renderer.addStarTokenTo(change.getTile(),
        starTokenColor, nbStars);
    Transform startTransform = new ConstantTransform(
        new Vector2d(finishTransform.getTranslation(0)),
        1.8 * finishTransform.getScaling(0),
        finishTransform.getRotation(0));
    double endingTime = startingTime + userPreferences.getAnimDuration();
    Sprite sprite = new Sprite(spriteResources.getStarToken(starTokenColor, nbStars),
        new ArcTransform(startTransform, finishTransform, startingTime, endingTime));
    renderer.addToAnimationGraph(sprite);
  }
}
