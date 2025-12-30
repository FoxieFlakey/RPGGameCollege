package foxie.rpg_college.entity;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.tile.Tile;

public class EntityHelper {
  public static FloatRectangle calculateRenderBox(Entity entity, Vec2 size) {
    Camera camera = entity.getWorld().getGame().getCamera();
    Vec2 pos = entity.getPos();
    
    return new FloatRectangle(
      camera.translateWorldToAWTGraphicsCoord(pos.sub(size.mul(0.5f))),
      camera.translateWorldToAWTGraphicsCoord(pos.add(size.mul(0.5f)))
    );
  }

  public static IVec2 fromWorldCoordToTileCoord(Vec2 coord) {
    return coord.div(Tile.SIZE.x()).round();
  }
}
