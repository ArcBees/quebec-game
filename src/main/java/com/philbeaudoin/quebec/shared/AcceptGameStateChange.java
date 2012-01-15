package com.philbeaudoin.quebec.shared;

/**
 * The interface used to pass methods that accept a single {@link GameStateChange}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface AcceptGameStateChange {
  void execute(GameStateChange gameStateChange);
}
