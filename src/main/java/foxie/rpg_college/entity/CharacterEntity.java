package foxie.rpg_college.entity;

import java.awt.Color;

import foxie.rpg_college.Bar;

public abstract class CharacterEntity extends LivingEntity {
  private float manaPoint;
  private Bar manaBar;
  private float timeUntilManaRefill = -1.0f;
  
  public CharacterEntity() {
    this.manaPoint = this.getMaxManaPoint();
    this.manaBar = new Bar(
      0.0f,
      this.manaPoint,
      this.getMaxManaPoint(),
      new Color(0.0f, 0.0f, 0.9f, 1.0f),
      new Color(0.7f, 0.4f, 0.4f, 1.0f)
    );
    this.addBar(this.manaBar);
  }
  
  public float getManaPoint() {
    return this.manaPoint;
  }
  
  public void setManaPoint(float newPoint) {
    this.manaPoint = Math.max(0.0f, newPoint);
    this.manaBar.val = this.manaPoint;
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
    
    if (this.getManaPoint() < this.getMaxManaPoint() && !this.isDead()) {
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
