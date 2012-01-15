package com.philbeaudoin.quebec.shared;


/**
 * A valid place to take or send cubes. The destination implies a color of cubes.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface CubeDestination {

  /**
   * @return The number of cubes available in that destination.
   * @param gameState The game state into which to check.
   */
  int getNbCubes(GameState gameState);

  /**
   * @return The color of the player associated with that destination.
   */
  PlayerColor getPlayerColor();

  /**
   * Remove cubes from that specific destination in the provided game state.
   * @param nbCubes The number of cubes to add.
   * @param gameState The game state from which to remove cubes.
   */
  void removeFrom(int nbCubes, GameState gameState);

  /**
   * Add cubes to this specific destination in the provided game state.
   * @param nbCubes The number of cubes to add.
   * @param gameState The game state onto which to add cubes.
   */
  void addTo(int nbCubes, GameState gameState);

  /**
   * Accepts a visitor.
   * @param visitor The visitor.
   */
  void accept(CubeDestinationVisitor visitor);
}
