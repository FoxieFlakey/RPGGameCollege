package foxie.rpg_college;

import java.util.HashMap;
import java.util.Optional;

import foxie.rpg_college.world.World;

// Kelas ini menjaga dan memanajemem berbagai dunia
// yang program telah masukkan dan juga memajukan simulasi-nya
// di method tick
public class WorldManager {
  private final HashMap<String, World> worlds = new HashMap<>();
  
  public static final String OVERWORLD_ID = "overworld";
  public static final String BATTLE_ARENA_ID = "battle_arena";
  
  // Cari dunia dengan ID specific
  public Optional<World> getWorld(String id) {
    return Optional.ofNullable(this.worlds.get(id));
  }
  
  // Menambahkan dunia baru ke penyimpanan untuk dilacak
  public void addWorld(String id, World world) {
    if (this.worlds.containsKey(id)) {
      throw new IllegalStateException("Attempt to add '" + id +"' world twice");
    }
    
    this.worlds.put(id, Optional.of(world).get());
  }
  
  // Memajukan simulasi tiap dunia, deltaTime
  // secara sederhana adalah waktu yang telah berlalu
  //
  // Jadi ini maksudnya simulasikan dunia seolah-olah
  // 'deltaTime' telah berlalu
  public void tick(float deltaTime) {
    for (World world : this.worlds.values()) {
      world.tick(deltaTime);
    }
  }
}
