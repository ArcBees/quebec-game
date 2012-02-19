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
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Scene tree node drawing black text with the ability to embed icons in the text. Surrounds the
 * text with a white box.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ComplexText extends SceneNodeImpl {

  /**
   * A component of a complex text.
   */
  public interface Component {
    double getWidth(Context2d context);
    double getApproximateWidth();
    void draw(double dx, Context2d context);
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
    public double getWidth(Context2d context) {
      context.save();
      context.scale(0.001, 0.001);
      context.setFont("25px arial");
      double result = context.measureText(text).getWidth();
      context.restore();
      return result / 1000.0;
    }
    @Override
    public void draw(double dx, Context2d context) {
      node.setTransform(new ConstantTransform(new Vector2d(dx, 0)));
      node.draw(0, context);
    }
    @Override
    public double getApproximateWidth() {
      // TODO(beaudoin): Approximate. Can we do better?
      return 0.0137 * text.length();
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
    public double getWidth(Context2d context) {
      return getWidth();
    }
    private double getWidth() {
      return info.getWidth() * info.getSizeFactor() * scale;
    }
    private double getHeight() {
      return info.getHeight() * info.getSizeFactor() * scale;
    }
    @Override
    public void draw(double dx, Context2d context) {
      node.setTransform(new ConstantTransform(
          new Vector2d(dx + getWidth() / 2.0, -getHeight() / 2.0 * (1 + verticalAdjustment)),
          scale, 0));
      node.draw(0, context);
    }
    @Override
    public double getApproximateWidth() {
      return getWidth();
    }
  }

  private final ArrayList<Component> components;
  private final String gradientFrom;
  private final String gradientTo;

  public ComplexText(ArrayList<Component> components, Transform transform) {
    this(components, "#aaa", "#ddd", transform);
  }

  public ComplexText(ArrayList<Component> components, String gradientFrom,
      String gradientTo, Transform transform) {
    this(components, gradientFrom, gradientTo, transform, true);
  }

  private ComplexText(ArrayList<Component> components, String gradientFrom, String gradientTo,
      Transform transform, boolean visible) {
    super(transform, visible);
    this.components = new ArrayList<Component>(components);
    this.gradientFrom = gradientFrom;
    this.gradientTo = gradientTo;
  }

  @Override
  public void drawUntransformed(double time, Context2d context) {
    double totalWidth = 0;
    for (Component component : components) {
      totalWidth += component.getWidth(context);
    }
    // Center horizontally.
    double dx = -totalWidth / 2.0;

    Rectangle rectangle = new Rectangle(dx * 1.07, -0.03, (dx + totalWidth) * 1.14, 0.01,
        gradientFrom, gradientTo, "#000", 2);
    rectangle.draw(time, context);

    for (Component component : components) {
      component.draw(dx, context);
      dx += component.getWidth(context);
    }
  }

  @Override
  public SceneNode deepClone() {
    return new ComplexText(components, gradientFrom, gradientTo, getTransform(), isVisible());
  }
}
