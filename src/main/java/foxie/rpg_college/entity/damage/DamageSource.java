package foxie.rpg_college.entity.damage;

public abstract class DamageSource {
  private final float damagePoint;
  
  public DamageSource(float damagePoint) {
    this.damagePoint = damagePoint;
  }
  
  public float getDamagePoint() {
    return this.damagePoint;
  }
  
  public abstract String getName();
}

