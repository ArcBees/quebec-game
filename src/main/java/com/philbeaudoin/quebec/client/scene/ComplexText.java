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

package com.philbeaudoin.quebec.client.scene;

import java.util.ArrayList;

import com.google.gwt.canvas.dom.client.Context2d;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.MutableVector2d;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Scene tree node drawing black text with the ability to embed icons in the text. Surrounds the
 * text with a white box.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ComplexText extends SceneNodeImpl {

  private static final double HEIGHT_PER_LINE = 0.03;

  /**
   * A list of complex components.
   */
  public static class ComponentList {

    private final ArrayList<Component> components;

    public ComponentList() {
      this.components = new ArrayList<Component>();
    }

    public Vector2d calculateSize(Context2d context) {
      Cursor cursor = new Cursor(new Vector2d(0,0));
      double maxX = 0;
      for (Component component : components) {
        component.updateCursor(cursor, context);
        maxX = Math.max(maxX, cursor.pos.getX());
      }
      return new Vector2d(maxX, cursor.pos.getY() + HEIGHT_PER_LINE);
    }

    public Vector2d calculateApproximateSize() {
      Cursor cursor = new Cursor(new Vector2d(0,0));
      double maxX = 0;
      for (Component component : components) {
        component.updateApproximateCursor(cursor);
        maxX = Math.max(maxX, cursor.pos.getX());
      }
      return new Vector2d(maxX, cursor.pos.getY() + HEIGHT_PER_LINE);
    }

    public void draw(Vector2d pos, Context2d context) {
      Cursor cursor = new Cursor(pos);
      for (Component component : components) {
        component.draw(cursor.pos, context);
        component.updateCursor(cursor, context);
      }
    }

    public void add(Component component) {
      components.add(component);
    }

    public boolean isEmpty() {
      return components.isEmpty();
    }

    /**
     * Adds a list of components containing only text and end of lines.
     * @param string The multiline text. Lines should be separated by \n.
     */
    public void addFromMultilineText(String string) {
      String[] lines = string.split("\n");
      boolean first = true;
      for (String line : lines) {
        if (!first) {
          add(new EndLineComponent());
        }
        first = false;
        add(new TextComponent(line));
      }
    }

    /**
     * Break lines that are too longs. Lines will only be broken at the end of a word or between
     * two existing components.
     * @param maxLineLength
     */
    public void breakLongLines(double maxLineLength) {
      assert maxLineLength > 0;
      Cursor cursor = new Cursor(new Vector2d(0,0));
      int i = 0;
      while (i < components.size()) {
        Component component = components.get(i);
        double prevX = cursor.pos.getX();
        component.updateApproximateCursor(cursor);
        if (cursor.pos.getX() > maxLineLength) {
          // A long line, break it apart.
          ComponentPair newComponents = component.breakApart(maxLineLength - prevX);
          if (newComponents != null && newComponents.first != null) {
            // The component has been broken apart, remove it and add the two new ones with a line
            // break in between. The second component is not necessarily broken enough, so go to
            // it and break it down again.
            components.remove(i);
            components.add(i, newComponents.first);
            components.add(i + 1, new EndLineComponent());
            if (newComponents.second == null) {
              // Just one component. It was probably truncated somehow.
              i++;
            } else {
              components.add(i + 2, newComponents.second);
              i += 2;
            }
            cursor.nextLine(HEIGHT_PER_LINE);
          } else if (newComponents != null) {
            // The component has been broken but the first half is empty. Insert a line break before
            // the second component, unless we're at the beginning of the line in which case we add
            // it immediately and we do not break.
            if (cursor.pos.getX() >= 0) {
              // Insert the line break before the component.
              components.remove(i);
              components.add(i, new EndLineComponent());
              components.add(i + 1, newComponents.second);
              i++;
              cursor.nextLine(HEIGHT_PER_LINE);
            } else {
              // Don't insert any line break.
              components.remove(i);
              components.add(i, newComponents.second);
            }
          } else {
            // The component cannot be broken. Insert a line break right before it, unless we're at
            // the beginning of the line in which case we accept to go beyond the maximum length
            // and insert the break after the component.
            if (cursor.pos.getX() >= 0) {
              // Insert the line break before the component.
              components.add(i, new EndLineComponent());
              i++;
              cursor.nextLine(HEIGHT_PER_LINE);
            } else {
              // Insert the line break after the component.
              components.add(i + 1, new EndLineComponent());
              i += 2;
              cursor.nextLine(HEIGHT_PER_LINE);
            }
          }
        } else {
          // Normal case, just go to the next component.
          i++;
        }
      }
    }
  }

  private static class ComponentPair {
    Component first;
    Component second;
    public ComponentPair(TextComponent first, TextComponent second) {
      this.first = first;
      this.second = second;
    }
  }

  /**
   * The position at which the next component should be inserted.
   */
  private static class Cursor {
    double lineStartX;
    MutableVector2d pos;
    Cursor(Vector2d pos) {
      lineStartX = pos.getX();
      this.pos = new MutableVector2d(pos);
    }
    private void addToLine(double width) {
      pos.setX(pos.getX() + width);
    }
    private void nextLine(double height) {
      pos.setX(lineStartX);
      pos.setY(pos.getY() + height);
    }
  }

  /**
   * A component of a complex text.
   */
  public interface Component {
    /**
     * Updates the cursor to indicate the position the next component should be drawn at.
     * @param cursor The cursor used to draw this component.
     * @param context The current context.
     */
    void updateCursor(Cursor cursor, Context2d context);
    /**
     * Updates the cursor to indicate the position the next component should be drawn at. This
     * method cannot calculate the size precisely as it doesn't have access to the context.
     * @param cursor The cursor used to draw this component.
     */
    void updateApproximateCursor(Cursor cursor);
    /**
     * Draws the component at a given position.
     * @param pos The position at which to draw the component.
     * @param context The context into which to draw the component.
     */
    void draw(Vector2d pos, Context2d context);
    /**
     * Breaks this component apart in two components, ensuring the first one does not exceed
     * maxLength in size. Returns null if the component cannot be broken.
     * @param maxLength The maximum length of the first component after being broken down.
     * @return A pair of components where the first one has a length less or equal to maxLength, or
     *     null if the component cannot be broken in such a way.
     */
    ComponentPair breakApart(double maxLength);
  }

  /**
   * A text component of a complex text.
   */
  public static class TextComponent implements Component {
    final String text;
    final Text node;
    public TextComponent(String text) {
      this.text = text;
      node = new Text(text, new ConstantTransform());
    }
    @Override
    public void updateCursor(Cursor cursor, Context2d context) {
      cursor.addToLine(getWidth(context));
    }
    @Override
    public void updateApproximateCursor(Cursor cursor) {
      // TODO(beaudoin): Can we get a better approximation?
      cursor.addToLine(0.0137 * text.length());
    }
    @Override
    public void draw(Vector2d pos, Context2d context) {
      node.setTransform(new ConstantTransform(pos));
      node.draw(0, context);
    }
    private double getWidth(Context2d context) {
      context.save();
      context.scale(0.001, 0.001);
      context.setFont("25px arial");
      double result = context.measureText(text).getWidth();
      context.restore();
      return result / 1000.0;
    }
    @Override
    public ComponentPair breakApart(double maxLength) {
      int previousFirstSpace = -1, previousLastSpace = -1, foundAt;
      while (true) {
        foundAt = text.indexOf(' ', previousLastSpace + 1);
        if (foundAt == -1) {
          foundAt = text.length();
        }
        // TODO(beaudoin): Can we get a better approximation?
        double totalLength = 0.0137 * (foundAt - 1);
        if (totalLength > maxLength) {
          if (previousLastSpace == -1) {
            // Cannot break apart.
            return null;
          } else if (previousFirstSpace == 0) {
            // Only have a second component.
            return new ComponentPair(null,
                new TextComponent(text.substring(previousLastSpace + 1)));
          } else {
            // Have a first and a second component.
            return new ComponentPair(
                new TextComponent(text.substring(0, previousFirstSpace)),
                new TextComponent(text.substring(previousLastSpace + 1)));
          }
        } else {
          if (foundAt == text.length()) {
            // No need to break anything.
            return new ComponentPair(this, null);
          }
          // Eat white spaces.
          previousFirstSpace = previousLastSpace = foundAt;
          while (text.charAt(previousLastSpace) == ' ' && previousLastSpace < text.length()) {
            previousLastSpace++;
          }
          previousLastSpace--;
        }
      }
    }
  }

  /**
   * A sprite component of a complex text.
   */
  public static class SpriteComponent implements Component {
    private SpriteResources.Info info;
    final Sprite node;
    final double scale;
    final double verticalAdjustment;
    public SpriteComponent(SpriteResources.Info info, double scale, double verticalAdjustment) {
      this.info = info;
      this.node = new Sprite(info);
      this.scale = scale;
      this.verticalAdjustment = verticalAdjustment;
    }
    @Override
    public void updateCursor(Cursor cursor, Context2d context) {
      updateApproximateCursor(cursor);
    }
    @Override
    public void updateApproximateCursor(Cursor cursor) {
      cursor.addToLine(getWidth());
    }
    @Override
    public void draw(Vector2d pos, Context2d context) {
      node.setTransform(new ConstantTransform(
          new Vector2d(pos.getX() + getWidth() / 2.0,
              pos.getY() - getHeight() / 2.0 * (1 + verticalAdjustment)), scale, 0));
      node.draw(0, context);
    }
    private double getWidth() {
      return info.getWidth() * info.getSizeFactor() * scale;
    }
    private double getHeight() {
      return info.getHeight() * info.getSizeFactor() * scale;
    }
    @Override
    public ComponentPair breakApart(double maxLength) {
      // Cannot be broken.
      return null;
    }
  }

  /**
   * A component of a complex text that goes to the next line.
   */
  public static class EndLineComponent implements Component {
    @Override
    public void updateCursor(Cursor cursor, Context2d context) {
      updateApproximateCursor(cursor);
    }
    @Override
    public void updateApproximateCursor(Cursor cursor) {
      cursor.nextLine(HEIGHT_PER_LINE);
    }
    @Override
    public void draw(Vector2d pos, Context2d context) {
    }
    @Override
    public ComponentPair breakApart(double maxLength) {
      // Cannot be broken.
      return null;
    }
  }

  private final ComponentList components;
  private final String gradientFrom;
  private final String gradientTo;

  public ComplexText(ComponentList components, Transform transform) {
    this(components, "#aaa", "#ddd", transform);
  }

  public ComplexText(ComponentList components, String gradientFrom,
      String gradientTo, Transform transform) {
    this(components, gradientFrom, gradientTo, transform, true);
  }

  private ComplexText(ComponentList components, String gradientFrom, String gradientTo,
      Transform transform, boolean visible) {
    super(transform, visible);
    this.components = components;
    this.gradientFrom = gradientFrom;
    this.gradientTo = gradientTo;
  }

  @Override
  public void drawUntransformed(double time, Context2d context) {
    Vector2d sizeInfo = components.calculateSize(context);
    // Center horizontally.
    double dx = sizeInfo.getX() / 2.0;
    double dy = sizeInfo.getY() / 2.0;

    Rectangle rectangle = new Rectangle(-dx - 0.008, -dy - 0.005, dx + 0.008, dy + 0.005,
        gradientFrom, gradientTo, "#000", 2);
    rectangle.draw(time, context);
    components.draw(new MutableVector2d(-dx, -dy - 0.005 + HEIGHT_PER_LINE), context);
  }

  @Override
  public SceneNode deepClone() {
    return new ComplexText(components, gradientFrom, gradientTo, getTransform(), isVisible());
  }
}
