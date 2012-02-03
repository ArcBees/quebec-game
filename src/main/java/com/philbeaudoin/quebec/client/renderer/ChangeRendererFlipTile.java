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
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeFlipTile;
import com.philbeaudoin.quebec.shared.utils.ArcTransform;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * A change renderer that can apply a
 * {@link com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveLeader GameStateChangeFlipTile}
 * to a scene graph.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ChangeRendererFlipTile implements ChangeRenderer {

  private final SpriteResources spriteResources;
  private final UserPreferences userPreferences;
  private final GameStateChangeFlipTile change;

  @Inject
  ChangeRendererFlipTile(
      SpriteResources spriteResources,
      UserPreferences userPreferences,
      @Assisted GameStateChangeFlipTile change) {
    this.spriteResources = spriteResources;
    this.userPreferences = userPreferences;
    this.change = change;
  }

  @Override
  public void applyRemovals(GameStateRenderer renderer) {
    renderer.removeStarTokenFrom(change.getTile());
  }

  @Override
  public void applyAdditions(GameStateRenderer renderer) {
    PlayerColor starTokenColor = change.getStarTokenColor();
    if (starTokenColor != PlayerColor.NONE) {
      renderer.addStarTokenTo(change.getTile(), starTokenColor, change.getNbStars());
    }
  }

  @Override
  public void undoRemovals(GameStateRenderer renderer) {
    // No way to undo the removal, we don't know what was there before!
  }

  @Override
  public void undoAdditions(GameStateRenderer renderer) {
    renderer.removeStarTokenFrom(change.getTile());
  }

  @Override
  public void generateAnim(GameStateRenderer renderer, double startingTime) {
    PlayerColor starTokenColor = change.getStarTokenColor();
    if (starTokenColor != PlayerColor.NONE) {
      int nbStars = change.getNbStars();
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
}
