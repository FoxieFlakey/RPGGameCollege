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
import foxie.rpg_college.tile.Tile;
import foxie.rpg_college.world.World;

public class CatEntity extends LivingEntity implements Controllable {
  private static final Vec2 SIZE = new Vec2(Tile.SIZE.x() * 0.7f, Tile.SIZE.x() * 0.7f);
  private final CollisionBox collisionBox = new CollisionBox(1.0f, new Vec2(0.0f, 0.0f), CatEntity.SIZE);

  private EntityController controller = null;
  
  public CatEntity() {
    this.setHealth(this.getMaxHealth());
  }

  @Override
  public float getMaxHealth() {
    return 20.0f;
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
  public void render(Graphics2D g, float deltaTime) {
    FloatRectangle renderBox = EntityHelper.calculateRenderBox(this, CatEntity.SIZE);

    int x = (int) renderBox.getTopLeftCorner().x();
    int y = (int) renderBox.getTopLeftCorner().y();
    int width = (int) renderBox.getSize().x();
    int height = (int) renderBox.getSize().y();

    Color color = new Color(0.00f, 0.00f, 0.60f, 1.00f);
    if (this.getFlashState()) {
      color = new Color(0.00f, 0.00f, 0.90f, 1.00f);
    }

    if (this.isDead()) {
      color = new Color(0.00f, 0.00f, 0.30f, 1.00f);
    }

    g.setColor(color);
    g.fillRoundRect(
      x, y,
      width, height,
      5, 5
    );
  }

  @Override
  public FloatRectangle getLegBox() {
    return this.collisionBox.asRect();
  }
  
  @Override
  public Vec2 getLegPos() {
    return new Vec2(
      this.getPos().x(),
      this.getPos().y() - CatEntity.SIZE.y() * 0.5f
    );
  }
  
  @Override
  public float getMovementSpeed() {
    return 200.0f;
  }
  
  @Override
  public Controller getController() {
    if (this.controller == null) {
      this.controller = new LivingEntityController(this);
    }
    return this.controller;
  }
  
  @Override
  public void setPos(Vec2 pos) {
    super.setPos(pos);
    if (this.controller != null) {
      this.controller.onPositionUpdated();
    }
  }
  
  @Override
  public void setWorld(World world) {
    super.setWorld(world);
    if (this.controller != null) {
      this.controller.onWorldChange();
    }
  }
  
  @Override
  public void die() {
    super.die();
    if (this.controller != null) {
      this.controller.onEntityNoLongerControllable();
    }
  }
}
