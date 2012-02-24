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

package com.philbeaudoin.quebec.client.renderer;

import java.util.ArrayList;

import javax.inject.Inject;

import com.philbeaudoin.quebec.client.resources.text.GraphicConstants;
import com.philbeaudoin.quebec.client.resources.text.GraphicMessages;
import com.philbeaudoin.quebec.client.scene.ComplexText;
import com.philbeaudoin.quebec.client.scene.ComplexText.Component;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.ScoringInformation;
import com.philbeaudoin.quebec.shared.ZoneScoringInformation;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.state.ActionType;

/**
 * A message visitor used to render messages into complex text components.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class MessageRenderer implements Message.Visitor<Void> {

  private final GraphicConstants constants;
  private final GraphicMessages messages;
  private final SpriteResources spriteResources;
  private final ArrayList<ComplexText.Component> components =
      new ArrayList<ComplexText.Component>();

  @Inject
  public MessageRenderer(GraphicConstants constants, GraphicMessages messages,
      SpriteResources spriteResources) {
    this.constants = constants;
    this.messages = messages;
    this.spriteResources = spriteResources;
  }

  /**
   * Obtain the components rendered by this renderer.
   * @return The components.
   */
  public ArrayList<Component> getComponents() {
    return components;
  }

  /**
   * Calculates the approximate width of the series of components generated.
   * @return The approximate width.
   */
  public double calculateApproximateWidth() {
    double total = 0;
    for (ComplexText.Component component : components) {
      total += component.getApproximateWidth();
    }
    return total;
  }

  @Override
  public Void visit(Message.MoveYourArchitect host) {
    addText(constants.moveYourArchitect());
    return null;
  }

  @Override
  public Void visit(Message.MoveYourArchitectToThisTile host) {
    addText(constants.moveYourArchitectToThisTile());
    return null;
  }

  @Override
  public Void visit(Message.Skip host) {
    addText(constants.skip());
    return null;
  }

  @Override
  public Void visit(Message.Continue host) {
    addText(constants.continueMsg());
    return null;
  }

  @Override
  public Void visit(Message.SelectStarTokenToIncrease host) {
    addText(constants.selectStarTokenToIncrease());
    return null;
  }

  @Override
  public Void visit(Message.SelectSpotToFill host) {
    addText(constants.selectSpotToFill());
    return null;
  }

  @Override
  public Void visit(Message.ScoringPhaseBegins host) {
    addText(constants.scoringPhaseBegins());
    return null;
  }

  @Override
  public Void visit(Message.PrepareNextCentury host) {
    addText(constants.prepareNextCentury());
    return null;
  }

  @Override
  public Void visit(Message.TakeThisLeaderCard host) {
    if (host.getCount() == 0) {
      addText(constants.takeThisLeaderCard());
    } else {
      fillInPlaceholders(repeatPlaceholder(constants.takeThisLeaderCardAndActivateCubes(), 0,
          host.getCount()), newCube(host.getColor(0)));
    }
    return null;
  }

  @Override
  public Void visit(Message.MoveEitherArchitect host) {
    fillInPlaceholders(constants.moveEitherArchitect(),
        newPawn(host.getColor(0)), newPawn(host.getColor(1)));
    return null;
  }

  @Override
  public Void visit(Message.MoveArchitect host) {
    fillInPlaceholders(constants.moveArchitect(), newPawn(host.getColor(0)));
    return null;
  }

  @Override
  public Void visit(Message.SendPassiveCubesToOneOfTwoZones host) {
    fillInPlaceholders(repeatPlaceholder(constants.sendPassiveCubesToOneOfTwoZones(), 0,
        host.getCount()),
        newCube(host.getColor(0)),
        newZoneIcon(host.getZone(0)),
        newZoneIcon(host.getZone(1)));
    return null;
  }

  @Override
  public Void visit(Message.SendActiveCubesToOneOfTwoZones host) {
    fillInPlaceholders(repeatPlaceholder(constants.sendActiveCubesToOneOfTwoZones(), 0,
            host.getCount()),
        newCube(host.getColor(0)),
        newZoneIcon(host.getZone(0)),
        newZoneIcon(host.getZone(1)));
    return null;
  }

  @Override
  public Void visit(Message.SendActiveCubesToZone host) {
    fillInPlaceholders(repeatPlaceholder(constants.sendActiveCubesToZone(), 0, host.getCount()),
        newCube(host.getColor(0)),
        newZoneIcon(host.getZone(0)));
    return null;
  }

  @Override
  public Void visit(Message.SendPassiveCubesToZone host) {
    fillInPlaceholders(repeatPlaceholder(constants.sendPassiveCubesToZone(), 0, host.getCount()),
        newCube(host.getColor(0)),
        newZoneIcon(host.getZone(0)));
    return null;
  }

  @Override
  public Void visit(Message.SelectAction host) {
    addText(constants.selectActionToExecute());
    return null;
  }

  @Override
  public Void visit(Message.SendPassiveCubesToAnyZone host) {
    fillInPlaceholders(repeatPlaceholder(constants.sendPassiveCubesToAnyZone(), 0,
        host.getCount()),
        newCube(host.getColor(0)),
        newZoneIcon(InfluenceType.RELIGIOUS),
        newZoneIcon(InfluenceType.POLITIC),
        newZoneIcon(InfluenceType.ECONOMIC),
        newZoneIcon(InfluenceType.CULTURAL));
    return null;
  }

  @Override
  public Void visit(Message.SendPassiveCubesToAnyZoneOrCitadel host) {
    fillInPlaceholders(repeatPlaceholder(constants.sendPassiveCubesToAnyZoneOrCitadel(), 0,
        host.getCount()),
        newCube(host.getColor(0)),
        newZoneIcon(InfluenceType.RELIGIOUS),
        newZoneIcon(InfluenceType.POLITIC),
        newZoneIcon(InfluenceType.ECONOMIC),
        newZoneIcon(InfluenceType.CULTURAL),
        newZoneIcon(InfluenceType.CITADEL));
    return null;
  }

  @Override
  public Void visit(Message.SendPassiveCubesToThisTile host) {
    fillInPlaceholders(repeatPlaceholder(constants.sendPassiveCubesToThisTile(), 0,
        host.getCount()), newCube(host.getColor(0)));
    return null;
  }

  @Override
  public Void visit(Message.SendActiveCubesToThisTile host) {
    fillInPlaceholders(repeatPlaceholder(constants.sendActiveCubesToThisTile(), 0, host.getCount()),
        newCube(host.getColor(0)));
    return null;
  }

  @Override
  public Void visit(Message.SendActiveCubesToThisTileAndExecuteAction host) {
    fillInPlaceholders(repeatPlaceholder(constants.sendActiveCubesToThisTileAndExecuteAction(), 0,
        host.getCount()), newCube(host.getColor(0)), newAction(host.getActionType()));
    return null;
  }

  @Override
  public Void visit(Message.ActivateCubes host) {
    fillInPlaceholders(repeatPlaceholder(constants.activateCubes(), 0,
        host.getCount()), newCube(host.getColor(0)));
    return null;
  }

  @Override
  public Void visit(Message.ScorePoints host) {
    addText(messages.scorePoints(host.getCount()));
    return null;
  }

  @Override
  public Void visit(Message.MoveOneOrTwoCubesSelectOrigin host) {
    fillInPlaceholders(constants.moveOneOrTwoCubesSelectOrigin(), newCube(host.getColor(0)));
    return null;
  }

  @Override
  public Void visit(Message.MoveCubesSelectDestination host) {
    fillInPlaceholders(repeatPlaceholder(constants.moveCubesSelectDestination(), 0,
        host.getCount()), newCube(host.getColor(0)), newZoneIcon(host.getZone(0)));
    return null;
  }

  @Override
  public Void visit(Message.SelectWhereToEmptyTile host) {
    fillInPlaceholders(constants.selectWhereToEmptyTile(), newPawn(host.getColor(0)),
        newZoneIcon(InfluenceType.RELIGIOUS),
        newZoneIcon(InfluenceType.POLITIC),
        newZoneIcon(InfluenceType.ECONOMIC),
        newZoneIcon(InfluenceType.CULTURAL));
    return null;
  }

  @Override
  public Void visit(Message.MoveArchitectOut host) {
    fillInPlaceholders(constants.moveArchitectOut(), newPawn(host.getColor(0)));
    return null;
  }

  @Override
  public Void visit(Message.RemoveNeutralArchitect host) {
    fillInPlaceholders(constants.removeNeutralArchitect(), newPawn(PlayerColor.NEUTRAL));
    return null;
  }

  // TODO(beaudoin): Refactor, the following visit methods are all quite similar.

  @Override
  public Void visit(Message.InformationOnZoneScore host) {
    ZoneScoringInformation scoringInformation = host.getScoringInformation();

    // Build sprite array, will be useful later.
    ComplexText.SpriteComponent[] sprites = new ComplexText.SpriteComponent[11];
    sprites[0] = newZoneIcon(scoringInformation.getZoneToScore());

    StringBuilder scoreDisplay = new StringBuilder();
    StringBuilder cascadeDisplay = new StringBuilder();
    for (PlayerColor playerColor : PlayerColor.NORMAL) {
      // Placeholder 0 is for the zone logo.
      int placeholderIndexForPawn = playerColor.normalColorIndex() + 1;
      int placeholderIndexForCube = playerColor.normalColorIndex() + 6;  // 5 pawns

      sprites[placeholderIndexForPawn] = newPawn(playerColor);
      sprites[placeholderIndexForCube] = newCube(playerColor);

      int score = scoringInformation.getScore(playerColor);
      if (score > 0) {
        if (scoreDisplay.length() > 0) {
          scoreDisplay.append(", ");
        }
        scoreDisplay.append("[" + placeholderIndexForPawn + "]: " + score);
      }

      int cubesToCascade = scoringInformation.getCubesToCascade(playerColor);
      if (cubesToCascade > 0) {
        if (cascadeDisplay.length() == 0) {
          // First cascade, insert the cascade size.
          cascadeDisplay.append(cubesToCascade);
        }
        cascadeDisplay.append("[" + placeholderIndexForCube + "]");
      }
    }

    if (scoreDisplay.length() == 0) {
      // No score.
      fillInPlaceholders(constants.scoreZoneWithoutScore(),
          newZoneIcon(scoringInformation.getZoneToScore()));
    } else if (cascadeDisplay.length() == 0) {
      // No cascade.
      String message = replacePlaceholders(constants.scoreZoneWithoutCascade(),
          "[0]", scoreDisplay.toString());
      fillInPlaceholders(message, sprites);
    } else {
      String message = replacePlaceholders(constants.scoreZoneWithCascade(),
          "[0]", scoreDisplay.toString(), cascadeDisplay.toString());
      fillInPlaceholders(message, sprites);
    }
    return null;
  }

  @Override
  public Void visit(Message.InformationOnIncompleteBuildingsScore host) {
    ScoringInformation scoringInformation = host.getScoringInformation();

    // Build sprite array, will be useful later.
    ComplexText.SpriteComponent[] sprites = new ComplexText.SpriteComponent[5];
    StringBuilder scoreDisplay = new StringBuilder();
    for (PlayerColor playerColor : PlayerColor.NORMAL) {
      int placeholderIndexForPawn = playerColor.normalColorIndex();
      sprites[placeholderIndexForPawn] = newPawn(playerColor);

      int score = scoringInformation.getScore(playerColor);
      if (score > 0) {
        if (scoreDisplay.length() > 0) {
          scoreDisplay.append(", ");
        }
        scoreDisplay.append("[" + placeholderIndexForPawn + "]: " + score);
      }
    }

    if (scoreDisplay.length() == 0) {
      // No score.
      addText(constants.scoreIncompleteBuildingsWithoutScore());
    } else {
      String message = replacePlaceholders(constants.scoreIncompleteBuildings(),
          scoreDisplay.toString());
      fillInPlaceholders(message, sprites);
    }
    return null;
  }

  @Override
  public Void visit(Message.InformationOnActiveCubesScore host) {
    ScoringInformation scoringInformation = host.getScoringInformation();

    // Build sprite array, will be useful later.
    ComplexText.SpriteComponent[] sprites = new ComplexText.SpriteComponent[5];
    StringBuilder scoreDisplay = new StringBuilder();
    for (PlayerColor playerColor : PlayerColor.NORMAL) {
      int placeholderIndexForPawn = playerColor.normalColorIndex();
      sprites[placeholderIndexForPawn] = newPawn(playerColor);

      int score = scoringInformation.getScore(playerColor);
      if (score > 0) {
        if (scoreDisplay.length() > 0) {
          scoreDisplay.append(", ");
        }
        scoreDisplay.append("[" + placeholderIndexForPawn + "]: " + score);
      }
    }

    if (scoreDisplay.length() == 0) {
      // No score.
      addText(constants.scoreActiveCubesWithoutScore());
    } else {
      String message = replacePlaceholders(constants.scoreActiveCubes(),
          scoreDisplay.toString());
      fillInPlaceholders(message, sprites);
    }
    return null;
  }

  @Override
  public Void visit(Message.InformationOnBuildingsScore host) {
    ScoringInformation scoringInformation = host.getScoringInformation();

    // Build sprite array, will be useful later.
    ComplexText.SpriteComponent[] sprites = new ComplexText.SpriteComponent[5];
    StringBuilder scoreDisplay = new StringBuilder();
    for (PlayerColor playerColor : PlayerColor.NORMAL) {
      int placeholderIndexForPawn = playerColor.normalColorIndex();
      sprites[placeholderIndexForPawn] = newPawn(playerColor);

      int score = scoringInformation.getScore(playerColor);
      if (score > 0) {
        if (scoreDisplay.length() > 0) {
          scoreDisplay.append(", ");
        }
        scoreDisplay.append("[" + placeholderIndexForPawn + "]: " + score);
      }
    }

    if (scoreDisplay.length() == 0) {
      // No score.
      addText(constants.scoreBuildingsWithoutScore());
    } else {
      String message = replacePlaceholders(constants.scoreBuildings(),
          scoreDisplay.toString());
      fillInPlaceholders(message, sprites);
    }
    return null;
  }

  @Override
  public Void visit(Message.GameCompleted host) {
    addText(constants.gameCompleted());
    return null;
  }

  private void addText(String text) {
    components.add(new ComplexText.TextComponent(text));
  }

  private ComplexText.SpriteComponent newPawn(PlayerColor playerColor) {
    return new ComplexText.SpriteComponent(spriteResources.getPawn(playerColor), 0.7, -0.25);
  }

  private ComplexText.SpriteComponent newCube(PlayerColor playerColor) {
    return new ComplexText.SpriteComponent(spriteResources.getCube(playerColor), 1, 0);
  }

  private ComplexText.SpriteComponent newZoneIcon(InfluenceType influenceZone) {
    return new ComplexText.SpriteComponent(spriteResources.getZoneLogo(influenceZone), 1.2, -0.4);
  }

  private ComplexText.SpriteComponent newAction(ActionType actionType) {
    return new ComplexText.SpriteComponent(spriteResources.getAction(actionType), 0.35, -0.2);
  }

  private String repeatPlaceholder(String text, int placeholderIndex, int count) {
    if (count == 1) {
      return text;
    }
    String placeholder = "[" + placeholderIndex + "]";
    final StringBuilder repeatedPlaceholderSB = new StringBuilder();
    for (int i = 0; i < count; i++) {
      repeatedPlaceholderSB.append(placeholder);
    }
    String repeatedPlaceholder = repeatedPlaceholderSB.toString();
    int placeholderLength = placeholder.length();
    StringBuilder result = new StringBuilder();
    int index = 0, lastIndex = 0;
    while ((index = text.indexOf(placeholder, lastIndex)) >= 0) {
      result.append(text, lastIndex, index);
      result.append(repeatedPlaceholder);
      lastIndex = index + placeholderLength;
    }
    if (lastIndex < text.length() - 1) {
      result.append(text.substring(lastIndex));
    }
    return result.toString();
  }

  private String replacePlaceholders(String text, String... replacements) {
    int startIndex = 0;
    int index = 0;
    StringBuilder result = new StringBuilder();
    while ((index = text.indexOf('[', startIndex)) >= 0) {
      if (index > startIndex) {
        result.append(text, startIndex, index);
      }
      int integerStart = index + 1;
      startIndex = text.indexOf(']', integerStart);
      assert startIndex > integerStart;
      int placeholderIndex = Integer.parseInt(text.substring(integerStart, startIndex));
      assert placeholderIndex < replacements.length;
      result.append(replacements[placeholderIndex]);
      startIndex++;
    }
    if (startIndex < text.length()) {
      result.append(text, startIndex, text.length());
    }
    return result.toString();
  }

  private void fillInPlaceholders(String text, ComplexText.SpriteComponent... sprites) {
    int startIndex = 0;
    int index = 0;
    while ((index = text.indexOf('[', startIndex)) >= 0) {
      if (index > startIndex) {
        addText(text.substring(startIndex, index));
      }
      int integerStart = index + 1;
      startIndex = text.indexOf(']', integerStart);
      assert startIndex > integerStart;
      int placeholderIndex = Integer.parseInt(text.substring(integerStart, startIndex));
      assert placeholderIndex < sprites.length;
      components.add(sprites[placeholderIndex]);
      startIndex++;
    }
    if (startIndex < text.length()) {
      addText(text.substring(startIndex));
    }
  }
}
