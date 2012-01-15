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

import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.MutableTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * Base class for any node of the scene tree.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public abstract class SceneNodeImpl implements SceneNode {

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
}