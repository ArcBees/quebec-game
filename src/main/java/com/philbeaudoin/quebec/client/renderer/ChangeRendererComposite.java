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

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * A change renderer that can apply a
 * {@link com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeComposite
 * GameStateChangeComposite} to a scene graph.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ChangeRendererComposite implements ChangeRenderer {

  private final ArrayList<ChangeRenderer> gameStateChangeRenderers =
      new ArrayList<ChangeRenderer>();

  @Inject
  ChangeRendererComposite() {
  }

  /**
   * Adds a {@link ChangeRenderer} to this composite.
   * @param changeRenderer The change renderer to add.
   */
  void add(ChangeRenderer changeRenderer) {
    gameStateChangeRenderers.add(changeRenderer);
  }

  @Override
  public void applyAnimChanges(GameStateRenderer renderer) {
    for (ChangeRenderer gameStateChangeRenderer : gameStateChangeRenderers) {
      gameStateChangeRenderer.applyAnimChanges(renderer);
    }
  }

  @Override
  public void undoAdditions(GameStateRenderer renderer) {
    for (ChangeRenderer gameStateChangeRenderer : gameStateChangeRenderers) {
      gameStateChangeRenderer.undoAdditions(renderer);
    }
  }

  @Override
  public void generateAnim(GameStateRenderer renderer, double startingTime) {
    for (ChangeRenderer gameStateChangeRenderer : gameStateChangeRenderers) {
      gameStateChangeRenderer.generateAnim(renderer, startingTime);
    }
  }
}
