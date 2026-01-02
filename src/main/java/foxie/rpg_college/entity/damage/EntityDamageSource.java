package foxie.rpg_college.entity.damage;

import foxie.rpg_college.entity.Entity;

public class EntityDamageSource extends DamageSource {
  private final Entity source;
  
  public EntityDamageSource(Entity source, float damagePoint) {
    super(damagePoint);
    this.source = source;
  }
  
  @Override
  public String getName() {
    return this.source.getName();
  }
}
