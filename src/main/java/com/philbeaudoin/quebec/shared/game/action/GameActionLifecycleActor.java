package com.philbeaudoin.quebec.shared.game.action;

import com.philbeaudoin.quebec.shared.utils.Callback;

/**
 * An actor that wants to be notified of a {@link GameActionLifecycle}'s transitions and control
 * them. Actors must hook onto every lifecycle methods and, at a minimum, invoke the provided
 * {@code completedCallback}.
 *
 * @author beaudoin
 */
public interface GameActionLifecycleActor {
  /**
   * Called when the game action lifecycle enters the {@code STARTED} stage. The actor must
   * absolutely call {@code completedCallback} exactly once at some point after this call.
   * This method should never be called more than once on a given actor.
   * @param completedCallback A callback that must be called once the actor has completed the 
   *    {@code STARTED} stage.
   */
  public void onStart(Callback completedCallback);

  /**
   * Called when the game action lifecycle enters the {@code FINALIZED} stage. The actor must
   * absolutely call {@code completedCallback} exactly once at some point after this call.
   * This method should never be called more than once on a given actor.
   * @param completedCallback A callback that must be called once the actor has completed the
   *    {@code FINALIZED} stage.
   */
  public void onFinalize(Callback completedCallback);

  /**
   * Called when the game action lifecycle enters the {@code COMPLETED} stage.
   * This method should never be called more than once on a given actor.
   */
  public void onComplete();
}
