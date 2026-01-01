package foxie.rpg_college.tile;

import foxie.rpg_college.Game;

public class TileList {
  public final WallTile WALL_TILE;
  public final LavaTile LAVA_TILE;

  public TileList(Game game) {
    this.WALL_TILE = new WallTile(game);
    this.LAVA_TILE = new LavaTile(game);
  }
}
