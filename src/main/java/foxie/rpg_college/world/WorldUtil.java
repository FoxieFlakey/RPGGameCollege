package foxie.rpg_college.world;

import java.awt.Graphics2D;
import java.awt.Image;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Vec2;

public class WorldUtil {
  public static void renderBackground(World world, Graphics2D g, Image backgroundImage) {
    Vec2 backgroundImageSize = new Vec2(backgroundImage.getWidth(null), backgroundImage.getHeight(null));
    
    FloatRectangle visible = world.getGame().getCamera().getVisibleWorld();
    Vec2 coord = visible.getTopLeftCorner().add(backgroundImageSize.mul(0.5f));
    Vec2 visibleSize = visible.getSize();
    Vec2 visibleSizeOutput = world.getGame().getCamera().translateScreenToAWTGraphicsCoord(visible.getSize());

    // Image might be not ready, but lets ignore
    g.drawImage(
      backgroundImage,
      // Dest coords
      0, 0,
      (int) visibleSizeOutput.x(), (int) visibleSizeOutput.y(),
      // Source coords
      (int) coord.x(), (int) coord.y(),
      (int) (visibleSize.x() + coord.x()), (int) (visibleSize.y() + coord.y()),
      null
    );
  }
}
