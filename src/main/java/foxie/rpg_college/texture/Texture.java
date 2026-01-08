package foxie.rpg_college.texture;

import java.awt.image.BufferedImage;

public record Texture(
  int width,
  int height,
  BufferedImage image
) {
  public Texture(BufferedImage image) {
    this(Texture.validateSize(image.getWidth(null)), Texture.validateSize(image.getHeight(null)), image);
  }
  
  private static int validateSize(int x) {
    if (x < 1.0) {
      throw new RuntimeException("Unexpected size");
    }
    return x;
  }
}
