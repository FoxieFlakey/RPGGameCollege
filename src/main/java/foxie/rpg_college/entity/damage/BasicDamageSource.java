package foxie.rpg_college.entity.damage;

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
