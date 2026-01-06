package foxie.rpg_college.entity;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import javax.imageio.ImageIO;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Util;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.damage.DamageSource;
import foxie.rpg_college.entity.damage.EntityDamageSource;

public class ArcherCharacter extends CharacterEntity implements Attackable, Defenseable {
  private static final Vec2 SIZE = new Vec2(
    140.0f,
    200.0f
  );
  private static final float ATTACK_MANA_POINT = 7.0f;
  
  private static final float MINIMAL_MANA_POINT_TO_DODGE = 70.0f;
  private static final float MINIMAL_REDUCED_ARROW_DAMAGE_MANA_POINT = 40.0f;
  private static final float DODGE_MANA_POINT = 15.0f;
  private static final float REDUCE_ARROW_DAMAGE_MANA_POINT = 12.0f;

  private final CollisionBox collisionBox = new CollisionBox(10.0f, new Vec2(0.0f, 0.0f), ArcherCharacter.SIZE);
  private float fireArrowCooldown = -1.0f;
  
  private static final URL ARCHER_ORIENT_DOWN_TEXTURE_URL = Util.getResource("/archer_facing_down.png");
  private static final URL ARCHER_DEAD_TEXTURE_URL = Util.getResource("/archer_dead.png");
  
  private static BufferedImage ARCHER_ORIENT_DOWN_TEXTURE;
  private static BufferedImage ARCHER_ORIENT_DOWN_TEXTURE_FLASHED;
  
  private static BufferedImage ARCHER_DEAD_TEXTURE;
  
  static {
    try {
      ARCHER_ORIENT_DOWN_TEXTURE = ImageIO.read(ArcherCharacter.ARCHER_ORIENT_DOWN_TEXTURE_URL.openStream());
      ARCHER_DEAD_TEXTURE = ImageIO.read(ArcherCharacter.ARCHER_DEAD_TEXTURE_URL.openStream());
      
      ARCHER_ORIENT_DOWN_TEXTURE_FLASHED = new RescaleOp(new float[] {2.0f, 1.3f, 1.3f, 1.0f}, new float[4], null)
        .filter(ARCHER_ORIENT_DOWN_TEXTURE, null);
    } catch (IOException e) {
      throw new RuntimeException("Error loading arrow texture", e);
    }
  }
  
  @Override
  public float getMaxHealth() {
    return 100.0f;
  }
  
  @Override
  public float getMaxManaPoint() {
    return 100.0f;
  }
  
  @Override
  public float getManaRefillRate() {
    return 40.0f;
  }
  
  @Override
  public float getManaRefillPeriod() {
    return 2.0f;
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
    Image texture = ArcherCharacter.ARCHER_ORIENT_DOWN_TEXTURE;
    
    if (this.getFlashState()) {
      texture = ArcherCharacter.ARCHER_ORIENT_DOWN_TEXTURE_FLASHED;
    }
    
    if (this.isDead()) {
      texture = ArcherCharacter.ARCHER_DEAD_TEXTURE;
    }
    
    FloatRectangle renderBox = EntityHelper.calculateRenderBox(this, ArcherCharacter.SIZE);

    int x = (int) renderBox.getTopLeftCorner().x();
    int y = (int) renderBox.getTopLeftCorner().y();
    int width = (int) renderBox.getSize().x();
    int height = (int) renderBox.getSize().y();
    
    g.drawImage(
      texture,
      x,
      y,
      x + width,
      y + height,
      0,
      0,
      ArcherCharacter.ARCHER_ORIENT_DOWN_TEXTURE.getWidth(),
      ArcherCharacter.ARCHER_ORIENT_DOWN_TEXTURE.getHeight(),
      null
    );
  }

  @Override
  public boolean isVisible(Camera cam) {
    // Player is always visible
    return true;
  }

  @Override
  public Optional<CollisionBox> getCollisionBox() {
    return Optional.of(this.collisionBox);
  }

  @Override
  public boolean canCollideWith(Entity other) {
    return true;
  }

  @Override
  public FloatRectangle getLegBox() {
    FloatRectangle collision = this.collisionBox.asRect();
    Vec2 topLeftCollision = collision.getTopLeftCorner();
    Vec2 bottomRightCollision = collision.getBottomRightCorner();
    
    return new FloatRectangle(
      new Vec2(
        topLeftCollision.x(),
        bottomRightCollision.y() - ArcherCharacter.SIZE.y() * 0.5f
      ),
      bottomRightCollision
    );
  }
  
  @Override
  public float getMovementSpeed() {
    return 100.0f;
  }
  
  @Override
  public void tick(float deltaTime) {
    super.tick(deltaTime);
    this.fireArrowCooldown -= deltaTime;
    if (this.fireArrowCooldown < 0.0f) {
      this.fireArrowCooldown = -1.0f;
    }
  }
  
  @Override
  public boolean attack() {
    if (!this.canAttack()) {
      return false;
    }
    
    if (!this.consumeManaPoint(ArcherCharacter.ATTACK_MANA_POINT)) {
      return false;
    }
    
    this.fireArrowCooldown = 0.1f;
    
    // Spawn arrow
    ArrowEntity arrow = new ArrowEntity(this);
    this.getWorld().addEntity(arrow);
    arrow.setPos(this.getPos());
    arrow.setRotation(this.getRotation());
    
    return true;
  }

  @Override
  public boolean canAttack() {
    return this.fireArrowCooldown < 0.0f && this.getManaPoint() >= ArcherCharacter.ATTACK_MANA_POINT;
  }
  
  @Override
  public boolean canDefense() {
    return
      this.getManaPoint() >= ArcherCharacter.MINIMAL_MANA_POINT_TO_DODGE ||
      this.getManaPoint() >= ArcherCharacter.MINIMAL_REDUCED_ARROW_DAMAGE_MANA_POINT;
  }
  
  @Override
  public void defend(DamageSource source) {
    if (source instanceof EntityDamageSource) {
      EntityDamageSource entitySource = (EntityDamageSource) source;
      if (entitySource.getSource() instanceof ArrowEntity) {
        if (this.getManaPoint() >= ArcherCharacter.MINIMAL_MANA_POINT_TO_DODGE) {
          if (this.consumeManaPoint(ArcherCharacter.DODGE_MANA_POINT)) {
            source.setDamagePoint(0.0f);
          }
        } else if (this.getManaPoint() >= ArcherCharacter.MINIMAL_REDUCED_ARROW_DAMAGE_MANA_POINT) {
          if (this.consumeManaPoint(ArcherCharacter.REDUCE_ARROW_DAMAGE_MANA_POINT)) {
            // Take 75% less damage from arrows. So only 25% of
            // damage is inflicted
            source.setDamagePoint(source.getDamagePoint() * 0.25f);
          }
        }
      }
    }
  }
}
