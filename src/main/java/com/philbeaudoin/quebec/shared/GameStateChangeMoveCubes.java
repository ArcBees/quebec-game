package com.philbeaudoin.quebec.shared;

/**
 * A change of the game state obtained by moving cubes from one location to another.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameStateChangeMoveCubes implements GameStateChange {

  private final int nbCubes;
  private final CubeDestination from;
  private final CubeDestination to;

  public GameStateChangeMoveCubes(int nbCubes, CubeDestination from, CubeDestination to) {
    assert from.getPlayerColor() == to.getPlayerColor();
    this.nbCubes = nbCubes;
    this.from = from;
    this.to = to;
  }

  @Override
  public GameState apply(GameState gameState) {
    assert from.getNbCubes(gameState) >= nbCubes;
    GameState result = new GameState(gameState);
    from.removeFrom(nbCubes, result);
    to.addTo(nbCubes, result);
    return result;
  }

  @Override
  public void accept(GameStateChangeVisitor visitor) {
    visitor.visit(this);
  }

  /**
   * Access the number of cubes being moved.
   * @return The number of cubes being moved.
   */
  public int getNbCubes() {
    return nbCubes;
  }

  /**
   * Access the cube destination from which the cubes start.
   * @return The origin cube destination.
   */
  public CubeDestination getFrom() {
    return from;
  }

  /**
   * Access the cube destination to which the cubes go.
   * @return The destination cube destination.
   */
  public CubeDestination getTo() {
    return to;
  }
}
