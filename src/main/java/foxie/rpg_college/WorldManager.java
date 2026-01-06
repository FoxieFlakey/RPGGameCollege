package foxie.rpg_college;

import java.util.HashMap;
import java.util.Optional;

import foxie.rpg_college.world.World;

public class WorldManager {
  private final HashMap<String, World> worlds = new HashMap<>();
  
  public static final String OVERWORLD_ID = "overworld";
  
  public Optional<World> getWorld(String id) {
    return Optional.ofNullable(this.worlds.get(id));
  }
  
  public void addWorld(String id, World world) {
    if (this.worlds.containsKey(id)) {
      throw new IllegalStateException("Attempt to add '" + id +"' world twice");
    }
    
    this.worlds.put(id, Optional.of(world).get());
  }
}
