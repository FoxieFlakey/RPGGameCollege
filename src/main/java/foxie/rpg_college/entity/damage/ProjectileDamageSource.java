package foxie.rpg_college.entity.damage;

import foxie.rpg_college.entity.ProjectileEntity;

public class ProjectileDamageSource extends EntityDamageSource {
  private final ProjectileEntity projectile;
  
  public ProjectileDamageSource(ProjectileEntity source, float damagePoint) {
    super(source.getShooter(), damagePoint);
    this.projectile = source;
  }
  
  public ProjectileEntity getProjectile() {
    return this.projectile;
  }
}
