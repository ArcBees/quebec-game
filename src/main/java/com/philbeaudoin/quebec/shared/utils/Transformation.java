package com.philbeaudoin.quebec.shared.utils;

import com.google.gwt.canvas.dom.client.Context2d;

public interface Transformation {

  /**
   * Returns the translation component of the transformation.
   * @param time The time at which to get the transformation.
   * @return The translation component.
   */
  public abstract Vector2d getTranslation(double time);

  /**
   * Returns the scaling factor of that transformation.
   * @param time The time at which to get the transformation.
   * @return The scaling factor.
   */
  public abstract double getScaling(double time);

  /**
   * Returns the clockwise rotation angle of that transformation.
   * @param time The time at which to get the transformation.
   * @return The angle, in radians.
   */
  public abstract double getRotation(double time);

  /**
   * Applies the transformation.
   * @param time The time at which to apply the transformation.
   * @param context The canvas context into which to render.
   */
  public abstract void applies(double time, Context2d context);

  /**
   * Applies the transformation with an extra scaling factor.
   * @param time The time at which to apply the transformation.
   * @param context The canvas context into which to render.
   * @param sizeFactor An extra scaling factor to use.
   */
  public abstract void applies(double time, Context2d context, double sizeFactor);

}