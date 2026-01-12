package foxie.rpg_college.entity;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import foxie.rpg_college.Vec2;

public class ProjectileHelper {
  // Method ini menyederhanakan kode yang meggambar projectile
  // render ini juga menggerakkan gambarnya agak kebelakang sehingga hitboxnya
  // sebenarnya di ujung projectile rata-rata seperti arrow yang kena kan
  // ujung panahnya contohnya :3
  public static void renderProjectile(Entity e, Graphics2D g, Image texture, Vec2 renderSize) {
    float textureWidth = texture.getWidth(null);
    float texureHeight = texture.getHeight(null);
    
    AffineTransform transform = EntityHelper.calculateCameraTransform(e);
    transform.scale(
      renderSize.x() / textureWidth,
      renderSize.y() / texureHeight
    );
    transform.translate(-renderSize.x() * 0.25f, -renderSize.y() * 0.05f);
    
    g.drawImage(texture, transform, null); 
  }
}
