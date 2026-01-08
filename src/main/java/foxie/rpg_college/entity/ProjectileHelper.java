package foxie.rpg_college.entity;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Vec2;

public class ProjectileHelper {
  public static void renderProjectile(Entity e, Graphics2D g, Image texture, Vec2 renderSize) {
    float textureWidth = texture.getWidth(null);
    float texureHeight = texture.getHeight(null);
    
    FloatRectangle renderBox = EntityHelper.calculateRenderBox(e, renderSize);
    AffineTransform transform = EntityHelper.calculateCameraTransform(e);
    transform.scale(
      renderBox.getSize().x() / textureWidth,
      renderBox.getSize().y() / texureHeight
    );
    transform.translate(-renderSize.x() * 0.25f, -renderSize.y() * 0.05f);
    
    g.drawImage(texture, transform, null); 
  }
}
