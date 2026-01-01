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
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Util;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.tile.Tile;

public class Arrow extends Entity {
  private static final Vec2 SIZE = new Vec2(32.0f, 32.0f);
  
  private Optional<CollisionBox> collisionBox = Optional.of(new CollisionBox(0.1f, new Vec2(0.0f, 0.0f), Arrow.SIZE));
  private float damage = 5.0f;
  
  private final Entity shooter;
  private float velocity = 400.0f;
  private float timeToLive = 1.0f;
  
  private static final URL ARROW_URL = Util.getResource("/arrow.png");
  private static Image ARROW_TEXTURE;
  
  static {
    try {
      ARROW_TEXTURE = ImageIO.read(Arrow.ARROW_URL.openStream());
    } catch (IOException e) {
      throw new RuntimeException("Error loading arrow texture", e);
    }
  }
  
  public Arrow(Entity shooter) {
    this.shooter = shooter;
  }
  
  @Override
  public boolean canCollideWith(Entity other) {
    if (other instanceof Arrow) {
      // Arrow dont collide with arrow
      return false;
    }
    
    if (other == shooter) {
      // Do not damage the shoter
      return false;
    }
    
    if (other instanceof LivingEntity) {
      LivingEntity living = (LivingEntity) other;
      if (living.isDead()) {
        // Dont collide with dead entity
        return false;
      }
      return true;
    }
    
    // Everything else arrow does not affect
    return false;
  }

  @Override
  public Optional<CollisionBox> getCollisionBox() {
    return this.collisionBox;
  }

  @Override
  public boolean isVisible(Camera cam) {
    return true;
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
    FloatRectangle renderBox = EntityHelper.calculateRenderBox(this, Arrow.SIZE);
    AffineTransform transform = EntityHelper.calculateCameraTransform(this);
    transform.scale(
      renderBox.getSize().x() / (float) Arrow.ARROW_TEXTURE.getWidth(null),
      renderBox.getSize().y() / (float) Arrow.ARROW_TEXTURE.getHeight(null)
    );
    transform.translate(-Arrow.SIZE.x() * 0.25f, -Arrow.SIZE.y() * 0.5f);
    
    g.drawImage(Arrow.ARROW_TEXTURE, transform, null);
  }

  @Override
  public void tick(float deltaTime) {
    this.timeToLive -= deltaTime;
    
    Vec2 velocity = Vec2.unitVectorOfAngle(this.getRotation()).mul(this.velocity * deltaTime);
    this.setPos(this.getPos().add(velocity));
    
    if (this.timeToLive < 0.0f) {
      this.die();
    }
  }
  
  void die() {
    this.getWorld().removeEntity(this);
  }

  @Override
  public void onTileStep(Tile tile, IVec2 tileCoord) {
  }

  @Override
  public Optional<FloatRectangle> getBoxToBeCheckedForTileStep() {
    return Optional.empty();
  }
  
  @Override
  public void onCollision() {
    super.onCollision();
    
    // Arrow collided a target
    this.collisionBox = Optional.empty();
    this.velocity = 0.0f;
  }
  
  @Override
  public void onEntityCollision(Entity other) {
    LivingEntity living = (LivingEntity) other;
    living.doDamage(this.damage);
    this.die();
  }
  
  @Override
  public void onTileCollision(IVec2 coord, Tile other) {
  }
}
