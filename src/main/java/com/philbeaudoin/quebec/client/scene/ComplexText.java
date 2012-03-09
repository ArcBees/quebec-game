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

    public SizeInfo calculateSize(Context2d context) {
      SizeInfo sizeInfo = new SizeInfo();
      for (Component component : components) {
        component.getSize(context, sizeInfo);
      }
      return sizeInfo;
    }

    public SizeInfo calculateApproximateSize() {
      SizeInfo sizeInfo = new SizeInfo();
      for (Component component : components) {
        component.getApproximateSize(sizeInfo);
      }
      return sizeInfo;
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
  }

  /**
   * A class representing the size of an object.
   */
  public static class SizeInfo {
    private double totalHeight = HEIGHT_PER_LINE;
    private double maxLineWidth;
    private double currLineWidth;
    private void addToCurrLine(double width) {
      currLineWidth += width;
      if (currLineWidth > maxLineWidth) {
        maxLineWidth = currLineWidth;
      }
    }
    private void endOfLine() {
      currLineWidth = 0;
      totalHeight += HEIGHT_PER_LINE;
    }
    public double getWidth() {
      return maxLineWidth;
    }
    public double getHeight() {
      return totalHeight;
    }
  }

  /**
   * The position at which the next component should be inserted.
   */
  private static class Cursor {
    private double lineStartX;
    private MutableVector2d pos;
    private Cursor(Vector2d pos) {
      lineStartX = pos.getX();
      this.pos = new MutableVector2d(pos);
    }
  }

  /**
   * A component of a complex text.
   */
  public interface Component {
    void getSize(Context2d context, SizeInfo sizeInfo);
    /**
     * Updates the cursor to indicate the position the next component should be drawn at.
     * @param cursor The cursor used to draw this component.
     * @param context The current context.
     */
    void updateCursor(Cursor cursor, Context2d context);
    void getApproximateSize(SizeInfo sizeInfo);
    void draw(Vector2d pos, Context2d context);
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
    public void getSize(Context2d context, SizeInfo sizeInfo) {
      sizeInfo.addToCurrLine(getWidth(context));
    }
    @Override
    public void updateCursor(Cursor cursor, Context2d context) {
      cursor.pos.setX(cursor.pos.getX() + getWidth(context));
    }
    @Override
    public void draw(Vector2d pos, Context2d context) {
      node.setTransform(new ConstantTransform(pos));
      node.draw(0, context);
    }
    @Override
    public void getApproximateSize(SizeInfo sizeInfo) {
      // TODO(beaudoin): Approximate. Can we do better?
      sizeInfo.addToCurrLine(0.0137 * text.length());
    }
    private double getWidth(Context2d context) {
      context.save();
      context.scale(0.001, 0.001);
      context.setFont("25px arial");
      double result = context.measureText(text).getWidth();
      context.restore();
      return result / 1000.0;
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
    public void getSize(Context2d context, SizeInfo sizeInfo) {
      sizeInfo.addToCurrLine(getWidth());
    }
    @Override
    public void updateCursor(Cursor cursor, Context2d context) {
      cursor.pos.setX(cursor.pos.getX() + getWidth());
    }
    @Override
    public void draw(Vector2d pos, Context2d context) {
      node.setTransform(new ConstantTransform(
          new Vector2d(pos.getX() + getWidth() / 2.0,
              pos.getY() - getHeight() / 2.0 * (1 + verticalAdjustment)), scale, 0));
      node.draw(0, context);
    }
    @Override
    public void getApproximateSize(SizeInfo sizeInfo) {
      sizeInfo.addToCurrLine(getWidth());
    }
    private double getWidth() {
      return info.getWidth() * info.getSizeFactor() * scale;
    }
    private double getHeight() {
      return info.getHeight() * info.getSizeFactor() * scale;
    }
  }

  /**
   * A component of a complex text that goes to the next line.
   */
  public static class EndLineComponent implements Component {
    @Override
    public void getSize(Context2d context, SizeInfo sizeInfo) {
      sizeInfo.endOfLine();
    }
    @Override
    public void updateCursor(Cursor cursor, Context2d context) {
      cursor.pos.setX(cursor.lineStartX);
      cursor.pos.setY(cursor.pos.getY() + HEIGHT_PER_LINE);
    }
    @Override
    public void draw(Vector2d pos, Context2d context) {
    }
    @Override
    public void getApproximateSize(SizeInfo sizeInfo) {
      sizeInfo.endOfLine();
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
    SizeInfo sizeInfo = components.calculateSize(context);
    // Center horizontally.
    double dx = -sizeInfo.getWidth() / 2.0;
    double dy =  sizeInfo.getHeight() / 2.0;

    Rectangle rectangle = new Rectangle(dx * 1.07, -dy - 0.005,
        (dx + sizeInfo.getWidth()) * 1.14, dy + 0.005,
        gradientFrom, gradientTo, "#000", 2);
    rectangle.draw(time, context);
    components.draw(new MutableVector2d(dx, -dy - 0.005 + HEIGHT_PER_LINE), context);
  }

  @Override
  public SceneNode deepClone() {
    return new ComplexText(components, gradientFrom, gradientTo, getTransform(), isVisible());
  }
}
