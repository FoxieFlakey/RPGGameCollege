package foxie.rpg_college.world;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Optional;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.entity.LivingEntity;
import foxie.rpg_college.entity.TurretEntity;
import foxie.rpg_college.texture.Texture;

// Kelas battle arena adalah dunia yang melakukan
// battle-batle didalamnya rencangnya begitu tetapi
// keterbatasan waktu, diubah menjadi dunia untuk
// sebuah level sederhana dimana player coba hidup
// selama mungkin sambil menghancurkan turret yang dispawn
public class BattleArena extends World {
  private static final float RESPAWN_DELAY = 6.0f;
  
  private final Texture backgroundTexture;

  // Array ini menyimpan list-list dari turrets yang masih
  // hidup agar dapat diperksa nanti
  private final ArrayList<TurretEntity> turrets = new ArrayList<>();
  
  // menentukan apakah turret sudah di hancurkan
  private boolean allTurretDestroyed = false;

  // jeda berapa detik lagi sampai waktunya
  // menspawn baru, kalau -1.0f berarti spawn
  // turret-turret baru, lalu variabel diatas
  // di reset ke 'false'
  private float turretRespawnDelay = -1.0f;
  
  // Hitungan berapa turret yang spawn
  // naik satu untuk tiap "level" yang player dapat
  // bertahan
  private int turretCount = 1;

  public BattleArena(Game game) {
    super(game, new FloatRectangle(
      new Vec2(
        -game.getTextureManager().getTexture("world/battle_arena/background").width() / 2.0f,
        -game.getTextureManager().getTexture("world/battle_arena/background").height() / 2.0f
      ),
      new Vec2(
        game.getTextureManager().getTexture("world/battle_arena/background").width() / 2.0f,
        game.getTextureManager().getTexture("world/battle_arena/background").height() / 2.0f
      )
    ));
    this.backgroundTexture = game.getTextureManager().getTexture("world/battle_arena/background");
    
    // Masukkan lava sama portal
    // awalnya untuk mentest saja agar 
    // dapat memperiksa apakah portal sama lava
    // dapat bekerja di dunia lain
    this.addTile(new IVec2(4,0), game.TILES.LAVA_TILE);
    this.addTile(new IVec2(4,4), game.TILES.PORTAL_TO_OVERWORLD);
    
    // Menambahkan sebuah dinding sehingga player dapat melindungi
    this.addTile(new IVec2(-3,0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(-2,0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(-1,0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(0,0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(1,0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(1,1), game.TILES.WALL_TILE);
    this.addTile(new IVec2(1,2), game.TILES.WALL_TILE);
    this.addTile(new IVec2(1,3), game.TILES.WALL_TILE);
    
    // Karena di awal belum ada turret dispawn maka
    // spawn sekerate :3
    this.spawnTurrets();
  }
  
  @Override
  public Vec2 getWorldSpawnPoint() {
    return new Vec2(-100.0f, 300.0f);
  }
  
  private void spawnTurrets() {
    float currentY = -this.backgroundTexture.height() / 2.0f + 100.0f;
    float maxY = this.backgroundTexture.height() / 2.0f;
    // Ini menghitung jarak antar turret sehingga dapat
    // menspawn kurang-lebih julah this.turretCount
    float intervalY = (maxY - currentY) / this.turretCount;
    float x = this.backgroundTexture.width() / 2.0f - 100.0f;

    // Variabel x dan currentY menentukan
    // posisi turret yang akan dispawn

    while (currentY < maxY) {
      TurretEntity turret = new TurretEntity(this.getGame());
      this.addEntity(turret);
      turret.setPos(new Vec2(x, currentY));
      currentY += intervalY;
      
      this.turrets.add(turret);
    }
  }
  
  @Override
  public void tick(float deltaTime) {
    super.tick(deltaTime);
    
    if (this.allTurretDestroyed) {
      // Jika semuah turret sudah hancur
      // mulai hitung mundurnya
      this.turretRespawnDelay -= deltaTime;
      if (this.turretRespawnDelay < 0.0f) {
        // jika semua sudah hancur spawn baru
        this.allTurretDestroyed = false;
        this.spawnTurrets();
        
        Optional<Entity> player = this.getGame().getPlayer();
        // Operator instanceof di Java memeriksa apakah aman untuk
        // meng cast Entity ke LivingEntity, karena kelas Entity  saja
        // tidak tau kalau itu apakah LivingEntity atau ProjectileEntity
        // atau apa saja yang bukan. Contohnya
        //
        // class Dasar {
        // }
        //
        // class Anak1 extends Dasar {
        //   void methodAnak1() {
        //     ...
        //   }
        // }
        //
        // class Anak2 extends Dasar {
        // }
        //
        // Dengan kelas-kelas diatas contohnya
        //
        // Dasar a = (Dasar) new Anak1();
        // Dasar b = (Dasar) new Anak2();
        //
        // Jika kita langsung mencast seperti ini mungkin Java
        // melembar ClassCastException karena salah kelas
        //
        // Anak1 anak1 = (Anak1) b; // Java mungkin melemparkan ClassCastException
        // anak1.methodAnak1();
        //
        // Tentunya kita bisa melihat kalau 'b' itu sebenarnya Anak2
        // tetapi Java tidak bisa menentukan saat runtime. Bayangkan
        // 'b' itu sebuah parameter ke method yang mengambil Dasar
        // Kode-kode memanggil method tersebut pakai object Anak2 tidak
        // error karena memang bisa tetapi method tersebut tidak
        // bisa tau kalau cast itu aman atau tidak. Java menyediakan
        // keyword instaceof persis untuk keadaan itu
        //
        // Kode diatas dapat diubah menjadi
        //
        // if (b instanceof Anak1) {
        //   Anak1 bSebagaiAnak1 = (Anak1) b; // Selalu berhasil
        //   bSebagaiAnak1.methodAnak1();
        //   ... kode lain..
        // } else {
        //   .. kode kalau 'b' bukanlah Anak1
        // }
        //
        // Disini pemeriksaan dibutuhkan karena Entity tidak menyimpan
        // HP hanya LivingEntity yang menyimpan karena Entity adalah makhluk
        // hidup yang berarti bisa bergerak, hidup, etc
        if (player.isPresent() && player.get() instanceof LivingEntity) {
          // Setelah itu coba heal player kalau ada
          // heal nya sebanyak 25% dari HP penuh
          LivingEntity playerEntity = (LivingEntity) player.get();
          playerEntity.setHealth(playerEntity.getHealth() + playerEntity.getMaxHealth() * 0.25f);
        }
      }
    } else {
      // Jika masih belum semua mati
      // hitung jumlah yang masih hidup

      // Check if all turrets destroyed
      int liveCount = 0;
      for (TurretEntity e : this.turrets) {
        if (!e.isDead()) {
          liveCount += 1;
        }
      }
      
      if (liveCount == 0) {
        // Kalau semuanya mati
        // mulai hitung mundur lalu
        // clear array yang berisi turrets
        // dan tambahkan satu turret untuk
        // level berikutnya
        this.allTurretDestroyed = true;
        this.turretRespawnDelay = BattleArena.RESPAWN_DELAY;
        this.turretCount += 1;
        this.turrets.clear();
      }
    }
  }
  
  @Override
  public void render(Graphics2D g, float deltaTime) {
    WorldUtil.renderBackground(this, g, this.backgroundTexture.image());
    super.render(g, deltaTime);
  }
}
