package foxie.rpg_college;

import java.net.URL;

public class Util {
  public static URL getResource(String path) {
    return Util.class.getResource(path);
  }

  public static float getTime() {
    return ((float) System.nanoTime()) / 1_000_000_000.0f;
  }
  
  public static float clamp(float value, float min, float max) {
    if (min < value && value < max) {
      return value;
    } else if (value <= min) {
      return min;
    } else if (value >= max) {
      return max;
    }
    throw new RuntimeException("unexpected");
  }

  public static boolean isZero(double n) {
    // Looks unnecessary but.. floats and doubles
    // well can't exactly represent. So lets use this
    // Float and double only work sanely if its not equality
    // comparisons
    double sign = Math.signum(n);
    return sign > -1.0f && sign < 1.0f;
  }

  public static float normalizeAngle(float degree) {
    degree %= 360.0f;

    if (degree < 0.0f) {
      degree = 360.0f + degree;
    }
    return degree;
  }
}
