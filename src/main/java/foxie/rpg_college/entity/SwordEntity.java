package foxie.rpg_college.entity;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Util;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.controller.Controller;
import foxie.rpg_college.entity.damage.EntityDamageSource;
import foxie.rpg_college.texture.Texture;
import foxie.rpg_college.tile.Tile;

public class SwordEntity extends Entity {
  private static final Vec2 SIZE = new Vec2(120.0f, 2.63f * 120.0f);
  
  // Offset pedang, sehingga pedang berputar di pegangan nya
  // bukan di ujung pegangan
  private static final float Y_OFFSET = SIZE.y() * 0.25f;
  
  // Seberapa jauh pedang mencapai, ini berguna untuk
  // menentukan entity mana yang kena damage
  private static final float SWING_DISTANCE = SIZE.y() - Y_OFFSET;
  
  // Kecepatan ayung pedang dalam derajat per detik
  private static final float SWING_SPEED = 360.0f /* deg/s */;
  
  // Siapa yang memegang pedang
  private final Entity wielder;
  
  // Sudut dimana pedang mulai diayun
  private final float swingStart;
  
  // Tekstur yang dipakai untuk pedang
  private final Texture texture;
  
  // Apakah pedang diayun sesuai arah jarum jama atau tidak
  private final boolean isClockwise;
  
  // Tujuan pedang diayunkan
  private final float angleDone;
  
  // Set untuk entity-entity yang sudah pernah didamage
  // ini ada agar pedang dimendamage entity yang sama berkali-kali
  private final HashSet<Long> damagedEntities = new HashSet<>();
  private final float damage;
  
  // Offset yang diberikan oleh pemengan sehingga pedang dapat terletak
  // ditempat yang diinginkan seperti tangan bukan tengah badan
  private final Vec2 offset;
  
  // Menyimpan berapa sudut telah lewati dihitung dari swingStart
  private float angleCurrent = 0.0f;
  
  // Apakah pedang sudah selesai menyayun >w<
  private boolean doneSwinging = false;
  
  /*
  
  
  
  Jilid hijau
  
  
  
  
  
  
  
  
  */
  
  public SwordEntity(Game game, Entity wielder, float damage, float swingStart, float swingEnd, boolean isClockwise, Vec2 offset) {
    super(game);
    
    // Tentukan dimana ayunan dimulai
    // kalau searah jarum jam maka swingStart jadi awalnya
    // kalau lawan arah jarum jam maka swingEnd digunakan
    if (isClockwise) {
      this.swingStart = Util.normalizeAngle(swingStart);
    } else {
      this.swingStart = Util.normalizeAngle(swingEnd);
    }
    
    // Hitung berapa banyak pedang perlu berputar agar selesai
    // Math.abs merupakan fungsi untuk mengabsolutkan angka jadi
    // ini selalu positif, kita hanya perlu tau jaraknya dan jarak
    // negatif tidak ada
    this.angleDone = Math.abs(swingEnd - swingStart);
    
    this.wielder = wielder;
    this.damage = damage;
    this.offset = offset;
    this.isClockwise = isClockwise;
    this.texture = game.getTextureManager().getTexture("entity/sword");
  }
  
  public SwordEntity(Game game, Entity wielder, float damage, float swingStart, float swingEnd, boolean isClockwise) {
    this(game, wielder, damage, swingStart, swingEnd, isClockwise, new Vec2(0.0f));
  }
  
  public boolean isDoneSwinging() {
    return this.doneSwinging;
  }
  
  public void renderSword(Graphics2D g, float deltaTime) {
    // Menampilkan pedang dan offset render nya :3
    AffineTransform transform = EntityHelper.calculateCameraTransform(this);
    transform.translate(-SwordEntity.SIZE.x() * 0.5f, -(SwordEntity.SIZE.y() - Y_OFFSET));
    transform.scale(
      SwordEntity.SIZE.x() / this.texture.width(),
      SwordEntity.SIZE.y() / this.texture.height()
    );
    
    g.drawImage(this.texture.image(), transform, null);
  }
  
  public void updatePos() {
    // Update posisi agar sesuai dengan pemegangnya setelah
    // di offset
    this.setPos(this.wielder.getPos().add(this.offset));
  }
  
  @Override
  public boolean canCollideWith(Entity other) {
    return false;
  }
  
  @Override
  public void onTileCollision(IVec2 coord, Tile other) {
  }
  
  @Override
  public void onWorldBorderCollision() {
  }
  
  @Override
  public void onEntityCollision(Entity other) {
  }
  
  @Override
  public Optional<CollisionBox> getCollisionBox() {
    // Pedang tidak memiliki kotak collision jadi empty saja
    // karena pedang tidak menabrak apa-pun. Ia hanya menembus -w-
    return Optional.empty();
  }
  
  @Override
  public boolean isVisible(Camera cam) {
    return true;
  }
  
  @Override
  public Optional<FloatRectangle> getRenderBound() {
    return Optional.empty();
  }
  
  @Override
  public void render(Graphics2D g, float deltaTime) {
    // NOTE: THe actual rendering for sword happened at above, by call from the wielder!
    // ----------------------------------------------------------------------------------
    // Ini kosong karena render dilakukan oleh method lain, disebabkan karena urutan nya
    // tidak tentu relatif entity yang memegang. Kadang entity menimpa pedang atau pedang
    // menimpa entity
  }
  
  @Override
  public void tick(float deltaTime) {
    if (this.doneSwinging) {
      return;
    }
    
    this.angleCurrent += SwordEntity.SWING_SPEED * deltaTime;
    
    if (this.angleCurrent > this.angleDone) {
      this.angleCurrent = this.angleDone;
      this.doneSwinging = true;
      this.getWorld().removeEntity(this);
      return;
    }
    
    this.updatePos();
    this.setRotation(this.swingStart + this.angleCurrent * (this.isClockwise ? 1.0f : -1.0f));
    
    // Untuk tiap posisi cari entity-entity yang dapat didamage
    Iterator<LivingEntity> affectedEntities = this.getWorld().findEntities(this.getPos(), SwordEntity.SWING_DISTANCE)
      .filter(e -> e != this.wielder)
      .filter(e -> e instanceof LivingEntity)
      .map(e -> (LivingEntity) e)
      .filter(e -> !e.isDead())
      .iterator();
    
    while (affectedEntities.hasNext()) {
      LivingEntity affected = affectedEntities.next();
      
      // Setelah itu menghitung sudut entity relatif dengan pemegang dengan margin
      // 5 derajat, jika sudut nya mirip maka sudah yakin entity dapat didamage
      float angleToLookatIt = affected.getPos().sub(this.getPos()).calculateAngle();
      if (Math.abs(this.getRotation() - angleToLookatIt) <= 5.0f) {
        if (!this.damagedEntities.contains(affected.id)) {
          // Massukkan ke list damagedEntities agar tidak didamage lagi
          this.damagedEntities.add(affected.id);
          
          // Entity is affected
          affected.doDamage(new EntityDamageSource(this.wielder, this.damage));
        }
      }
    }
  }
  
  @Override
  public Optional<FloatRectangle> getBoxToBeCheckedForTileStep() {
    return Optional.empty();
  }
  
  @Override
  public boolean canBeControlled() {
    return false;
  }
  
  @Override
  protected Controller createController() {
    return null;
  }
}
