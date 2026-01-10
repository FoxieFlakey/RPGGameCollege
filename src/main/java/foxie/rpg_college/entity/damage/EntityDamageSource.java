package foxie.rpg_college.entity.damage;

import foxie.rpg_college.entity.Entity;

// Bentuk damage lain yang berasal dari sebuah entity lain
// contohnya seperti damage dari archer/mage/etc ataupun dari
// turret atau karakter apa saja asal ada Entity nya yang
// menjadi source. Jika tanpa source, pengguna seharusnya
// pakai BasicDamageSource kelas bukan kelas ini
public class EntityDamageSource extends DamageSource {
  private final Entity source;
  
  public EntityDamageSource(Entity source, float damagePoint) {
    super(damagePoint);
    this.source = source;
  }
  
  public Entity getSource() {
    return this.source;
  }
  
  // Nama dari entity yang menyerang dapat
  // diambil melalui method yang tersedia di
  // semua entity
  @Override
  public String getName() {
    return this.source.getName();
  }
}
