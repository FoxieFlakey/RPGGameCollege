package foxie.rpg_college.entity;

public abstract class CharacterEntity extends LivingEntity {
  private float manaPoint;
  private float timeUntilManaRefill = -1.0f;
  
  public CharacterEntity() {
    this.manaPoint = this.getMaxManaPoint();
  }
  
  public float getManaPoint() {
    return this.manaPoint;
  }
  
  public void setManaPoint(float newPoint) {
    this.manaPoint = Math.max(0.0f, newPoint);
  }
  
  public boolean consumeManaPoint(float val) {
    if (this.manaPoint < val) {
      return false;
    }
    
    this.setManaPoint(this.manaPoint - val);
    
    if (this.timeUntilManaRefill < 0.0f) {
      this.timeUntilManaRefill = this.getManaRefillPeriod();
    }
    return true;
  }
  
  @Override
  public void tick(float deltaTime) {
    super.tick(deltaTime);
    
    if (this.getManaPoint() < this.getMaxManaPoint()) {
      this.timeUntilManaRefill -= deltaTime;
      if (this.timeUntilManaRefill < 0.0f) {
        this.timeUntilManaRefill = this.getManaRefillPeriod();
        this.setManaPoint(Math.min(this.getMaxManaPoint(), this.manaPoint + this.getManaRefillRate()));
      }
    }
  }
  
  public abstract float getManaRefillPeriod();
  public abstract float getManaRefillRate();
  public abstract float getMaxManaPoint();
}
