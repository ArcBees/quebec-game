/**
 * Copyright 2012 Philippe Beaudoin
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

package com.philbeaudoin.quebec.shared.game.state;

import java.util.ArrayList;

import com.philbeaudoin.quebec.shared.InfluenceType;

/**
 * A randomized deck from which tiles can be drawn.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class TileDeck {
  // One deck for each influence type.
  private final ArrayList<ArrayList<Tile>> decks = new ArrayList<ArrayList<Tile>>(4);

  /**
   * Create a tile deck and initialize it with the standard tiles of the game.
   * @param shuffler The shuffler to use, or null if no shuffling is desired.
   */
  public TileDeck(Shuffler shuffler) {
    for (int influenceTypeIndex = 0; influenceTypeIndex < 4; ++influenceTypeIndex) {
      InfluenceType influenceType = InfluenceType.values()[influenceTypeIndex];
      ArrayList<Tile> deck = new ArrayList<Tile>();
      decks.add(deck);
      for (int century = 0; century < 4; ++century) {
        int nbTiles = InfluenceType.getNbTilesForCentury(influenceType, century);
        for (int i = 0; i < nbTiles; ++i) {
          deck.add(new Tile(influenceType, century, i));
        }
      }
      if (shuffler != null) {
        shuffler.shuffle(deck, influenceTypeIndex);
      }
    }
  }

  /**
   * Draws a tile of a given influence type from the tile deck. The drawn tile is removed from the
   * deck and will no longer be returned.
   * @param influenceType The desired influence type to draw.
   * @return A tile, or {@code null} if no more tile of that influence type are available.
   */
  public Tile draw(InfluenceType influenceType) {
    ArrayList<Tile> deck = decks.get(influenceType.ordinal());
    if (deck.isEmpty()) {
      return null;
    }
    return deck.remove(deck.size() - 1);
  }
}
