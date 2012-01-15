package com.philbeaudoin.quebec.shared;

/**
 * Interface for a class that can visit a {@link CubeDestination}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface CubeDestinationVisitor {
  /**
   * Visits a {@link CubeDestinationInfluenceZone}.
   * @param host The visited class.
   */
  public void visit(CubeDestinationInfluenceZone host);
}
