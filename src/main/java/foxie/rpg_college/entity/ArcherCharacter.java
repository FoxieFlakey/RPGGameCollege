package foxie.rpg_college.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Vec2;

public class ArcherCharacter extends CharacterEntity implements Attackable {
  private static final Vec2 SIZE = new Vec2(
    50.0f,
    100.0f
  );
  private static final float ATTACK_MANA_POINT = 7.0f;

  private final CollisionBox collisionBox = new CollisionBox(10.0f, new Vec2(0.0f, 0.0f), ArcherCharacter.SIZE);
  private float fireArrowCooldown = -1.0f;
  
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
    FloatRectangle renderBox = EntityHelper.calculateRenderBox(this, ArcherCharacter.SIZE);

    int x = (int) renderBox.getTopLeftCorner().x();
    int y = (int) renderBox.getTopLeftCorner().y();
    int width = (int) renderBox.getSize().x();
    int height = (int) renderBox.getSize().y();

    Color color = new Color(0.98f, 0.63f, 0.28f, 1.00f);
    if (this.getFlashState()) {
      color = new Color(1.00f, 0.93f, 0.58f, 1.00f);
    }

    if (this.isDead()) {
      color = new Color(0.68f, 0.33f, 0.00f, 1.00f);
    }

    g.setColor(color);

    g.fillRoundRect(
      x, y,
      width, height,
      5, 5
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
}
