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

package com.philbeaudoin.quebec.client.sprites;

import com.philbeaudoin.quebec.shared.utils.MutableTransformation;
import com.philbeaudoin.quebec.shared.utils.Transformation;

/**
 * Base class for any node of the scene tree.
 *
 * @author beaudoin
 */
public abstract class SceneNodeImpl implements SceneNode {

  private final MutableTransformation transformation;
  private SceneNodeList parent;

  public SceneNodeImpl() {
    this.transformation = new MutableTransformation();
  }

  public SceneNodeImpl(Transformation transformation) {
    this.transformation = new MutableTransformation(transformation);
  }

  @Override
  public void setTransformation(Transformation transformation) {
    this.transformation.set(transformation);
  }

  @Override
  public Transformation getTransformation() {
    return transformation;
  }

  @Override
  public void setParent(SceneNodeList parent) {
    if (this.parent != null) {
      this.parent.removeFromList(this);
    }
    this.parent = parent;
    this.parent.addToList(this);
  }

  @Override
  public SceneNodeList getParent() {
    return parent;
  }

  @Override
  public Transformation getTotalTransformation() {
    if (parent == null) {
      return getTransformation();
    }
    return parent.getTotalTransformation().times(getTransformation());
  }
}