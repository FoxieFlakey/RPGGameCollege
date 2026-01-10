package foxie.rpg_college.entity.damage;

// Kelas abstrak damage source, menjelaskan asal usul
// sebuah damage. Kelas dasar ini isinya hanya menentukan
// damage berapa. Subclassnya dapat menambahkan informasi
// tambahkan seperti EntityDamageSource, menambahkan
// damage yang berasal dari entity spesifik. Sedangkan
// TileDamageSource menjelaskan damagenya berasal dari
// tile contohnya lava. Ini memungkinkan kode-kode seperti
// implementasi Defenseable memeriksa apakah karakter
// dapat bertahan dari contohnya hanya dapat bertahan dari
// arrows. Tetapi 100% gagal bertahan terhadap fireball
// dan sumber damage lain
public abstract class DamageSource {
  private float damagePoint;
  
  public DamageSource(float damagePoint) {
    this.setDamagePoint(damagePoint);
  }
  
  public void setDamagePoint(float newDamage) {
    this.damagePoint = Math.max(0.0f, newDamage);
  }
  
  // Method ini memeriksa apakah damage sourcenya
  // 100% dicancel atau damage nya adalah 0.0f
  public boolean isCanceled() {
    return Math.signum(this.getDamagePoint()) < 1.0f && Math.signum(this.getDamagePoint()) > -1.0f;
  }
  
  public float getDamagePoint() {
    return this.damagePoint;
  }
  
  // Mereturn string yang mendeskripsikan
  // sumber dari damagenya dapat digunakan
  // dalam UI death screen. Untuk menulis
  // alasan kematian
  public abstract String getName();
}

