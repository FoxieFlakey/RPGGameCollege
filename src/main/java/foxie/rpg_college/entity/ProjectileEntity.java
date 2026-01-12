package foxie.rpg_college.entity;

import java.util.Optional;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.controller.ProjectileEntityController;
import foxie.rpg_college.entity.controller.Controller;
import foxie.rpg_college.tile.Tile;

// Projectile entity berisi kode-kode uumum yang berlaku di semua projectile
// seperti arrow, fireball dan lain lain UwU
//
// Mereka memiliki data/method/kegunaan umum seperti
// 1. memiliki penembak
// 2. memiliki kecepatan
// 3. memiliki batas waktu
// 4. tidak bisa menabrak penembaknya
// 5. dan lain sebagainya :3
public abstract class ProjectileEntity extends Entity {
  // Melacak apakah projectile telah bertabrak apa-pun
  // kalau iya, jangan bergerak dan berhenti melakukan damage
  private boolean hasProjectileHitSomething = false;
  
  private final Entity shooter;
  private float velocity;
  private float timeToLive;
  
  public ProjectileEntity(Game game, Entity shooter, float timeToLive, float velocity) {
    super(game);
    this.shooter = shooter;
    this.timeToLive = timeToLive;
    this.velocity = velocity;
  }
  
  @Override
  public boolean canCollideWith(Entity other) {
    // Projectile tidak dapat bertabrakan dengan projectile lain
    if (other instanceof ProjectileEntity) {
      // Arrow dont collide with arrow
      return false;
    }
    
    // Penembak tidak dapat dilukai oleh diri sendiri
    if (other == shooter) {
      // Do not damage the shoter
      return false;
    }
    
    // Everything else arrow does not affect
    // ----------------------------------------
    // Untuk hal-hal lain, tanya sama method
    // abstrak canBeHit
    return this.canBeHit(other);
  }
  
  @Override
  public void tick(float deltaTime) {
    // Jika projectile dikendalikan jangan melakukan apa-apa
    if (this.isBeingControlled()) {
      return;
    }
    
    this.timeToLive -= deltaTime;
    
    // bergerak ke depan sebanyak kecepatan dikali deltaTime
    // unitVectorOfAngle digunakan untuk membuat vektor arah
    // menuju ke arah specific dalam derajat lalu bisa dikalikan
    // dengan berapa banyak di pindah lalu di set
    Vec2 velocity = Vec2.unitVectorOfAngle(this.getRotation()).mul(this.velocity * deltaTime);
    this.setPos(this.getPos().add(velocity));
    
    // Projectile hidup terlalu lama, remove
    if (this.timeToLive < 0.0f) {
      this.die();
    }
  }
  
  public float getSpeed() {
    return this.velocity;
  }
  
  void die() {
    this.getWorld().removeEntity(this);
  }
  
  public boolean hasProjectileHitSomething() {
    return this.hasProjectileHitSomething;
  }
  
  // method agar pemanggil dapat cari entity
  // yang menembak
  public Entity getShooter() {
    return this.shooter;
  }

  @Override
  public Optional<FloatRectangle> getBoxToBeCheckedForTileStep() {
    return Optional.empty();
  }
  
  @Override
  public void onCollision() {
    super.onCollision();
    
    // Arrow collided a target
    // ------------------------
    // Arrow sudah bertabrakan jadi cancel
    // pergerakannya :3
    this.velocity = 0.0f;
  }
  
  @Override
  public void onEntityCollision(Entity other) {
    if (!this.hasProjectileHitSomething) {
      // Damage entity lain kalau belum ketabrak
      this.onHit(other);
      this.hasProjectileHitSomething = true;
    }
    this.die();
  }
  
  @Override
  public void onWorldBorderCollision() {
    // Projectile telah menabrak dinding dunia
    // jadi set variabel
    this.hasProjectileHitSomething = true;
  }
  
  @Override
  public void onTileCollision(IVec2 coord, Tile other) {
    // Projectile telah menabrak sebuah tile jadi
    // set variabel
    this.hasProjectileHitSomething = true;
  }
  
  @Override
  public void onTileStep(Tile tile, IVec2 tileCoord) {
  }
  
  @Override
  public boolean canBeControlled() {
    // Projectile entity dapat dikontrol
    return true;
  }
  
  @Override
  protected Controller createController() {
    return new ProjectileEntityController(this);
  }
  
  public abstract boolean canBeHit(Entity other);
  public abstract void onHit(Entity other);
}
