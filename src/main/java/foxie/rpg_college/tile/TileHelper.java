package foxie.rpg_college.tile;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;

public class TileHelper {
  // Fungsi pembantu ini menghitung area kotak
  // yang menjadi posisi Tilenya yang akan menjadi
  // hasil rendernya di posisi layar
  public static FloatRectangle calculateRenderBox(Tile tile, IVec2 pos) {
    Camera camera = tile.getGame().getCamera();
    Vec2 fpos = Tile.fromTileCoordToWorldCoord(pos);
    return camera.translateWorldToAWTGraphicsCoord(fpos, Tile.SIZE);
  }
}
