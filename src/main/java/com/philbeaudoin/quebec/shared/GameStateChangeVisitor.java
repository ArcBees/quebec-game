package com.philbeaudoin.quebec.shared;

/**
 * Interface for a class that can visit a {@link GameStateChange}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface GameStateChangeVisitor {
  /**
   * Visits a {@link GameStateChangeComposite}.
   * @param host The visited class.
   */
  public void visit(GameStateChangeComposite host);
  /**
   * Visits a {@link GameStateChangeMoveCubes}.
   * @param host The visited class.
   */
  public void visit(GameStateChangeMoveCubes host);
}
