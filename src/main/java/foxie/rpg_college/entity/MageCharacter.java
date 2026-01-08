package foxie.rpg_college.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;

public class MageCharacter extends CharacterEntity {
  private static final Vec2 SIZE = new Vec2(
    140.0f,
    200.0f
  );
  private final CollisionBox collisionBox = new CollisionBox(10.0f, new Vec2(0.0f, 0.0f), MageCharacter.SIZE);
  
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
    g.setColor(Color.ORANGE);
    g.fillRect(
      (int) renderBox.getTopLeftCorner().x(),
      (int) renderBox.getTopLeftCorner().y(),
      (int) renderBox.getSize().x(),
      (int) renderBox.getSize().y()
    );
  }
}
