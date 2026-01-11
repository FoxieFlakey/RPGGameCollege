package foxie.rpg_college.tile;

import foxie.rpg_college.Game;
import foxie.rpg_college.WorldManager;

// Menyimpan list-list tile yang ada dalam game
public class TileList {
  // Java memilki keyword 'final' yang berguna untuk konstanta
  // 'final' memblokir perubahan pada sebuah method/class/variabel
  // sehingga tidak berubah. Disini final dipakai ke variabel
  // berarti tidak bisa diedit sama sekali oleh apapun. Karena
  // final tersebut field lebih aman diexpose ke luar dan tidak
  // ada kode yang mengubah isinnya.
  
  public final WallTile WALL_TILE;
  public final LavaTile LAVA_TILE;

  // Ini salah satu dua tile instance nya yang beda
  // PortalTile memiliki tujuan yang berbeda jadi instance
  // nya berbada.
  public final PortalTile PORTAL_TO_BATTLE;
  public final PortalTile PORTAL_TO_OVERWORLD;

  public TileList(Game game) {
    this.WALL_TILE = new WallTile(game);
    this.LAVA_TILE = new LavaTile(game);
    this.PORTAL_TO_BATTLE = new PortalTile(game, WorldManager.BATTLE_ARENA_ID);
    this.PORTAL_TO_OVERWORLD = new PortalTile(game, WorldManager.OVERWORLD_ID);
  }
}
