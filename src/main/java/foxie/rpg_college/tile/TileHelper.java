package foxie.rpg_college.tile;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;

public class TileHelper {
  public static FloatRectangle calculateRenderBox(Tile tile, IVec2 pos) {
    Camera camera = tile.getGame().getCamera();
    Vec2 fpos = Tile.fromTileCoordToWorldCoord(pos);
    
    return new FloatRectangle(
      camera.translateWorldToAWTGraphicsCoord(fpos.sub(Tile.SIZE.mul(0.5f))),
      camera.translateWorldToAWTGraphicsCoord(fpos.add(Tile.SIZE.mul(0.5f)))
    );
  }
}
