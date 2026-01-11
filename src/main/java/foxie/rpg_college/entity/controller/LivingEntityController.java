package foxie.rpg_college.entity.controller;

import foxie.rpg_college.entity.LivingEntity;

// Sebuah subclass dari entity controller untuk
// megisi beberapa method dengan data dari LivingEntity
public class LivingEntityController extends EntityController {
  public LivingEntityController(LivingEntity owner) {
		super(owner);
	}
  
  // Sama seperti di ProjectileEntiyController#getProjectileEntity
  // ini memudahkan mengambil instance sebagai LivingEntity
  public LivingEntity getLivingEntity() {
    return (LivingEntity) this.getEntity();
  }

	@Override
  public float getMovementSpeed() {
    return this.getLivingEntity().getMovementSpeed();
  }
  
  @Override
  public boolean shouldControlDisabled() {
    // LivingEntity tidak dapat dikontrol kalau sudah
    // mati
    return this.getLivingEntity().isDead();
  }
}
