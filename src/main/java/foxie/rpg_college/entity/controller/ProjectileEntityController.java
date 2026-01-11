package foxie.rpg_college.entity.controller;

import foxie.rpg_college.entity.ProjectileEntity;

// Kelas untuk mengendalikan projectile seperti arrow, dan fireball
// fungsi mengendalikan arrow dan fireball hanya dapat diakses jika
// mode debug diaktifkan, ini hanya tersedia untuk mendebug
public class ProjectileEntityController extends EntityController {
  public ProjectileEntityController(ProjectileEntity owner) {
    super(owner);
  }
  
  // Method convenience untuk mendapatkan entity projectile
  // selalu berhasil karena jika membuat kelas ini selalu
  // memerlukan ProjectileEntity jadi cast ini tidak gagal
  public ProjectileEntity getProjectileEntity() {
    return (ProjectileEntity) this.getEntity();
  }

  // Jika arrow/firebal telah tabrak, pergerakan atau kontrol
  // dimatikan
  @Override
  public boolean shouldControlDisabled() {
    return this.getProjectileEntity().hasProjectileHitSomething();
  }

  // Kecepat pergerakan disamakan dengan kecepatan projectilenya
  // sendiri
  @Override
  public float getMovementSpeed() {
    return this.getProjectileEntity().getSpeed();
  }
}
