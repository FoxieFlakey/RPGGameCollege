package foxie.rpg_college.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;

public class WarriorCharacter extends HeroCharacter implements Attackable {
  private static final Vec2 SIZE = new Vec2(
    175.0f,
    250.0f
  );
  private static final float SWORD_DAMAGE = 20.0f;
  private static final float SWORD_MANA_POINT = 20.0f;
  
  private final CollisionBox collisionBox = new CollisionBox(1.0f, new Vec2(0.0f, 0.0f), WarriorCharacter.SIZE);
  private Optional<SwordEntity> sword = Optional.empty();
  
  public WarriorCharacter(Game game) {
    super(game);
  }

  @Override
  public float getManaRefillPeriod() {
    return 2.0f;
  }

  @Override
  public float getManaRefillRate() {
    return 40.0f;
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
        bottomRightCollision.y() - WarriorCharacter.SIZE.y() * 0.5f
      ),
      bottomRightCollision
    );
  }

  @Override
  public float getMaxHealth() {
    return 150.0f;
  }

  @Override
  public float getMovementSpeed() {
    return 70.0f;
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
    return Optional.of(EntityHelper.calculateRenderBox(this, WarriorCharacter.SIZE));
  }
  
  @Override
  public void render(Graphics2D g, float deltaTime) {
    super.render(g, deltaTime);
    
    FloatRectangle renderBox = this.getRenderBound().get();
    Color color = new Color(0.8f, 0.4f, 0.8f);
    
    if (this.getFlashState()) {
      color = new Color(1.0f, 0.8f, 1.0f);
    }
    
    if (this.isDead()) {
      color = new Color(0.4f, 0.2f, 0.4f);
    }
    
    g.setColor(color);
    g.fillRect(
      (int) renderBox.getTopLeftCorner().x(),
      (int) renderBox.getTopLeftCorner().y(),
      (int) renderBox.getSize().x(),
      (int) renderBox.getSize().y()
    );
    
    if (this.sword.isPresent() && !this.sword.get().isDoneSwinging()) {
      this.sword.get().renderSword(g, deltaTime);
    }
  }
  
  @Override
  public boolean canAttack() {
    return (this.sword.isEmpty() || this.sword.get().isDoneSwinging()) &&
      this.getManaPoint() >= WarriorCharacter.SWORD_MANA_POINT;
  }

  @Override
  public boolean attack() {
    if (!this.canAttack()) {
      return false;
    }
    
    if (!this.consumeManaPoint(WarriorCharacter.SWORD_MANA_POINT)) {
      return false;
    }
    
    boolean isClockwise;
    
    switch (this.getOrientation()) {
      case Right:
      case Down:
      case Up:
      default:
        isClockwise = true;
        break;
      case Left:
        isClockwise = false;
        break;
    }
    
    SwordEntity sword = new SwordEntity(
      this.getGame(),
      this, WarriorCharacter.SWORD_DAMAGE,
      this.getRotation() - 80.0f,
      this.getRotation() + 80.0f,
      isClockwise,
      Vec2.unitVectorOfAngle(this.getRotation())
        .mul(
          Float.min(
            WarriorCharacter.SIZE.x(),
            WarriorCharacter.SIZE.y()
          ) / 2.0f - 25.0f
        )
    );
    this.getWorld().addEntity(sword);
    sword.updatePos();
    
    this.sword = Optional.of(sword);
    return true;
  }
}
