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

package com.philbeaudoin.quebec.client.interaction;

import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.action.HasInfluenceZone;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeQueuePossibleActions;

/**
 * This is an interaction with the game board for the action of selecting an influence zone from
 * which to move a number of cubes.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionMoveCubesFromZone extends InteractionWithAction {
  @Inject
  public InteractionMoveCubesFromZone(Scheduler scheduler,
      InteractionFactories interactionFactories,
      @Assisted GameState gameState, @Assisted GameStateRenderer gameStateRenderer,
      @Assisted InfluenceType origin, @Assisted PossibleActions possibleDestinations) {
    super(scheduler, gameState, gameStateRenderer,
        interactionFactories.createInteractionTargetInfluenceZone(gameStateRenderer,
            toHasInfluenceType(origin)),
        new GameStateChangeQueuePossibleActions(possibleDestinations));
  }

  /**
   * Static builder method to convert from an {@link InfluenceType} to this wrapper type.
   * @return The {@link HasInfluenceZone} wrapping the influence type.
   */
  static HasInfluenceZone toHasInfluenceType(final InfluenceType influenceType) {
    return new HasInfluenceZone() {
      @Override
      public InfluenceType getInfluenceZone() {
        return influenceType;
      }
    };
  }
}
