package foxie.rpg_college.entity;

import java.util.Optional;

import foxie.rpg_college.Vec2;
import foxie.rpg_college.tile.Lava;
import foxie.rpg_college.tile.Tile;

public abstract class LivingEntity extends Entity {
  private float healthPoint = 0.0f;

  // When timer hits 0, a burn happens
  // from fire source like lava or fire
  private float burnTimer = -1.0f;

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
  }

  @Override
  public void tick(float deltaTime) {
    burnTimer -= deltaTime;
    if (burnTimer < 0.0f) {
      this.burnTimer = -1.0f;
    }

    Optional<Tile> maybeTile = this.getWorld().getTileAt(EntityHelper.fromWorldCoordToTileCoord(this.getLegPos()));
    if (maybeTile.isPresent()) {
      Tile tile = maybeTile.get();
      
      // Harm the living entity when stepped on lava
      if (tile == this.getWorld().getGame().TILES.LAVA_TILE) {
        if (this.canBurn()) {
          this.burnTimer = Lava.BURN_PERIOD;
          this.doDamage(Lava.DAMAGE);
        }
      }
    }
  }

  public abstract Vec2 getLegPos();
  public abstract float getMaxHealth();
}

