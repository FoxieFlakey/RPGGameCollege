package foxie.rpg_college.entity;

import java.awt.geom.AffineTransform;

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
  
  public static float distanceBetween(Entity a, Entity b) {
    return a.getPos().sub(b.getPos()).magnitude();
  }
  
  // returned affine transform can be applied so 0, 0 is the center of the entity
  // also properly rotated, where 0 degree rotation is pointing to negative Y
  //
  // So the caller can render an arrow pointing up from 0, 0. It would properly
  // translated and rotated to correct position
  //
  // It does not need to be translated again by calculateRenderBox
  public static AffineTransform calculateCameraTransform(Entity e) {
    Camera camera = e.getWorld().getGame().getCamera();
    Vec2 pos = camera.translateWorldToAWTGraphicsCoord(e.getPos());
    AffineTransform transform = new AffineTransform();
    transform.translate(pos.x(), pos.y());
    transform.rotate(Math.toRadians(e.getRotation()));
    return transform;
  } 

  public static IVec2 fromWorldCoordToTileCoord(Vec2 coord) {
    return coord.div(Tile.SIZE.x()).round();
  }
}
