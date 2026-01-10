package foxie.rpg_college.texture;

import java.awt.image.BufferedImage;

// Record ini menyimpan data berkaitan dengan
// tekstur atau gambar beserta lebar and panjang
// tidak banyak yang dilakukan untuk sekarang ini
public record Texture(
  int width,
  int height,
  BufferedImage image
) {
  public Texture(BufferedImage image) {
    this(Texture.validateSize(image.getWidth(null)), Texture.validateSize(image.getHeight(null)), image);
  }
  
  // Fungsi ini diperlukan karena java.awt.Image menentukan
  // kalau getWidth dan getHeight mungkin return hasil negatif
  // jadi kalau itu terjadi lempar exception saja dari pada
  // angka negatif tersebut menjadi tersembunyi
  private static int validateSize(int x) {
    if (x < 1.0) {
      throw new RuntimeException("Unexpected size");
    }
    return x;
  }
}
