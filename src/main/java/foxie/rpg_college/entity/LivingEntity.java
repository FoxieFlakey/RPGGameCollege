package foxie.rpg_college.entity;

public abstract class LivingEntity extends Entity {
  private float healthPoint;

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
}

