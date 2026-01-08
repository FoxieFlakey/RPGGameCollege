package foxie.rpg_college.entity.controller;

import foxie.rpg_college.entity.ProjectileEntity;

public class ProjectileEntityController extends EntityController {
  public ProjectileEntityController(ProjectileEntity owner) {
    super(owner);
  }
  
  public ProjectileEntity getProjectileEntity() {
    return (ProjectileEntity) this.getEntity();
  }

  @Override
  public boolean shouldControlDisabled() {
    return this.getProjectileEntity().hasProjectileHitSomething();
  }

  @Override
  public float getMovementSpeed() {
    return this.getProjectileEntity().getSpeed();
  }
}
