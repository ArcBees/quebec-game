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
import com.philbeaudoin.quebec.client.scene.ComplexText;
import com.philbeaudoin.quebec.client.scene.ComplexText.Component;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.state.ActionType;

/**
 * A message visitor used to render messages into complex text components.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class MessageRenderer implements Message.Visitor<Void> {

  private final GraphicConstants messages;
  private final SpriteResources spriteResources;
  private final ArrayList<ComplexText.Component> components =
      new ArrayList<ComplexText.Component>();

  @Inject
  public MessageRenderer(GraphicConstants messages, SpriteResources spriteResources) {
    this.messages = messages;
    this.spriteResources = spriteResources;
  }

  /**
   * Obtain the components rendered by this renderer.
   * @return
   */
  public ArrayList<Component> getComponents() {
    return components;
  }

  @Override
  public Void visit(Message.MoveYourArchitect host) {
    addText(messages.moveYourArchitect());
    return null;
  }

  @Override
  public Void visit(Message.MoveYourArchitectToThisTile host) {
    addText(messages.moveYourArchitectToThisTile());
    return null;
  }

  @Override
  public Void visit(Message.TakeThisLeaderCard host) {
    addText(messages.takeThisLeaderCard());
    return null;
  }

  @Override
  public Void visit(Message.MoveEitherArchitect host) {
    fillInPlaceholders(messages.moveEitherArchitect(),
        newArchitect(host.getColor(0)), newArchitect(host.getColor(1)));
    return null;
  }

  @Override
  public Void visit(Message.SelectWhichArchitect host) {
    fillInPlaceholders(messages.selectWhichArchitect(),
        newArchitect(host.getColor(0)), newArchitect(host.getColor(1)));
    return null;
  }

  @Override
  public Void visit(Message.SendPassiveCubesToOneOfTwoZones host) {
    fillInPlaceholders(repeatPlaceholder(messages.sendPassiveCubesToOneOfTwoZones(), 0,
        host.getCount()),
        newCube(host.getColor(0)),
        newZoneIcon(host.getZone(0)),
        newZoneIcon(host.getZone(1)));
    return null;
  }

  @Override
  public Void visit(Message.SendActiveCubesToOneOfTwoZones host) {
    fillInPlaceholders(repeatPlaceholder(messages.sendActiveCubesToOneOfTwoZones(), 0,
            host.getCount()),
        newCube(host.getColor(0)),
        newZoneIcon(host.getZone(0)),
        newZoneIcon(host.getZone(1)));
    return null;
  }

  @Override
  public Void visit(Message.SendActiveCubesToZone host) {
    fillInPlaceholders(repeatPlaceholder(messages.sendActiveCubesToZone(), 0, host.getCount()),
        newCube(host.getColor(0)),
        newZoneIcon(host.getZone(0)));
    return null;
  }

  @Override
  public Void visit(Message.SendPassiveCubesToZone host) {
    fillInPlaceholders(repeatPlaceholder(messages.sendPassiveCubesToZone(), 0, host.getCount()),
        newCube(host.getColor(0)),
        newZoneIcon(host.getZone(0)));
    return null;
  }

  @Override
  public Void visit(Message.SelectAction host) {
    addText(messages.selectActionToExecute());
    return null;
  }

  @Override
  public Void visit(Message.SendPassiveCubesToAnyZone host) {
    fillInPlaceholders(repeatPlaceholder(messages.sendPassiveCubesToAnyZone(), 0, host.getCount()),
        newCube(host.getColor(0)),
        newZoneIcon(InfluenceType.RELIGIOUS),
        newZoneIcon(InfluenceType.POLITIC),
        newZoneIcon(InfluenceType.ECONOMIC),
        newZoneIcon(InfluenceType.CULTURAL));
    return null;
  }

  @Override
  public Void visit(Message.SendPassiveCubesToThisTile host) {
    fillInPlaceholders(repeatPlaceholder(messages.sendPassiveCubesToThisTile(), 0, host.getCount()),
        newCube(host.getColor(0)));
    return null;
  }

  @Override
  public Void visit(Message.SendActiveCubesToThisTile host) {
    fillInPlaceholders(repeatPlaceholder(messages.sendActiveCubesToThisTile(), 0, host.getCount()),
        newCube(host.getColor(0)));
    return null;
  }

  @Override
  public Void visit(Message.SendActiveCubesToThisTileAndExecuteAction host) {
    fillInPlaceholders(repeatPlaceholder(messages.sendActiveCubesToThisTileAndExecuteAction(), 0,
        host.getCount()), newCube(host.getColor(0)), newAction(host.getActionType()));
    return null;
  }

  private void addText(String text) {
    components.add(new ComplexText.TextComponent(text));
  }

  private ComplexText.SpriteComponent newArchitect(PlayerColor playerColor) {
    return new ComplexText.SpriteComponent(spriteResources.getPawn(playerColor), 0.7, 0);
  }

  private ComplexText.SpriteComponent newCube(PlayerColor playerColor) {
    return new ComplexText.SpriteComponent(spriteResources.getCube(playerColor), 1, 0);
  }

  private ComplexText.SpriteComponent newZoneIcon(InfluenceType influenceZone) {
    return new ComplexText.SpriteComponent(spriteResources.getZoneLogo(influenceZone), 1, 0);
  }

  private ComplexText.SpriteComponent newAction(ActionType actionType) {
    return new ComplexText.SpriteComponent(spriteResources.getAction(actionType), 0.35, -0.2);
  }

  private String repeatPlaceholder(String text, int placeholderIndex, int count) {
    if (count == 1) {
      return text;
    }
    String placeholder = "{" + placeholderIndex + "}";
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

  private void fillInPlaceholders(String text, ComplexText.SpriteComponent... sprites) {
    int startIndex = 0;
    int index = 0;
    while ((index = text.indexOf('{', startIndex)) >= 0) {
      if (index > startIndex) {
        addText(text.substring(startIndex, index));
      }
      int integerStart = index + 1;
      startIndex = text.indexOf('}', integerStart);
      assert startIndex > integerStart;
      int spriteNumber = Integer.parseInt(text.substring(integerStart, startIndex));
      assert spriteNumber < sprites.length;
      components.add(sprites[spriteNumber]);
      startIndex++;
    }
    if (startIndex < text.length() - 1) {
      addText(text.substring(startIndex));
    }
  }

}
