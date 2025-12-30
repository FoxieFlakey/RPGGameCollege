package foxie.rpg_college;

import java.net.URL;

public class Util {
  public static URL getResource(String path) {
    return Util.class.getResource(path);
  }

  public static float getTime() {
    return ((float) System.nanoTime()) / 1_000_000_000.0f;
  }

  public static float normalizeAngle(float degree) {
    degree %= 360.0f;

    if (degree < 0.0f) {
      degree = 360.0f + degree;
    }
    return degree;
  }
}
