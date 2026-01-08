package foxie.rpg_college.texture;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Supplier;

import javax.imageio.ImageIO;

import foxie.rpg_college.Util;

public class TextureManager {
  private final HashMap<String, Texture> textures = new HashMap<>();
  private final HashMap<String, Supplier<Texture>> loaders = new HashMap<>();
  private boolean isAllLoaded = false;
  
  public void addTexture(String id, Supplier<Texture> loader) {
    if (this.isAllLoaded) {
      throw new IllegalStateException("Cannot add texture on manager loaded");
    }
    
    if (this.textures.containsKey(id) || this.loaders.containsKey(id)) {
      throw new IllegalArgumentException("Attempt to add texture that already loaded");
    }
    
    this.loaders.put(id, loader);
  }
  
  public void addTexture(String id, String resourcePath) {
    this.addTexture(id, () -> {
      try {
        return new Texture(ImageIO.read(Util.getResource(resourcePath)));
      } catch (IOException e) {
        throw new RuntimeException("Cannot load texture " + id, e);
      }
    });
  }
  
  public Texture getTexture(String id) {
    if (!this.isAllLoaded) {
      throw new IllegalStateException("Textures not loaded yet");
    }
    
    if (!this.textures.containsKey(id)) {
      throw new IllegalArgumentException("Cannot find texture");
    }
    
    return this.textures.get(id);
  }
  
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
