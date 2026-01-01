package foxie.rpg_college.entity.controller;

import foxie.rpg_college.entity.LivingEntity;

public class LivingEntityController extends SimpleEntityController {
  public LivingEntityController(LivingEntity owner) {
		super(owner);
	}
  
  public LivingEntity getLivingEntity() {
    return (LivingEntity) this.getEntity();
  }

	@Override
  public float getMovementSpeed() {
    return this.getLivingEntity().getMovementSpeed();
  }
  
  @Override
  public boolean shouldControlDisabled() {
    return this.getLivingEntity().isDead();
  }
}
