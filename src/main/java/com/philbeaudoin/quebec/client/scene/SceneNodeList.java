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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.dom.client.Context2d;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * This class tracks a list of scene nodes. This class should have only logic, no GWT-specific code,
 * so it's easily testable.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class SceneNodeList extends SceneNodeImpl {
  private final ArrayList<SceneNode> sceneNodes = new ArrayList<SceneNode>();

  public SceneNodeList() {
    super();
  }

  public SceneNodeList(Transform transform) {
    super(transform);
  }

  /**
   * Adds a scene node to the list.
   * @param sceneNode The scene node to add.
   */
  public void add(SceneNode sceneNode) {
    sceneNode.setParent(this);
  }

  /**
   * Removes all the nodes from this list. The removed node will end up with a {@code null} parent.s
   */
  public void clear() {
    while (!sceneNodes.isEmpty()) {
      sceneNodes.get(sceneNodes.size() - 1).setParent(null);
    }
  }

  /**
   * Ensures that the specified scene node is drawn first so it appears behind all other nodes.
   * Will only work if the provided {@code sceneNode} is in the list, otherwise it has no effect.
   * @param sceneNode The scene node to draw behind all others.
   */
  public void sendToBack(SceneNode sceneNode) {
    if (sceneNodes.remove(sceneNode)) {
      sceneNodes.add(0, sceneNode);
    }
  }

  /**
   * Ensures that the specified scene node is drawn last so it appears on top of all other nodes.
   * Will only work if the provided {@code sceneNode} is in the list, otherwise it has no effect.
   * @param sceneNode The scene node to draw in front of all others.
   */
  public void sendToFront(SceneNode sceneNode) {
    if (sceneNodes.remove(sceneNode)) {
      sceneNodes.add(sceneNode);
    }
  }

  /**
   * Access all the children of that scene node.
   * @return The list of children.
   */
  public List<SceneNode> getChildren() {
    return sceneNodes;
  }

  /**
   * Checks whether the scene nod has children.
   * @return {@code true} if it has at least one children.
   */
  public boolean hasChild() {
    return !sceneNodes.isEmpty();
  }

  @Override
  public void drawUntransformed(double time, Context2d context) {
    for (SceneNode sceneNode : sceneNodes) {
      sceneNode.draw(time, context);
    }
  }

  /**
   * Package-private method used by scene nodes to add themselves to the list.
   * @param sceneNodes The scene node to add.
   */
  void addToList(SceneNode sceneNode) {
    sceneNodes.add(sceneNode);
  }

  /**
   * Package-private method used by scene nodes to remove themselves from the list.
   * @param sceneNodes The scene node to remove.
   */
  void removeFromList(SceneNode sceneNode) {
    sceneNodes.remove(sceneNode);
  }

  @Override
  protected boolean areChildrenAnimationsCompleted(double time) {
    for (SceneNode sceneNode : sceneNodes) {
      if (!sceneNode.isAnimationCompleted(time)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public SceneNode deepClone() {
    SceneNodeList clone = new SceneNodeList(getTransform());
    for (SceneNode sceneNode : sceneNodes) {
      SceneNode childClone = sceneNode.deepClone();
      childClone.setParent(clone);
    }
    return clone;
  }
}
