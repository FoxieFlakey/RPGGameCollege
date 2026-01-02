package foxie.rpg_college.entity;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import javax.imageio.ImageIO;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Util;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.damage.EntityDamageSource;

public class ArrowEntity extends ProjectileEntity {
  private static final Vec2 RENDER_SIZE = new Vec2(12.0f, 28.0f).mul(2.5f);
  private static final Vec2 COLLISION_SIZE = new Vec2(5.0f, 5.0f);
  
  private CollisionBox collisionBox = new CollisionBox(0.1f, new Vec2(0.0f, 0.0f), ArrowEntity.COLLISION_SIZE);
  
  private float damage = 5.0f;
  
  private static final URL ARROW_URL = Util.getResource("/arrow.png");
  private static Image ARROW_TEXTURE;
  
  static {
    try {
      ARROW_TEXTURE = ImageIO.read(ArrowEntity.ARROW_URL.openStream());
    } catch (IOException e) {
      throw new RuntimeException("Error loading arrow texture", e);
    }
  }
  
  public ArrowEntity(Entity shooter) {
    super(shooter, 1.0f, 400.0f);
  }
  
  @Override
  public boolean canBeHit(Entity other) {
    if (other instanceof LivingEntity) {
      LivingEntity living = (LivingEntity) other;
      if (living.isDead()) {
        // Dont collide with dead entity
        return false;
      }
      return true;
    }
    return false;
  }
  
  @Override
  public Optional<CollisionBox> getCollisionBox() {
    if (this.hasProjectileHitSomething()) {
      return Optional.empty();
    }
    return Optional.of(this.collisionBox);
  }
  
  @Override
  public void onHit(Entity other) {
    LivingEntity living = (LivingEntity) other;
    living.doDamage(new EntityDamageSource(this, this.damage));
  }
  
  @Override
  public void render(Graphics2D g, float deltaTime) {
    FloatRectangle renderBox = EntityHelper.calculateRenderBox(this, ArrowEntity.RENDER_SIZE);
    AffineTransform transform = EntityHelper.calculateCameraTransform(this);
    transform.translate(-ArrowEntity.RENDER_SIZE.x() * 0.5f, -ArrowEntity.RENDER_SIZE.x() * 0.5f);
    transform.scale(
      renderBox.getSize().x() / (float) ArrowEntity.ARROW_TEXTURE.getWidth(null),
      renderBox.getSize().y() / (float) ArrowEntity.ARROW_TEXTURE.getHeight(null)
    );
    
    g.drawImage(ArrowEntity.ARROW_TEXTURE, transform, null);
  }

  @Override
  public boolean isVisible(Camera cam) {
    return true;
  }
}
