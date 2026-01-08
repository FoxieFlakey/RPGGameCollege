package foxie.rpg_college.entity;

import java.util.Optional;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.controller.ProjectileEntityController;
import foxie.rpg_college.entity.controller.Controller;
import foxie.rpg_college.tile.Tile;

public abstract class ProjectileEntity extends Entity {
  private boolean hasProjectileHitSomething = false;
  
  private final Entity shooter;
  private float velocity;
  private float timeToLive;
  
  public ProjectileEntity(Game game, Entity shooter, float timeToLive, float velocity) {
    super(game);
    this.shooter = shooter;
    this.timeToLive = timeToLive;
    this.velocity = velocity;
  }
  
  @Override
  public boolean canCollideWith(Entity other) {
    if (other instanceof ProjectileEntity) {
      // Arrow dont collide with arrow
      return false;
    }
    
    if (other == shooter) {
      // Do not damage the shoter
      return false;
    }
    
    // Everything else arrow does not affect
    return this.canBeHit(other);
  }
  
  @Override
  public void tick(float deltaTime) {
    if (this.isBeingControlled()) {
      return;
    }
    
    this.timeToLive -= deltaTime;
    
    Vec2 velocity = Vec2.unitVectorOfAngle(this.getRotation()).mul(this.velocity * deltaTime);
    this.setPos(this.getPos().add(velocity));
    
    if (this.timeToLive < 0.0f) {
      this.die();
    }
  }
  
  public float getSpeed() {
    return this.velocity;
  }
  
  void die() {
    this.getWorld().removeEntity(this);
    
    if (this.canDispatchControllerEvents()) {
      this.getController().get().dispatchOnEntityNoLongerControllable();
    }
  }
  
  public boolean hasProjectileHitSomething() {
    return this.hasProjectileHitSomething;
  }
  
  public Entity getShooter() {
    return this.shooter;
  }

  @Override
  public Optional<FloatRectangle> getBoxToBeCheckedForTileStep() {
    return Optional.empty();
  }
  
  @Override
  public void onCollision() {
    super.onCollision();
    
    // Arrow collided a target
    this.velocity = 0.0f;
    this.hasProjectileHitSomething = true;
  }
  
  @Override
  public void onEntityCollision(Entity other) {
    if (!this.hasProjectileHitSomething) {
      this.onHit(other);
      this.hasProjectileHitSomething = true;
    }
    this.die();
  }
  
  @Override
  public void onWorldBorderCollision() {
    this.hasProjectileHitSomething = true;
  }
  
  @Override
  public void onTileCollision(IVec2 coord, Tile other) {
    this.hasProjectileHitSomething = true;
  }
  
  @Override
  public void onTileStep(Tile tile, IVec2 tileCoord) {
  }
  
  @Override
  public boolean canBeControlled() {
    return true;
  }
  
  @Override
  protected Controller createController() {
    return new ProjectileEntityController(this);
  }
  
  public abstract boolean canBeHit(Entity other);
  public abstract void onHit(Entity other);
}
