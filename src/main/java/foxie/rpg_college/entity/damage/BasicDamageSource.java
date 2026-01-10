package foxie.rpg_college.entity.damage;

// Subclass dari DamageSource yang menyatakan
// damage tetapi tidak tentu asal usulnya
//
// Kelas ini basic saja, sesuai namanya memungkinkan
// pengguna menset damage sama nama, itu saja
public class BasicDamageSource extends DamageSource {
  private final String name;
  
  public BasicDamageSource(String name, float damagePoint) {
    super(damagePoint);
    this.name = name;
  }
  
  @Override
  public String getName() {
    return this.name;
  }
}
