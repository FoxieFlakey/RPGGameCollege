package foxie.rpg_college.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.controller.Controllable;
import foxie.rpg_college.entity.controller.Controller;
import foxie.rpg_college.entity.controller.EntityController;
import foxie.rpg_college.entity.controller.LivingEntityController;
import foxie.rpg_college.world.World;

public class PlayerEntity extends LivingEntity implements Controllable {
  private static final Vec2 SIZE = new Vec2(
    50.0f,
    100.0f
  );

  private final CollisionBox collisionBox = new CollisionBox(10.0f, new Vec2(0.0f, 0.0f), PlayerEntity.SIZE);
  
  private EntityController controller = null;

  public PlayerEntity() {
    this.setHealth(this.getMaxHealth());
  }

  @Override
  public void setPos(Vec2 pos) {
    super.setPos(pos);
    if (this.controller != null) {
      this.controller.onPositionUpdated();
    }
  }

  @Override
  public float getMaxHealth() {
    return 100.0f;
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
    FloatRectangle renderBox = EntityHelper.calculateRenderBox(this, PlayerEntity.SIZE);

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
  public void setWorld(World world) {
    super.setWorld(world);
    
    if (this.controller != null) {
      this.controller.onWorldChange();
    }
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
        bottomRightCollision.y() - PlayerEntity.SIZE.y() * 0.5f
      ),
      bottomRightCollision
    );
  }
  
  @Override
  public void die() {
    // Player cannot die
  }
  
  @Override
  public Controller getController() {
    PlayerEntity player = this;
    if (this.controller == null) {
      this.controller = new LivingEntityController(player);
    }
    return this.controller;
  }
  
  @Override
  public float getMovementSpeed() {
    return 100.0f;
  }
}
