package foxie.rpg_college.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

import foxie.rpg_college.Bar;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.controller.Controller;
import foxie.rpg_college.entity.controller.LivingEntityController;
import foxie.rpg_college.entity.damage.DamageSource;
import foxie.rpg_college.tile.Tile;

// LivingEntity adalah entity yang hidup, memiliki HP
// dan memliki beberapa method seperti dapat dibakar
// dapat berkedip dan waktu sebelum remove, seperti mati
public abstract class LivingEntity extends Entity {
  // HP dari entity hidup
  private float healthPoint;
  // Lalu ada bar untuk HP agar bisa diupdae
  private Bar healthBar;
  
  // When timer hits 0, a burn happens
  // from fire source like lava or fire
  private float burnTimer = -1.0f;

  // Waktu berapa lama flash terjadi
  private float flashDuration = -1.0f;
  // Jangka antara kedipan jika sedang flashing
  private float flashPeriod = -1.0f;
  // Apa lagi flash? jika true, entity yang dirender
  // bewarna cerah kalau tidak normal.
  private boolean flashState = false;
  
  private float timeToDie = 5.0f;
  private Optional<DamageSource> deathBy = Optional.empty();

  // Durasi flash
  public static final float FLASH_DURATION = 1.2f;
  // Sama kecepatan flash
  public static final float FLASH_PERIOD = 0.1f;
  
  public LivingEntity(Game game) {
    super(game);
    this.healthPoint = this.getMaxHealth();
    this.healthBar = new Bar(
      0.0f,
      this.healthPoint,
      this.getMaxHealth(),
      new Color(0.9f, 0.0f, 0.0f, 1.0f),
      new Color(0.7f, 0.4f, 0.4f, 1.0f)
    );
    this.addBar(this.healthBar);
  }

  public boolean canBurn() {
    return burnTimer <= 0.0f;
  }

  public boolean isDead() {
    return this.healthPoint <= 0.0f;
  }
  
  public Optional<DamageSource> getDeathReason() {
    return this.deathBy;
  }

  public float getHealth() {
    // Jika HP dibawah 0.0f dianggap mati
    // jadi return 0.0f
    if (this.healthPoint <= 0.0f) {
      return 0.0f;
    }

    return this.healthPoint;
  }

  public void setHealth(float health) {
    // Mengupdate health
    this.healthPoint = health;
    
    // Lalu memeriksa validitas dari health
    if (this.healthPoint <= 0.0f) {
      // Jika negatif, dianggap mati jadi set -1.0f
      this.healthPoint = -1.0f;
    } else if (this.healthPoint >= this.getMaxHealth()) {
      // Jika HP melebihi maximum di set ke maximum
      this.healthPoint = this.getMaxHealth();
    }
    
    // Lalu update health bar nya
    this.healthBar.val = this.getHealth();
    
    if (health > 0.0f) {
      // Jika dihidupkan lagi hapus alasan matinya
      this.deathBy = Optional.empty();
    }
  }

  public void doDamage(DamageSource damageSource) {
    if (this instanceof Defenseable) {
      // Jika entity bisa bertahan coba memintanya
      // bertahan damage dari damageSource (Method
      // defend dapat memodifikasi damageSource sesuai
      // efek yang ingin di buatnya)
      Defenseable defenseableEntity = (Defenseable) this;
      if (defenseableEntity.canDefense()) {
        defenseableEntity.defend(damageSource);
      }
    }
    
    if (damageSource.isCanceled()) {
      // Damage is fully cancelled
      // -----------------------------
      // Damage dibatalkan atau tidak ada damage sama sekali
      // jadi tidak melakukan apa-apa
      return;
    }
    
    // Ambil jumlah damage point lalu pastikan tidak
    // menjadi negatif
    float damage = damageSource.getDamagePoint();
    if (damage < 0.0f) {
      damage = 0.0f;
    }

    // lalu update health dan mulai berkedip
    this.setHealth(this.healthPoint - damage);
    this.flash();
    
    // Jika mati, catat damage terakhir sebelum
    // mati
    if (this.isDead()) {
      this.deathBy = Optional.of(damageSource);
    }
  }

  public void flash() {
    this.flashDuration = LivingEntity.FLASH_DURATION;
    this.flashPeriod = LivingEntity.FLASH_PERIOD;
    this.flashState = true;
  }
  
  public void resetFlash() {
    this.flashState = false;
    this.flashDuration = -1.0f;
    this.flashPeriod = -1.0f;
  }

  public boolean isFlashing() {
    // apakah entity sedang perkedip?
    return this.flashDuration > 0.0f;
  }

  // This one should be used in render code
  // to either render unflashed (which is false)
  // or flashed (very bright version, which is true)
  // --------------------------------------------------
  // Yang ini seharusnya digunakan pada rendercode
  // untuk entah merender entity belum berkedip (yaitu false)
  // atau flashed (warna sangat cerah, yaitu true)
  public boolean getFlashState() {
    if (!this.isFlashing()) {
      // jika entity tidak sedang berkedip
      // return false saja, yaitu render normal
      return false;
    }
    
    return this.flashState;
  }
  
  public void burn(float burnTime, DamageSource source) {
    if (!this.canBurn()) {
      // Entity tidak bisa terbakar jadi biarkan saja
      return;
    }

    // Mulai hitung mundur untuk delay sebelum damage berikutnya
    this.burnTimer = burnTime;
    this.doDamage(source);
  }
  
  @Override
  public final Optional<FloatRectangle> getBoxToBeCheckedForTileStep() {
    return Optional.of(this.getLegBox());
  }

  @Override
  public void tick(float deltaTime) {
    // LivingEntity juga memiliki logikanya
    // sendiri seperti memajukan animasi untuk
    // flashing sama waktu terbakar
    
    burnTimer -= deltaTime;
    if (burnTimer < 0.0f) {
      this.burnTimer = -1.0f;
    }

    if (this.isFlashing()) {
      this.flashPeriod -= deltaTime;
      this.flashDuration -= deltaTime;

      if (this.flashPeriod < 0.0f) {
        this.flashPeriod = LivingEntity.FLASH_PERIOD;
        this.flashState = !this.flashState;
      }
    }
    
    if (this.isDead()) {
      // Kalau entity mati, mulai hitung mundur
      // waktu sebelum method die() dipanggil
      // method tersebut akhirnya meremove
      // entity dari dunia
      this.timeToDie -= deltaTime;
      
      if (this.timeToDie < 0.0f) {
        // Waktu telah habus maka entity diremove
        this.die();
      }
    }
    
    this.healthBar.max = this.getMaxHealth();
  }

  public Vec2 getLegPos() {
    return this.getLegBox().getCenter();
  }
  
  public void die() {
    this.getWorld().removeEntity(this);
  }
  
  // All living entities can be attacked
  // override this if some living entities
  // have state where they can't attack
  public boolean canBeAttacked() {
    return true;
  }
  
  // Secara default LivingEntity tidak ada aksi
  // special yang perlu dilakukan ketika menabrak
  // entity lain
  @Override
  public void onEntityCollision(Entity other) {
  }
  
  // Secara default LivingEntity tidak ada aksi
  // special yang perlu dilakukan ketika menabrak
  // tile lain
  @Override
  public void onTileCollision(IVec2 coord, Tile other) {
  }
  
  // Secara default LivingEntity tidak ada aksi
  // special yang perlu dilakukan ketika menabrak
  // wold border
  @Override
  public void onWorldBorderCollision() {
  }
  
  // Buat entity dapat dikontrol secara default
  @Override
  public boolean canBeControlled() {
    return true;
  }
  
  @Override
  protected Controller createController() {
    return new LivingEntityController(this);
  }
  
  @Override
  public void render(Graphics2D g, float deltaTime) {
    super.renderBars(g);
  }
  
  // LegBox init berguna untuk mendapatkan kotak dimana
  // kaki makhluk hidup ini berada. Digunakan untuk
  // menentukan dimana damage lava dapat terjadi
  public abstract FloatRectangle getLegBox();
  public abstract float getMaxHealth();
  public abstract float getMovementSpeed();
}

