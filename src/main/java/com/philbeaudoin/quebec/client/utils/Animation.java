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

package com.philbeaudoin.quebec.client.utils;

import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.shared.utils.AnimTransform;

/**
 * A class that can be used to generate an animation starting at a given time.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class Animation {
  private final SceneNode sceneNode;
  private final AnimTransform transform;
  private final double duration;

  public Animation(SceneNode sceneNode, AnimTransform transform, double duration) {
    this.sceneNode = sceneNode;
    this.transform = transform;
    this.duration = duration;
  }

  /**
   * Generates the animation.
   * @param startingTime The time at which the animation starts.
   * @return The scene node with the transform corresponding to the animation.
   */
  public SceneNode generate(double startingTime) {
    transform.setTimes(startingTime, startingTime + duration);
    sceneNode.setTransform(transform);
    return sceneNode;
  }
}
