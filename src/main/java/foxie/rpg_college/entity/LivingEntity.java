package foxie.rpg_college.entity;

public abstract class LivingEntity extends Entity {
  private float healthPoint;

  public boolean isDead() {
    return this.healthPoint < 0.0f;
  }

  public void doDamage(float damage) {
    if (damage < 0.0f) {
      damage = 0.0f;
    }

    this.healthPoint -= damage;

    if (this.healthPoint < 0.0) {
      this.healthPoint = -1.0f;
    }
  }
}

