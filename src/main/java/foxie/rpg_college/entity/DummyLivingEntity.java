package foxie.rpg_college.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;

public class DummyLivingEntity extends LivingEntity {
  private static final Vec2 SIZE = new Vec2(
    140.0f,
    140.0f
  );
  private final CollisionBox collisionBox = new CollisionBox(10.0f, new Vec2(0.0f, 0.0f), DummyLivingEntity.SIZE);
  
  public DummyLivingEntity(Game game) {
    super(game);
  }

  @Override
  public FloatRectangle getLegBox() {
    return this.collisionBox.asRect();
  }

  @Override
  public float getMaxHealth() {
    return 100.0f;
  }

  @Override
  public float getMovementSpeed() {
    return 200.0f;
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
    return Optional.of(EntityHelper.calculateRenderBox(this, DummyLivingEntity.SIZE));
  }
  
  @Override
  public void setHealth(float health) {
    super.setHealth(this.getMaxHealth() * 0.9f);
  }
  
  @Override
  public void render(Graphics2D g, float deltaTime) {
    super.render(g, deltaTime);
    
    FloatRectangle renderBox = this.getRenderBound().get();
    Color color = new Color(0.4f, 1.0f, 0.4f);
    
    if (this.getFlashState()) {
      color = new Color(0.7f, 1.0f, 0.7f);
    }
    
    if (this.isDead()) {
      color = new Color(0.1f, 0.5f, 0.1f);
    }
    
    g.setColor(color);
    g.fillRect(
      (int) renderBox.getTopLeftCorner().x(),
      (int) renderBox.getTopLeftCorner().y(),
      (int) renderBox.getSize().x(),
      (int) renderBox.getSize().y()
    );
  }
}
