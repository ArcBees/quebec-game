package com.philbeaudoin.quebec.shared;


/**
 * A cube destination corresponding to an given player color within an influence zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class CubeDestinationInfluenceZone implements CubeDestination {

  private final InfluenceType influenceType;
  private final PlayerColor playerColor;

  public CubeDestinationInfluenceZone(InfluenceType influenceType,
      PlayerColor playerColor) {
    this.influenceType = influenceType;
    this.playerColor = playerColor;
  }
  
  @Override
  public int getNbCubes(GameState gameState) {
    return gameState.getPlayerCubesInInfluenceZone(influenceType, playerColor);
  }

  @Override
  public PlayerColor getPlayerColor() {
    return playerColor;
  }

  @Override
  public void removeFrom(int nbCubes, GameState gameState) {
    int nbCubesInZone = gameState.getPlayerCubesInInfluenceZone(influenceType, playerColor);
    assert nbCubesInZone >= nbCubes;
    gameState.setPlayerCubesInInfluenceZone(influenceType, playerColor, nbCubesInZone - nbCubes);
  }

  @Override
  public void addTo(int nbCubes, GameState gameState) {
    int nbCubesInZone = gameState.getPlayerCubesInInfluenceZone(influenceType, playerColor);
    gameState.setPlayerCubesInInfluenceZone(influenceType, playerColor, nbCubesInZone + nbCubes);
  }

  @Override
  public void accept(CubeDestinationVisitor visitor) {
    visitor.visit(this);
  }

  /**
   * Access the influence zone of this destination.
   * @return The influence type of that zone.
   */
  public InfluenceType getInfluenceType() {
    return influenceType;
  }
}
