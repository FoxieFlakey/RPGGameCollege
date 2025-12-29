package foxie.rpg_college;

import java.net.URL;

public class Util {
  public static URL getResource(String path) {
    return Util.class.getResource(path);
  }

  public static float getTime() {
    return ((float) System.nanoTime()) / 1_000_000_000.0f;
  }
}
