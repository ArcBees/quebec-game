package com.philbeaudoin.quebec.shared;

import java.util.ArrayList;

/**
 * A change of game state obtained by combining multiple different game state changes.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameStateChangeComposite implements GameStateChange {
  final private ArrayList<GameStateChange> changes = new ArrayList<GameStateChange>();

  @Override
  public GameState apply(GameState gameState) {
    GameState result = gameState;
    for (GameStateChange change : changes) {
      result = change.apply(result);
    }
    return result;
  }

  public void callOnEach(AcceptGameStateChange acceptGameStateChange) {
    for (GameStateChange change : changes) {
      acceptGameStateChange.execute(change);
    }
  }

  @Override
  public void accept(GameStateChangeVisitor visitor) {
    visitor.visit(this);
  }
}
