package foxie.rpg_college.texture;

import java.awt.Image;

public record Texture(
  int width,
  int height,
  Image image
) {
  public Texture(Image image) {
    this(Texture.validateSize(image.getWidth(null)), Texture.validateSize(image.getHeight(null)), image);
  }
  
  private static int validateSize(int x) {
    if (x < 1.0) {
      throw new RuntimeException("Unexpected size");
    }
    return x;
  }
}
