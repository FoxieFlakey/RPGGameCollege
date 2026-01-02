package foxie.rpg_college.entity;

import java.util.Optional;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.controller.Controller;
import foxie.rpg_college.entity.controller.LivingEntityController;
import foxie.rpg_college.tile.LavaTile;
import foxie.rpg_college.tile.Tile;

public abstract class LivingEntity extends Entity {
  private float healthPoint = 0.0f;

  // When timer hits 0, a burn happens
  // from fire source like lava or fire
  private float burnTimer = -1.0f;

  private float flashDuration = -1.0f;
  private float flashPeriod = -1.0f;
  private boolean flashState = false;
  
  private float timeToDie = 5.0f;

  public static final float FLASH_DURATION = 1.2f;
  public static final float FLASH_PERIOD = 0.1f;

  public boolean canBurn() {
    return burnTimer <= 0.0f;
  }

  public boolean isDead() {
    return this.healthPoint <= 0.0f;
  }

  public float getHealth() {
    if (this.healthPoint <= 0.0f) {
      return 0.0f;
    }

    return this.healthPoint;
  }

  public void setHealth(float health) {
    this.healthPoint = health;
    
    if (this.healthPoint <= 0.0f) {
      this.healthPoint = -1.0f;
    }
  }

  public void doDamage(float damage) {
    if (damage < 0.0f) {
      damage = 0.0f;
    }

    this.setHealth(this.healthPoint - damage);
    this.flash();
  }

  public void flash() {
    this.flashDuration = LivingEntity.FLASH_DURATION;
    this.flashPeriod = LivingEntity.FLASH_PERIOD;
    this.flashState = true;
  }
  
  public void resetFlash() {
    this.flashState = false;
    this.flashDuration = -1.0f;
    this.flashPeriod = -1.0f;
  }

  public boolean isFlashing() {
    return this.flashDuration > 0.0f;
  }

  // This one should be used in render code
  // to either render unflashed (which is false)
  // or flashed (very bright version, which is true)
  public boolean getFlashState() {
    if (!this.isFlashing()) {
      return false;
    }
    
    return this.flashState;
  }
  
  @Override
  public void onTileStep(Tile tile, IVec2 tileCoord) {
    // Harm the living entity when stepped on lava
    if (tile == this.getWorld().getGame().TILES.LAVA_TILE) {
      if (this.canBurn()) {
        this.burnTimer = LavaTile.BURN_PERIOD;
        this.doDamage(LavaTile.DAMAGE);
      }
    }
  }
  
  @Override
  public final Optional<FloatRectangle> getBoxToBeCheckedForTileStep() {
    return Optional.of(this.getLegBox());
  }

  @Override
  public void tick(float deltaTime) {
    burnTimer -= deltaTime;
    if (burnTimer < 0.0f) {
      this.burnTimer = -1.0f;
    }

    if (this.isFlashing()) {
      this.flashPeriod -= deltaTime;
      this.flashDuration -= deltaTime;

      if (this.flashPeriod < 0.0f) {
        this.flashPeriod = LivingEntity.FLASH_PERIOD;
        this.flashState = !this.flashState;
      }
    }
    
    if (this.isDead()) {
      this.timeToDie -= deltaTime;
      
      if (this.timeToDie < 0.0f) {
        this.die();
      }
    }
  }

  public Vec2 getLegPos() {
    return this.getLegBox().getCenter();
  }
  
  public void die() {
    this.getWorld().removeEntity(this);
    if (this.canDispatchControllerEvents()) {
      this.getController().get().dispatchOnEntityNoLongerControllable();
    }
  }
  
  @Override
  public void onEntityCollision(Entity other) {
  }
  
  @Override
  public void onTileCollision(IVec2 coord, Tile other) {
  }
  
  @Override
  public boolean canBeControlled() {
    return true;
  }
  
  @Override
  protected Controller createController() {
    return new LivingEntityController(this);
  }
  
  public abstract FloatRectangle getLegBox();
  public abstract float getMaxHealth();
  public abstract float getMovementSpeed();
}

