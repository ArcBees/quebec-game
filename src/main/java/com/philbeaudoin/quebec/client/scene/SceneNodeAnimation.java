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

package com.philbeaudoin.quebec.client.scene;

import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.shared.utils.ArcTransform;
import com.philbeaudoin.quebec.shared.utils.Callback;
import com.philbeaudoin.quebec.shared.utils.CallbackRegistration;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * Takes a scene node and animates it from a starting from a given transformation and ending at
 * a given information. The duration is fixed and the animation can be started or stopped at any
 * time. Stopping the animation will transform back from the ending transformation to the starting
 * transformation. After being stopped, the node is removed from the animation graph.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class SceneNodeAnimation {
  private static final double DURATION = 0.2;

  private final Scheduler scheduler;
  private final GameStateRenderer gameStateRenderer;
  private final Transform fromTransform;
  private final Transform toTransform;
  private final SceneNode animatedNode;

  private CallbackRegistration animRegistration;

  // True if the animated node was automatically added to the scene graph, and therefore should be
  // removed when the animation terminates.
  private boolean automaticallyAddedToGraph;

  /**
   * The factory for {@link SceneNodeAnimation}.
   */
  public interface Factory {
    SceneNodeAnimation create(GameStateRenderer gameStateRenderer,
        @Assisted("from") Transform fromTransform, @Assisted("to") Transform toTransform,
        SceneNode animatedNode);
  }

  @Inject
  public SceneNodeAnimation(Scheduler scheduler, @Assisted GameStateRenderer gameStateRenderer,
      @Assisted("from") Transform fromTransform, @Assisted("to") Transform toTransform,
      @Assisted SceneNode animatedNode) {
    this.scheduler = scheduler;
    this.gameStateRenderer = gameStateRenderer;
    this.fromTransform = fromTransform;
    this.toTransform = toTransform;
    this.animatedNode = animatedNode;
  }

  /**
   * Starts the animation at a given time.
   * @param time The time at which to start the animation.
   */
  public void startAnim(double time) {
    // Bump up.
    ensureAnimUnregistered();
    animatedNode.setTransform(new ArcTransform(fromTransform, toTransform, time, time + DURATION));
    addNodeIfNeeded();
  }

  /**
   * Stops the animation at a given time.
   * @param time The time at which to stop the animation.
   */
  public void stopAnim(double time) {
    // Bump down.
    ensureAnimUnregistered();
    animatedNode.setTransform(new ArcTransform(toTransform, fromTransform, time, time + DURATION));
    addNodeIfNeeded();

    // If we added the node automatically, remove it once the animation is over.
    if (automaticallyAddedToGraph) {
      animRegistration = animatedNode.addAnimationCompletedCallback(new Callback() {
        @Override
        public void execute() {
          scheduler.scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
              ensureAnimUnregistered();
            }
          });
        }
      });
    }
  }

  private void ensureAnimUnregistered() {
    removeNodeIfNeeded();
    if (animRegistration != null) {
      animRegistration.unregister();
      animRegistration = null;
    }
  }

  private void addNodeIfNeeded() {
    automaticallyAddedToGraph = animatedNode.getParent() == null;
    if (automaticallyAddedToGraph) {
      gameStateRenderer.addToAnimationGraph(animatedNode);
    }
  }

  private void removeNodeIfNeeded() {
    if (automaticallyAddedToGraph) {
      animatedNode.setParent(null);
      automaticallyAddedToGraph = false;
    }
  }
}
