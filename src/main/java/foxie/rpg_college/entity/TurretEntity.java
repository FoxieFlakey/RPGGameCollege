package foxie.rpg_college.entity;

import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.texture.Texture;
import foxie.rpg_college.tile.Tile;

public class TurretEntity extends LivingEntity implements Attackable {
  private static final Vec2 SIZE = Tile.SIZE;
  private static final float COOLDOWN = 0.5f;
  
  // Try look for new target every 1 second
  private static final float POLL_TIME = 1.0f;
  private static final float LOOKUP_RADIUS = 1000.0f;
  private static final float ENGAGE_DISTANCE = 800.0f;
  
  private final Texture turretDead;
  private final Texture turretReady;
  private final Texture turretNotReady;
  private final CollisionBox collisionBox = new CollisionBox(new Vec2(0.05f), TurretEntity.SIZE, true);
  
  private float cooldown = -1.0f;
  private float pollDelay = -1.0f;
  private Optional<LivingEntity> currentTarget = Optional.empty();
  
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
    if (other instanceof ProjectileEntity && ((ProjectileEntity) other).getShooter() instanceof TurretEntity) {
      // Turret cannot be harmed by projectiles from turrets
      return false;
    }
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
    return this.cooldown < 0.0f && !this.isDead();
  }
  
  @Override
  public void tick(float deltaTime) {
    super.tick(deltaTime);
    if (this.isDead()) {
      return;
    }
    
    this.cooldown -= deltaTime;
    this.pollDelay -= deltaTime;
    if (this.cooldown < 0.0f) {
      this.cooldown = -1.0f;
    }
    
    if (this.pollDelay < 0.0f) {
      this.pollDelay = TurretEntity.POLL_TIME;
      this.tryLookForTarget();
    }
    
    if (this.currentTarget.isPresent() && this.currentTarget.get().getWorld() != this.getWorld()) {
      // Target moved to different world, forget them
      this.currentTarget = Optional.empty();
    }
    
    if (this.currentTarget.isPresent() && this.currentTarget.get().isDead()) {
      this.pollDelay = TurretEntity.POLL_TIME;
      this.currentTarget = Optional.empty();
      this.tryLookForTarget();
    }
    
    if (this.currentTarget.isPresent() && !this.currentTarget.get().isDead()) {
      // Target still alive try aim to them
      LivingEntity target = this.currentTarget.get();
      this.setRotation(target.getPos().sub(this.getPos()).calculateAngle());
    }
    
    if (this.canAttack() && this.currentTarget.isPresent()) {
      LivingEntity target = this.currentTarget.get();
      if (EntityHelper.distanceBetween(this, target) <= TurretEntity.ENGAGE_DISTANCE) {
        // Target within enganging distance, attack
        this.attack();
      }
    }
  }
  
  private void tryLookForTarget() {
    // Looking for new target
    Iterator<LivingEntity> potentialTargets = this.getWorld()
      .findEntities(this.getPos(), TurretEntity.LOOKUP_RADIUS)
      .filter(e -> e != this)
      .filter(e -> !(e instanceof TurretEntity))
      .filter(e -> e instanceof LivingEntity)
      .map(e -> (LivingEntity) e)
      .filter(e -> !e.isDead())
      .iterator();
    
    LivingEntity bestEntity = null;
    float bestDistance = Float.POSITIVE_INFINITY;
    
    while (potentialTargets.hasNext()) {
      LivingEntity candidate = potentialTargets.next();
      float candidateDistance = EntityHelper.distanceBetween(this, candidate);
      
      if (candidateDistance < bestDistance) {
        bestEntity = candidate;
        bestDistance = candidateDistance;
      }
    }
    
    if (bestEntity != null) {
      // We have found new target
      this.currentTarget = Optional.of(bestEntity);
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
