package foxie.rpg_college.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;

public class MageCharacter extends CharacterEntity implements Attackable {
  private static final Vec2 SIZE = new Vec2(
    140.0f,
    200.0f
  );
  private static final float ATTACK_COOLDOWN = 1.5f;
  private static final float ATTACK_MANA = 40.0f;
  
  private final CollisionBox collisionBox = new CollisionBox(10.0f, new Vec2(0.0f, 0.0f), MageCharacter.SIZE);
  private float attackCooldown = -1.0f;
  
  public MageCharacter(Game game) {
    super(game);
  }

  @Override
  public float getManaRefillPeriod() {
    return 1.0f;
  }

  @Override
  public float getManaRefillRate() {
    return 50.0f;
  }

  @Override
  public float getMaxManaPoint() {
    return 100.0f;
  }

  @Override
  public FloatRectangle getLegBox() {
    FloatRectangle collision = this.collisionBox.asRect();
    Vec2 topLeftCollision = collision.getTopLeftCorner();
    Vec2 bottomRightCollision = collision.getBottomRightCorner();
    
    return new FloatRectangle(
      new Vec2(
        topLeftCollision.x(),
        bottomRightCollision.y() - MageCharacter.SIZE.y() * 0.5f
      ),
      bottomRightCollision
    );
  }

  @Override
  public float getMaxHealth() {
    return 100.0f;
  }

  @Override
  public float getMovementSpeed() {
    return 100.0f;
  }

  @Override
  public boolean canCollideWith(Entity other) {
    return true;
  }

  @Override
  public Optional<CollisionBox> getCollisionBox() {
    return Optional.of(this.collisionBox);
  }

  @Override
  public boolean isVisible(Camera cam) {
    return true;
  }

  @Override
  public Optional<FloatRectangle> getRenderBound() {
    return Optional.of(EntityHelper.calculateRenderBox(this, MageCharacter.SIZE));
  }
  
  @Override
  public void render(Graphics2D g, float deltaTime) {
    super.render(g, deltaTime);
    
    FloatRectangle renderBox = this.getRenderBound().get();
    Color color = new Color(1.0f, 0.4f, 0.4f);
    
    if (this.getFlashState()) {
      color = new Color(1.0f, 0.7f, 0.7f);
    }
    
    if (this.isDead()) {
      color = new Color(0.5f, 0.1f, 0.1f);
    }
    
    g.setColor(color);
    g.fillRect(
      (int) renderBox.getTopLeftCorner().x(),
      (int) renderBox.getTopLeftCorner().y(),
      (int) renderBox.getSize().x(),
      (int) renderBox.getSize().y()
    );
  }
  
  @Override
  public boolean canAttack() {
    return this.getManaPoint() >= MageCharacter.ATTACK_MANA && this.attackCooldown <= 0.0f && !this.isDead();
  }
  
  @Override
  public void tick(float deltaTime) {
    super.tick(deltaTime);
    this.attackCooldown -= deltaTime;
    
    if (this.attackCooldown < 0.0f) {
      this.attackCooldown = -1.0f;
    }
  }
  
  @Override
  public boolean attack() {
    if (!this.canAttack()) {
      return false;
    }
    
    if (!this.consumeManaPoint(MageCharacter.ATTACK_MANA)) {
      return false;
    }
    
    this.attackCooldown = MageCharacter.ATTACK_COOLDOWN;
    
    FireballEntity fireball = new FireballEntity(this.getGame(), this);
    this.getWorld().addEntity(fireball);
    fireball.setRotation(this.getRotation());
    fireball.setPos(this.getPos());
    
    return true;
  }
}
