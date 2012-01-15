package com.philbeaudoin.quebec.shared;

/**
 * A class can can track a single change of the game state.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface GameStateChange {
  /**
   * Apply this change to the specified game change and return the modified game state.
   * @param gameState The game state to apply this change to. It will not be modified.
   * @return The modified game state.
   */
  GameState apply(GameState gameState);

  /**
   * Accepts a visitor.
   * @param visitor The visitor.
   */
  void accept(GameStateChangeVisitor visitor);
}
