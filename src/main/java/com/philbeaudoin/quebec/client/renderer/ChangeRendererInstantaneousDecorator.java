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

/**
 * A change renderer that can apply a
 * {@link com.philbeaudoin.quebec.shared.statechange.ChangeRendererInstantaneousDecorator
 * ChangeRendererInstantaneousDecorator} to a scene graph.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ChangeRendererInstantaneousDecorator implements ChangeRenderer {

  private final ChangeRenderer decorated;

  public ChangeRendererInstantaneousDecorator(ChangeRenderer decorated) {
    this.decorated = decorated;
  }

  @Override
  public void applyAnimChanges(GameStateRenderer renderer) {
    decorated.applyAnimChanges(renderer);
  }

  @Override
  public void undoAdditions(GameStateRenderer renderer) {
    decorated.undoAdditions(renderer);
  }

  @Override
  public void generateAnim(GameStateRenderer renderer, double startingTime) {
    // Instantaneous means we only apply the additions.
    decorated.applyAnimChanges(renderer);
  }

}
