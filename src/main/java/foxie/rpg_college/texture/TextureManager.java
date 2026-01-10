package foxie.rpg_college.texture;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Supplier;

import javax.imageio.ImageIO;

import foxie.rpg_college.Util;

// Kelas texture ini bertugas untuk memanajemen
// texture-texture yang game punya
public class TextureManager {
  private final HashMap<String, Texture> textures = new HashMap<>();
  private final HashMap<String, Supplier<Texture>> loaders = new HashMap<>();
  private boolean isAllLoaded = false;
  
  // Fungsi ini menambahkan tekstur ke map 'loaders'
  // agar nanti texturenya di load dan dapat diakses
  // lagi dengan 'getTexture' setelah diload meggunakan
  // 'id' yang diberikan
  public void addTexture(String id, Supplier<Texture> loader) {
    if (this.isAllLoaded) {
      throw new IllegalStateException("Cannot add texture on manager loaded");
    }
    
    if (this.textures.containsKey(id) || this.loaders.containsKey(id)) {
      throw new IllegalArgumentException("Attempt to add texture that already loaded");
    }
    
    this.loaders.put(id, loader);
  }
  
  // Sama seperti diatas, tetapi ini memudahkan kalau ingin
  // menambah tekstur yang di load meggunakan fungsi Util.getResource
  public void addTexture(String id, String resourcePath) {
    this.addTexture(id, () -> {
      try {
        return new Texture(ImageIO.read(Util.getResource(resourcePath)));
      } catch (IOException e) {
        throw new RuntimeException("Cannot load texture " + id, e);
      }
    });
  }
  
  // Mencari texture yang memiliki id specific
  public Texture getTexture(String id) {
    if (!this.isAllLoaded) {
      throw new IllegalStateException("Textures not loaded yet");
    }
    
    if (!this.textures.containsKey(id)) {
      throw new IllegalArgumentException("Cannot find texture");
    }
    
    return this.textures.get(id);
  }
  
  // Mulai process untuk menload semua tekstur yang sudah
  // ditambah. Setelah ini penambahkan tekstur baru tidak diizinkan
  public void loadAll() {
    if (this.isAllLoaded) {
      throw new IllegalStateException("Cannot load again");
    }
    this.isAllLoaded = true;
    
    for (Entry<String, Supplier<Texture>> idAndLoader : this.loaders.entrySet()) {
      this.textures.put(idAndLoader.getKey(), Optional.ofNullable(idAndLoader.getValue().get()).get());
    }
  }
}
