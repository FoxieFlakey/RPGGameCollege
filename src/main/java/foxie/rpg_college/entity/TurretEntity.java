package foxie.rpg_college.entity;

import java.awt.Graphics2D;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.texture.Texture;
import foxie.rpg_college.tile.Tile;

public class TurretEntity extends LivingEntity implements Attackable {
  private static final Vec2 SIZE = Tile.SIZE;
  private static final float COOLDOWN = 0.2f;
  
  private final Texture turretDead;
  private final Texture turretReady;
  private final Texture turretNotReady;
  private final CollisionBox collisionBox = new CollisionBox(new Vec2(0.05f), TurretEntity.SIZE, true);
  
  private float cooldown = -1.0f;
  
  public TurretEntity(Game game) {
    super(game);
    
    this.turretReady = game.getTextureManager().getTexture("entity/turret/ready");
    this.turretDead = game.getTextureManager().getTexture("entity/turret/dead");
    this.turretNotReady = game.getTextureManager().getTexture("entity/turret/not_ready");
  }

  @Override
  public FloatRectangle getLegBox() {
    return collisionBox.asRect();
  }

  @Override
  public float getMaxHealth() {
    return 200.0f;
  }

  @Override
  public float getMovementSpeed() {
    return 0.0f;
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
    return Optional.of(EntityHelper.calculateRenderBox(this, TurretEntity.SIZE));
  }
  
  @Override
  public void render(Graphics2D g, float deltaTime) {
    super.render(g, deltaTime);
    
    Texture texture = this.turretNotReady;
    if (this.canAttack()) {
      texture = this.turretReady;
    }
    
    if (this.isDead()) {
      texture = this.turretDead;
    }
    
    EntityHelper.renderRotated(this, g, texture.image(), TurretEntity.SIZE);
  }

  @Override
  public boolean canAttack() {
    return this.cooldown < 0.0f;
  }
  
  @Override
  public void tick(float deltaTime) {
    super.tick(deltaTime);
    this.cooldown -= deltaTime;
    if (this.cooldown < 0.0f) {
      this.cooldown = -1.0f;
    }
  }

  @Override
  public boolean attack() {
    if (!canAttack()) {
      return false;
    }
    
    this.cooldown = TurretEntity.COOLDOWN;
    ArrowEntity arrow = new ArrowEntity(this.getGame(), this);
    this.getWorld().addEntity(arrow);
    arrow.setPos(this.getPos());
    arrow.setRotation(this.getRotation());
    return true;
  }
}
