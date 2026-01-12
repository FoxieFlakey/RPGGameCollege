package foxie.rpg_college.entity;

import java.awt.Color;

import foxie.rpg_college.Bar;
import foxie.rpg_college.Game;
import foxie.rpg_college.Util;

// CharacterEntity adalah makhluk-makhluk hidup  yang memiliki
// mana
public abstract class CharacterEntity extends LivingEntity {
  private float manaPoint;
  private Bar manaBar;
  private float timeUntilManaRefill = -1.0f;
  
  public CharacterEntity(Game game) {
    super(game);
    this.manaPoint = this.getMaxManaPoint();
    this.manaBar = new Bar(
      0.0f,
      this.manaPoint,
      this.getMaxManaPoint(),
      new Color(0.0f, 0.0f, 0.9f, 1.0f),
      new Color(0.8f, 0.8f, 0.9f, 1.0f)
    );
    this.addBar(this.manaBar);
  }
  
  public float getManaPoint() {
    return this.manaPoint;
  }
  
  public void setManaPoint(float newPoint) {
    // Set value mana dan batasi sehingga tidak
    // melebihi batas dan update bar mana nya
    this.manaPoint = Util.clamp(newPoint, 0.0f, this.getMaxManaPoint());
    this.manaBar.val = this.manaPoint;
  }
  
  // Coba memakai mana sebanyak 'val' jika sukses
  // return true, dan pemanggil dapat melakukan
  // aksi yang memakai mana, kalau gagal
  // return false, jadi jangan melakukan apa-apa
  public boolean consumeManaPoint(float val) {
    if (this.manaPoint < val) {
      return false;
    }
    
    // Kurangi mana, sebelumnya sudah diperiksa kalau tidak menjadi
    // negatif
    this.setManaPoint(this.manaPoint - val);
    
    if (this.timeUntilManaRefill < 0.0f) {
      // Mulai mengisi mana kalau sudah dipakai
      this.timeUntilManaRefill = this.getManaRefillPeriod();
    }
    return true;
  }
  
  @Override
  public void tick(float deltaTime) {
    super.tick(deltaTime);
    
    if (this.getManaPoint() < this.getMaxManaPoint() && !this.isDead()) {
      // Jika masih hidup, coba isi mana nya
      this.timeUntilManaRefill -= deltaTime;
      if (this.timeUntilManaRefill < 0.0f) {
        this.timeUntilManaRefill = this.getManaRefillPeriod();
        // Jika mana melebihi maximum maka set maksimum saja -w`
        this.setManaPoint(Math.min(this.getMaxManaPoint(), this.manaPoint + this.getManaRefillRate()));
      }
    }
    
    this.manaBar.max = this.getMaxManaPoint();
  }
  
  // Tiap karakter mengisi mananya pada kecepatan yang berbeda
  public abstract float getManaRefillPeriod();
  public abstract float getManaRefillRate();
  public abstract float getMaxManaPoint();
}
