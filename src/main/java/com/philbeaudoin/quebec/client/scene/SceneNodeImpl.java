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

package com.philbeaudoin.quebec.client.scene;

import com.google.gwt.canvas.dom.client.Context2d;
import com.philbeaudoin.quebec.shared.utils.Callback;
import com.philbeaudoin.quebec.shared.utils.CallbackRegistration;
import com.philbeaudoin.quebec.shared.utils.CallbackRegistry;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.MutableTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * Base class for any node of the scene tree.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public abstract class SceneNodeImpl implements SceneNode {

  private final CallbackRegistry animationCompletedCallbacks = new CallbackRegistry();
  private Transform transform;
  private SceneNodeList parent;

  public SceneNodeImpl() {
    this.transform = new MutableTransform();
  }

  public SceneNodeImpl(Transform transform) {
    this.transform = transform;
  }

  @Override
  public void setTransform(Transform transform) {
    this.transform = transform;
  }

  @Override
  public Transform getTransform() {
    return transform;
  }

  @Override
  public void setParent(SceneNodeList parent) {
    if (this.parent != null) {
      this.parent.removeFromList(this);
    }
    this.parent = parent;
    if (this.parent != null) {
      this.parent.addToList(this);
    }
  }

  @Override
  public SceneNodeList getParent() {
    return parent;
  }

  @Override
  public ConstantTransform getTotalTransform(double time) {
    ConstantTransform constantTransform = getTransform().eval(time);
    if (parent == null) {
      return constantTransform;
    }
    return parent.getTotalTransform(time).times(constantTransform);
  }

  @Override
  public CallbackRegistration addAnimationCompletedCallback(Callback callback) {
    return animationCompletedCallbacks.register(callback);
  }

  @Override
  public void draw(double time, Context2d context) {
    context.save();
    try {
      getTransform().applies(time, context);
      drawUntransformed(time, context);
      if (isAnimationCompleted(time)) {
        animationCompletedCallbacks.executeAll();
      }
    } finally {
      context.restore();
    }
  }

  @Override
  public boolean isAnimationCompleted(double time) {
    if (!getTransform().isAnimationCompleted(time)) {
      return false;
    }
    return areChildrenAnimationsCompleted(time);
  }

  /**
   * Draws the scene node to the canvas without the transformation.
   * @param time The time at which to draw the scene node.
   * @param context The canvas context into which to draw.
   */
  protected abstract void drawUntransformed(double time, Context2d context);

  /**
   * Checks whether or not the children's animations are completed at the given time. The default
   * implementation assumes the node has no children. Override this method if for composite nodes.
   * @param time The time at which to check if the animation is completed.
   */
  protected boolean areChildrenAnimationsCompleted(double time) {
    return true;
  }
}