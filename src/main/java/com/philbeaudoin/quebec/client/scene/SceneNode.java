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
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * An object that can be drawn as part of the scene tree into an HTML5 2d canvas.
 * @author Philippe Beaudoin
 */
public interface SceneNode {

  /**
   * Adds this node as a child of the parent. If the parent is {@code null} the node is removed
   * from its parent and becomes a top level node.
   * @param parent The parent.
   */
  void setParent(SceneNodeList parent);

  /**
   * Access the node's parent in the scene tree. If {@code null}, the node is a top level node.
   * @return The parent.
   */
  SceneNodeList getParent();

  /**
   * Draws the scene node to the canvas.
   * @param time The time at which to draw the scene node.
   * @param context The canvas context into which to draw.
   */
  void draw(double time, Context2d context);

  /**
   * Sets the local transform of the scene node.
   * @param transform The desired transform.
   */
  void setTransform(Transform transform);

  /**
   * Returns the local transform affecting the scene node.
   * @return The transform.
   */
  Transform getTransform();

  /**
   * Returns the total transform affecting the scene node.
   * @param time The time at which to evaluate the total transform.
   * @return The total transform.
   */
  ConstantTransform getTotalTransform(double time);
}