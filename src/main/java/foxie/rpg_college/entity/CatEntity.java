package foxie.rpg_college.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.tile.Tile;

public class CatEntity extends LivingEntity implements Attackable {
  private static final Vec2 SIZE = new Vec2(Tile.SIZE.x() * 0.7f, Tile.SIZE.x() * 0.7f);
  private static final float SWORD_DAMAGE = 16.0f;
  
  private final CollisionBox collisionBox = new CollisionBox(1.0f, new Vec2(0.0f, 0.0f), CatEntity.SIZE);
  
  private Optional<SwordEntity> sword = Optional.empty();
  
  public CatEntity(Game game) {
    super(game);
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
    super.render(g, deltaTime);
    
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
    
    if (this.sword.isPresent() && !this.sword.get().isDoneSwinging()) {
      this.sword.get().renderSword(g, deltaTime);
    }
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
  public Optional<FloatRectangle> getRenderBound() {
    return Optional.of(EntityHelper.calculateRenderBox(this, CatEntity.SIZE));
  }

  @Override
  public boolean canAttack() {
    return this.sword.isEmpty() || this.sword.get().isDoneSwinging();
  }

  @Override
  public boolean attack() {
    if (!this.canAttack()) {
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
    
    SwordEntity sword = new SwordEntity(this.getGame(), this, CatEntity.SWORD_DAMAGE, this.getRotation() - 80.0f, this.getRotation() + 80.0f, isClockwise);
    this.getWorld().addEntity(sword);
    sword.updatePos();
    
    this.sword = Optional.of(sword);
    return true;
  }
}
