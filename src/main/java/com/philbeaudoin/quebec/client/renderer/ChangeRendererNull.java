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
 * A change renderer that doesn't do anything.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ChangeRendererNull implements ChangeRenderer {

  @Override
  public void applyRemovals(GameStateRenderer renderer) {
  }

  @Override
  public void applyAdditions(GameStateRenderer renderer) {
  }

  @Override
  public void undoRemovals(GameStateRenderer renderer) {
  }

  @Override
  public void undoAdditions(GameStateRenderer renderer) {
  }

  @Override
  public void generateAnim(GameStateRenderer renderer, double startingTime) {
  }
}
