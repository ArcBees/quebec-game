/**
 * Copyright 2013 Philippe Beaudoin
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

package com.philbeaudoin.quebec.shared.game.action;

import java.util.ArrayList;
import java.util.List;

import com.philbeaudoin.quebec.shared.utils.Callback;

/**
 * This interface controls the lifecycle of applying a game action. Applying a game action is
 * performed in various stages and transitioning from one stage to the next requires
 * synchronization between various asynchronous actors.
 *
 * This class allows any number {@link GameActionLifecycleActor} to register towards a game state
 * change. Once the game state change lifecycle is started, every actor will be notified of the
 * lifecycle transitions. Moreover, a transition from one stage to the next will only occur after
 * all the registered actors have indicated they are ready.
 *
 * An animation actor may want to do the following:
 *   onStart(): Begin rendering animation. 
 *       --> invoke the callback when the animation has completed.
 *   onFinalize(): Re-render the background image of the board given the state post-animation.
 *       --> invoke the callback immediately.
 *   onComplete(): Do nothing.
 *
 * A network actor may want to do the following:
 *   onStart(): Send the move to the server.
 *       --> invoke the callback immediately.
 *   onFinalize(): Wait until the server response has been received.
 *       --> invoke the callback when the server response has been received.
 *   onComplete(): Do nothing.
 *
 * @author beaudoin
 */
public class GameActionLifecycle {

  public enum Stage {
    PRE_START,
    STARTED,
    FINALIZED,
    COMPLETED
  }

  private final List<GameActionLifecycleActor> actors = new ArrayList<GameActionLifecycleActor>();

  private Stage stage;
  // A bitfield where each bit is 1 to indicate an actor is still running the current stage.
  private int runningActors;

  /**
   * Initializes the lifecycle structure.
   * @param initialGameState The game state before the action is applied.
   * @param finalGameState The game state after the action has been applied.
   * @param gameStateChange The game state change resulting from applying the action to the state.
   * @param gameAction The action.
   */
  public GameActionLifecycle() {
    stage = Stage.PRE_START;
    runningActors = 0;
  }

  /**
   * Adds an actor that will be notified of every game action lifecycle change. This actor must
   * absolutely call the various Completed methods when they are ready to progress to the next
   * stage of the lifecycle. It is illegal to add actors once the lifecycle has started. There is
   * a limit of 30 actors on a lifecycle.
   * @param actor The actor to add.
   */
  public void addActor(GameActionLifecycleActor actor) {
    assert stage == Stage.PRE_START;
    if (actor == null) {
      return;
    }
    assert actors.size() < 30;
    actors.add(actor);
  }

  /**
   * Starts the lifecycle. This will call {@Link GameActionLifecycleActor#onStart} on each actor.
   * This method must be called only once.
   */
  public void start() {
    assert stage == Stage.PRE_START;
    assert runningActors == 0;
    stage = Stage.STARTED;
    setAllActorsRunning();
    int i = 0;
    for (GameActionLifecycleActor actor : actors) {
      final int actorIndex = i;
      actor.onStart(new Callback() {
        @Override
        public void execute() {
          startCompleted(actorIndex);
        }
      });
      i++;
    }
  }

  /**
   * Called by each actor after it has completed the start stage of the lifecycle.
   * Once all the actors have called this, the lifecycle will proceed to the finalize stage.
   * This method must be called only once by each actor.
   * @param actorIndex The index of the actor that has completed the start stage.
   */
  private void startCompleted(int actorIndex) {
    assert stage == Stage.STARTED;
    assert (runningActors & (1 << actorIndex)) != 0;
    runningActors &= ~(1 << actorIndex);

    if (runningActors == 0) {
      transitionToFinalized();
    }
  }

  /**
   * Automatically called once each actor has completed the start stage.  This will call
   * {@Link GameActionLifecycleActor#onFinalize} on each actor.
   */
  private void transitionToFinalized() {
    assert stage == Stage.STARTED;
    assert runningActors == 0;
    stage = Stage.FINALIZED;
    setAllActorsRunning();
    int i = 0;
    for (GameActionLifecycleActor actor : actors) {
      final int actorIndex = i;
      actor.onFinalize(new Callback() {
        @Override
        public void execute() {
          finalizeCompleted(actorIndex);
        }
      });
      i++;
    }
  }

  /**
   * Called by each actor after it has completed the finalize stage of the lifecycle.
   * Once all the actors have called this, the lifecycle will proceed to the completed stage.
   * This method must be called only once by each actor.
   * @param actorIndex The index of the actor that has completed the finalize stage.
   */
  private void finalizeCompleted(int actorIndex) {
    assert stage == Stage.FINALIZED;
    assert (runningActors & (1 << actorIndex)) != 0;
    runningActors &= ~(1 << actorIndex);

    if (runningActors == 0) {
      transitionToCompleted();
    }
  }

  /**
   * Automatically called once each actor has completed the finalize stage.  This will call
   * {@Link GameActionLifecycleActor#onComplete} on each actor.
   */
  private void transitionToCompleted() {
    assert stage == Stage.FINALIZED;
    assert runningActors == 0;
    stage = Stage.COMPLETED;
    for (GameActionLifecycleActor actor : actors) {
      actor.onComplete();
    }
  }

  /**
   * Ensures the bitfield contains 1 at the position for each actor.
   */
  private void setAllActorsRunning() {
    runningActors = (1 << actors.size()) - 1;
  }
}
