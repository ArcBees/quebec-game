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

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * An architect destination within a player zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class SceneArchitectDestinationOffboardNeutral implements SceneArchitectDestination {

  private static final Transform OFFBOARD_TRANSFORM =
      new ConstantTransform(new Vector2d(-0.05, 0.2));

  @Inject
  public SceneArchitectDestinationOffboardNeutral() {
  }

  @Override
  public PlayerColor getArchitectColor() {
    return PlayerColor.NEUTRAL;
  }

  @Override
  public Transform removeFrom(GameStateRenderer renderer) {
    return OFFBOARD_TRANSFORM;
  }

  @Override
  public Transform addTo(GameStateRenderer renderer) {
    return OFFBOARD_TRANSFORM;
  }
}
