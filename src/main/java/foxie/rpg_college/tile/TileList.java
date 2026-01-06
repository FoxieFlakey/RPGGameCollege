package foxie.rpg_college.tile;

import foxie.rpg_college.Game;
import foxie.rpg_college.WorldManager;

public class TileList {
  public final WallTile WALL_TILE;
  public final LavaTile LAVA_TILE;
  public final PortalTile PORTAL_TO_BATTLE;
  public final PortalTile PORTAL_TO_OVERWORLD;

  public TileList(Game game) {
    this.WALL_TILE = new WallTile(game);
    this.LAVA_TILE = new LavaTile(game);
    this.PORTAL_TO_BATTLE = new PortalTile(game, WorldManager.BATTLE_ARENA_ID);
    this.PORTAL_TO_OVERWORLD = new PortalTile(game, WorldManager.OVERWORLD_ID);
  }
}
