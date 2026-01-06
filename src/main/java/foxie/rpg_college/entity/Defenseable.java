package foxie.rpg_college.entity;

import foxie.rpg_college.entity.damage.DamageSource;

public interface Defenseable {
  boolean canDefense();
  void defend(DamageSource source);
}
