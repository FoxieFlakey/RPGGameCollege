package foxie.rpg_college.entity;

import java.util.Optional;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.tile.Tile;

public abstract class ProjectileEntity extends Entity {
  private boolean hasProjectileHitSomething = false;
  
  private final Entity shooter;
  private float velocity;
  private float timeToLive;
  
  public ProjectileEntity(Entity shooter, float timeToLiveWhenHit, float velocity) {
    this.shooter = shooter;
    this.timeToLive = timeToLiveWhenHit;
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
    this.timeToLive -= deltaTime;
    
    Vec2 velocity = Vec2.unitVectorOfAngle(this.getRotation()).mul(this.velocity * deltaTime);
    this.setPos(this.getPos().add(velocity));
    
    if (this.timeToLive < 0.0f) {
      this.die();
    }
  }
  
  void die() {
    this.getWorld().removeEntity(this);
  }
  
  public boolean hasProjectileHitSomething() {
    return this.hasProjectileHitSomething;
  }

  @Override
  public Optional<FloatRectangle> getBoxToBeCheckedForTileStep() {
    return Optional.empty();
  }
  
  @Override
  public void onCollision() {
    super.onCollision();
    
    // Arrow collided a target
    this.hasProjectileHitSomething = true;
    this.velocity = 0.0f;
  }
  
  @Override
  public void onEntityCollision(Entity other) {
    this.onHit(other);
    this.die();
  }
  
  @Override
  public void onTileCollision(IVec2 coord, Tile other) {
  }
  
  @Override
  public void onTileStep(Tile tile, IVec2 tileCoord) {
  }
  
  public abstract boolean canBeHit(Entity other);
  public abstract void onHit(Entity other);
}
