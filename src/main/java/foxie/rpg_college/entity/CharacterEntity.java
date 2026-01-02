package foxie.rpg_college.entity;

public abstract class CharacterEntity extends LivingEntity {
  private float manaPoint;
  
  public CharacterEntity() {
    this.manaPoint = this.getMaxManaPoint();
  }
  
  public float getManaPoint() {
    return this.manaPoint;
  }
  
  public void setManaPoint(float newPoint) {
    this.manaPoint = Math.max(0.0f, newPoint);
  }
  
  public abstract float getMaxManaPoint();
}
