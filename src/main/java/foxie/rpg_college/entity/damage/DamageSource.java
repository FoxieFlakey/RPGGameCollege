package foxie.rpg_college.entity.damage;

public abstract class DamageSource {
  private float damagePoint;
  
  public DamageSource(float damagePoint) {
    this.setDamagePoint(damagePoint);
  }
  
  public void setDamagePoint(float newDamage) {
    this.damagePoint = Math.max(0.0f, newDamage);
  }
  
  public boolean isCanceled() {
    return Math.signum(this.getDamagePoint()) < 1.0f && Math.signum(this.getDamagePoint()) > -1.0f;
  }
  
  public float getDamagePoint() {
    return this.damagePoint;
  }
  
  public abstract String getName();
}

