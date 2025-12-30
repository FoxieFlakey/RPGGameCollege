package foxie.rpg_college.tile;

import foxie.rpg_college.Game;

public class TileList {
  public final Wall WALL_TILE;
  public final Lava LAVA_TILE;

  public TileList(Game game) {
    this.WALL_TILE = new Wall(game);
    this.LAVA_TILE = new Lava(game);
  }
}
