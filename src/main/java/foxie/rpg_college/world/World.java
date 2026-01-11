package foxie.rpg_college.world;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.CollisionBox;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.tile.Tile;

// Kelas menjelaskan cara dan jalan simulasi dunia, banyak kode
// disini karena semua dunia memiliki banyak fungsi yang mirip
// seperti simulasi tabrakan, memanggil method tick, merender
// tiap entity dan menyimpan list tiles sama entities dan lain-lain
//
// Kebanyak subclass-subclass hanya mengoverride beberapa method
// untuk menambahkan keunikannya seperti merender background lalu
// menambah fungsi tambahkan seperti tiap 5 detik spawn enemy baru
// dan selanjutnya... 
public abstract class World {
  private final Game game;
  
  // Render bound berfungsi sebagai batas dunia yang dapat
  // ditampilkan
  private final FloatRectangle renderBound;
  
  // Valid bound berfungsi sebagai batas dimana posisi-posisi
  // yang entity boleh terletak (kalau melebihi method setPos di
  // entity otomatis memanggil World untuk memeriksa dan perbaiki)
  private final FloatRectangle validBound;
  
  // ConcurrentHashMap adalah varian HashMap yang dimana thread berbeda-beda dapat
  // mengakses hashmap secara aman. Game ini tidak menjalankan banyak kode di berbeda
  // thread. Tetapi salah satu fungsi yang berguna dari varian itu adalah kode dapat
  // meremove sebuah pasangan tanpa ada error saat mengiterasi
  //
  // Itu fungsi yang sangat dibutuhkan karena beberapa entity dalam method tick (yang
  // berarti World sedang mengiterasi) perlu menghapus dirinya atau entity lain. Fungsi
  // tersebut sangat berguna
  private final ConcurrentHashMap<Long, Entity> entities = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<IVec2, Tile> tiles = new ConcurrentHashMap<>();

  // World border berfungsi sebagai menjadi batas tabrakan dunia sehingga simulasi
  // tabrakan dapat terjadi dengan benar di ujung dunia
  private final CollisionBox[] worldBorder;
  
  // BORDER_DEPTH menentukan seberapa jauh kotak tabran dunianya
  // angka nya perlu sangat besar agar entitiy tidak terlempar keluar dari batas 
  private final static float BORDER_DEPTH = 200000000.0f;
  
  // Sedang kan BORDER_INNER_DEPTH menentukan seberapa dalam dari ujung dunia
  // kotak tabrakan akan berada
  private final static float BORDER_INNER_DEPTH = 50.0f;

  public World(Game game, FloatRectangle bound) {
    this.game = game;
    this.renderBound = bound;
    
    // Menentukan batas posisi yang valid untuk dunia
    // posisinya secara sengaja agak lebih dekat ke ujung
    // agar simulasi tabrakan tetap bisa terjadi dengan benar
    // tanpa verifikasi posisi secara tidak sengaja menghentikannya
    //
    // Salah satu dimana itu penting ada projectile seperti arrow
    // arrow jika menabrak sesuatu, Arrow langsung berhenti berjalan
    // dan juga menjadi tidak berbahaya, kalau tanpa -20.0f dibawah
    // ini arrow tetap diam karena verifikasi di setPos, dan arrow
    // tidak pernah mendapatkan panggilan ke Entity#onCollision
    // maupun Entity#onWorldBorderCollision yang memberitau arrow jika
    // ia telah menabrak sesuatu :3
    //
    // Tanpa panggilan itu arrow tetap berbahaya di ujung dunia padahal
    // dia seharusnya menabrak batas dunia...
    this.validBound = new FloatRectangle(
      this.renderBound.getTopLeftCorner().add(new Vec2(World.BORDER_INNER_DEPTH - 20.0f)),
      this.renderBound.getBottomRightCorner().sub(new Vec2(World.BORDER_INNER_DEPTH - 20.0f))
    );

    float left = this.renderBound.getTopLeftCorner().x();
    float right = this.renderBound.getBottomRightCorner().x();
    float top = this.renderBound.getTopLeftCorner().y();
    float bottom = this.renderBound.getBottomRightCorner().y();

    float width = this.renderBound.getSize().x();
    float height = this.renderBound.getSize().y();
    
    if (width < World.BORDER_INNER_DEPTH || height < World.BORDER_INNER_DEPTH) {
      throw new IllegalArgumentException("World bound is too small");
    }

    Vec2 center = new Vec2(
      (left + right) * 0.5f,
      (top + bottom) * 0.5f
    );
    
    this.worldBorder = new CollisionBox[] {
      // The top part of world
      new CollisionBox(new Vec2(center.x(), top - World.BORDER_DEPTH * 0.5f), new Vec2(width + World.BORDER_DEPTH * 2.0f, World.BORDER_DEPTH + World.BORDER_INNER_DEPTH * 2.0f), true),
      // The bottom part of world
      new CollisionBox(new Vec2(center.x(), bottom + World.BORDER_DEPTH * 0.5f), new Vec2(width + World.BORDER_DEPTH * 2.0f, World.BORDER_DEPTH + World.BORDER_INNER_DEPTH * 2.0f), true),

      // Left part of world
      new CollisionBox(new Vec2(left - World.BORDER_DEPTH * 0.5f, center.y()), new Vec2(World.BORDER_DEPTH + World.BORDER_INNER_DEPTH * 2.0f, height + World.BORDER_DEPTH * 2.0f), true),
      // Right part of world
      new CollisionBox(new Vec2(right + World.BORDER_DEPTH * 0.5f, center.y()), new Vec2(World.BORDER_DEPTH + World.BORDER_INNER_DEPTH * 2.0f, height + World.BORDER_DEPTH * 2.0f), true)
    };
  }

  public final Game getGame() {
    return this.game;
  }
  
  public final FloatRectangle getWorldBound() {
    return this.validBound;
  }
  
  public final FloatRectangle getRenderBound() {
    return this.renderBound;
  }

  // Fungsi ini menambahkan sebuah tile pada
  // koordinat coord and tile 'tile', Kelas tile
  // ini didesign agar ada satu instance saja walaupun
  // tampil berkali-kali di dunia. Hal ini karena
  // tile seharusnya tidak memiliki fungsi berbeda
  // karena posisinya.
  //
  // Sedangkan entity unik, untuk tiap instance
  // dan hampir tidak ada yang bisa dishare antara
  // dua entity yang sama. Kalau tile banyak yang
  // bisa dishare
  public void addTile(IVec2 coord, Tile tile) {
    if (this.tiles.containsKey(coord)) {
      throw new IllegalStateException("Attempting to add more than one tile to same coord");
    }
    
    Vec2 coordInWorld = Tile.fromTileCoordToWorldCoord(coord);
    if (!this.isValidPos(coordInWorld)) {
      throw new IllegalArgumentException("Attempting to add tile to outside of world");
    }

    this.tiles.put(coord, tile);
  }
  
  // Menambahkan entity dan meremove nya
  // dari dunia sebelumnya kalau sudah
  // ada di dunia lain
  public void addEntity(Entity entity) {
    // Memeriksa apakah entity sudah pernah
    // ditambahkan. Ini bekerja dengan benar
    // karena field 'id' final dan isinya dikendalikan
    // oleh kelas Entity di konstruktornya
    //
    // Jadi subclass-subclass tidak bisa
    // mengutak-atik nya.
    if (this.entities.containsKey(entity.id)) {
      throw new IllegalStateException("Attempt to add same entity twice");
    }

    this.entities.put(entity.id, entity);
    // Pertama kita meremove entitynya dari
    // dunia lain jika ada
    if (entity.getWorld() != null) {
      entity.getWorld().doRemoveEntityButDontDispatchEvents(entity);
    }
    
    // Lalu menambahkan entity tersebut ke dunia ini
    entity.setWorld(this);
    
    // Clamp the pos to the valid position
    // ------------------------------------
    // Periksa posisi, jika tidak valid update
    // dan verifikasi posisi di dunia barunya
    if (!this.isValidPos(entity.getPos())) {
      entity.setPos(this.validatePos(entity.getPos()));
    }
  }

  // This method is called when entity is removed from
  // current world but dont dispatch any events
  protected void doRemoveEntityButDontDispatchEvents(Entity entity) {
    if (!this.entities.containsKey(entity.id)) {
      throw new IllegalStateException("Attempt to remove unknown entity");
    }

    this.entities.remove(entity.id);
  }

  // Fungsi sesuai namanya meremove entity dari
  // dunia ini
  public void removeEntity(Entity entity) {
    this.doRemoveEntityButDontDispatchEvents(entity);
    entity.setWorld(null);
    if (entity.canDispatchControllerEvents()) {
      if (entity.getController().isPresent()) {
        entity.getController().get().dispatchOnEntityNoLongerControllable();
      }
    }
  }
  
  // The iterator must not be saved as new entitity may be added later
  // -----------------------------------------------------------------
  // Fungsi ini mencari entity yang menimpa sebuah titik
  // kotak entity yang diperiksa adalah kotak tabrakannya
  //
  // Pemanggil harus langsung memakai hasil nya, tidak boleh
  // disimpan karena hasilnya tidak aman untuk disimpan karena
  // entity-entity di dunia mungkin berubah
  public Stream<Entity> findEntitiesOverlaps(Vec2 point) {
    // Stream adalah sebuah kelas diJava yang mengfasilitas
    // "functional programming". Kelas ini memudahkan untuk
    // menambahkan kriteria (method filter), mengubah value
    // (method map), dan lain-lain. Tanpa pemanggil perlu tau
    // apa yang diperiksa
    //
    // Ini dapat dianalogikan sebagai "pipeline" di jalur
    // produksi pabrik. Tiap orang di samping jalur konveyor
    // adalah closure yang diberikan ke tiap method yang
    // mementingkan memproses item sekarang yang didepan
    // orangnya (atau closure dipanggil). Dan konveyor otomatis
    // melanjutkannya ke orang/closure berikutnya untuk diproses
    //
    // Patern ini memisahkan proses "apa yang dilakukan" dan
    // "caranya". Caranya di contexts ini adalah iterasi
    //
    // Contoh pengunaan nya
    //
    // record Buku {
    //   String[] tagBuku,
    //   int tahunRelease,
    //   String namaPenulis,
    //   String judul,
    //   int jumlahDiprint
    // }
    // 
    // Buku[] perpustakaan = {<.. list buku-buku ..>};
    //
    // // Kode berikut ada pencarian "umum" yang mereturn hasil
    // // buku-buku baru dan populer. Menggunakan filter kalau
    // // closure dalam filter return true maka itu hasil yang diiginkan
    // // jika false, maka hasil tidak muncul
    // Stream<Buku> bukuBaruPopuler = Arrays.stream(perpustakaan)
    //     .filter(buku -> buku.jumlahDiprint >= 100)
    //     .filter(buku -> buku.tahunRelease >= 2023);
    //
    // // Kode berikut ini mungkin bisa didalm method ayng bernama
    // // "cariBukuPopularJavaYangDiTulisPakRifky"
    // String penulisDicari = "Pak Rifky";
    // Stream<Buku> hasil = bukuBaruPopuler;
    //   .filter(buku -> buku.namaPenulis.equals(penulisDicari));
    //   .filter(buku -> Arrays.asList(buku.tagBuku).contains("Java"));
    //
    // // Pada pengguna, pengguna akhirnya hanya mengubah stream
    // // jadi iterator lalu iterasi hasil-hasilnya tanpa terlalu
    // // peduli apa sebenarnya yang terjadi. Hasil nya yang penting
    // // bagi kode yang memprint hasil
    // System.out.println("Berikut adalah judul buku" +
    //   "baru tentang algoritma Java populer yang ditulis" +
    //   "oleh Pak Rifky:"
    // );
    //
    // Kita ingin memprint judul jadi buku di "ubah" ke judul dengan
    // menggunakan method map dan membaca field judul
    // Iterator<String> iteratorHasil = hasil.map(buku -> buku.judul)
    //    .iterator();
    // for (int i = 1; iteratorHasil.hasNext(); i++) {
    //   System.out.println(" " + i + ". " + iteratorHasil.next());
    // }
    //
    // Bisa dilihat pattern tersebut memudahkan pengguna dan
    // method yang mecari listnya. dan menambah requirements
    // secara progresif
    //
    // Closure closure adalah sebuah fungsi yang declare secara lokal
    // dan hanya menghasilkan function object. Yang program dapat panggil
    // secara umum tanpa mengetahui tipe concretnya. Closure sangat penting
    // didalam pola functional programming.
    //
    // Objek fungsi dapat dibuat dari method kelas dengan cara "::" seperti
    // Object::new mengakses konstruktor dari object atau Object::method1
    // untuk mengakses method1 sebagai value
    return this.entities.values()
      .stream()
      .filter(e -> {
        Optional<CollisionBox> maybeBox = e.getCollisionBox();
        if (maybeBox.isEmpty()) {
          return false;
        }
        
        return maybeBox.get().asRect().contains(point);
      });
  }

  void checkCollisionInner(Entity e, Entity other, CollisionBox thisBox) {
    if (other.getCollisionBox().isEmpty() || other == e) {
      return;
    }

    if (other.canCollideWith(e) == false || e.canCollideWith(other) == false) {
      // Prefer collision not happening if there conflicting
      // answers
      return;
    }

    CollisionBox otherBox = other.getCollisionBox().get();
    
    if (thisBox.checkCollisionAndFix(otherBox)) {
      e.onCollision();
      other.onCollision();
      
      e.onEntityCollision(other);
      other.onEntityCollision(e);
    }
  }

  void checkCollisionWithTiles(Entity e, CollisionBox thisBox) {
    CollisionBox tempBox = new CollisionBox(new Vec2(0.0f, 0.0f), Tile.SIZE, true);
    for (Entry<IVec2, Tile> coordAndTile : this.tiles.entrySet()) {
      if (!coordAndTile.getValue().isCollisionEnabled()) {
        continue;
      }

      if (e.getWorld() == null || e.getCollisionBox().isEmpty()) {
        // Entity decided that it don't want colliding anymore
        return;
      }

      tempBox.setPos(Tile.fromTileCoordToWorldCoord(coordAndTile.getKey()));
      if (thisBox.checkCollisionAndFix(tempBox)) {
        e.onCollision();
        e.onTileCollision(coordAndTile.getKey(), coordAndTile.getValue());
      }
    }
  }

  void checkCollision(Entity e) {
    if (e.getWorld() == null || e.getCollisionBox().isEmpty()) {
      return;
    }

    CollisionBox thisBox = e.getCollisionBox().get();
    // Check collision against all tiles
    this.checkCollisionWithTiles(e, thisBox);

    // Try fix the collision with other entities
    for (Entity other : this.entities.values()) {
      if (e.getWorld() == null || e.getCollisionBox().isEmpty()) {
        // Entity decided it doesnt want colliding
        return;
      }
      
      checkCollisionInner(e, other, thisBox);
      
      if (other.getCollisionBox().isEmpty()) {
        // Other entity decided that it don't want colliding anymore
        continue;
      }
    }

    // Check collision against all tiles
    this.checkCollisionWithTiles(e, thisBox);

    // Check collision against world border
    for (CollisionBox otherBox : this.worldBorder) {
      if (thisBox.checkCollisionAndFix(otherBox)) {
        if (e.getWorld() == null || e.getCollisionBox().isEmpty()) {
          // Entity decided that it don't want colliding anymore
          return;
        }
        
        e.onCollision();
        e.onWorldBorderCollision();
      }
    }
  }

  protected void tickEntities(float deltaTime) {
    for (Entry<IVec2, Tile> coordAndTile : this.tiles.entrySet()) {
      coordAndTile.getValue().tick(deltaTime, coordAndTile.getKey());
    }
    
    for (Entity e : this.entities.values()) {
      Optional<FloatRectangle> maybeCheckBounds = e.getBoxToBeCheckedForTileStep();
      if (maybeCheckBounds.isEmpty()) {
        continue;
      }
      
      FloatRectangle checkBounds = maybeCheckBounds.get();
      Vec2 topLeft = checkBounds.getTopLeftCorner().div(Tile.SIZE.x());
      Vec2 bottomRight = checkBounds.getBottomRightCorner().div(Tile.SIZE.x());
      
      IVec2 tileToCheckStart = new IVec2(
        (int) Math.floor((double) topLeft.x()) - 1,
        (int) Math.floor((double) topLeft.y()) - 1
      );
      IVec2 tileToCheckEnd = new IVec2(
        (int) Math.ceil((double) bottomRight.x()) + 1,
        (int) Math.ceil((double) bottomRight.y()) + 1
      );
      
      for (int y = tileToCheckStart.y(); y < tileToCheckEnd.y(); y++) {
        for (int x = tileToCheckStart.x(); x < tileToCheckEnd.x(); x++) {
          IVec2 coord = new IVec2(x, y);
          Optional<Tile> tileOptional = this.getTileAt(coord);
          if (tileOptional.isEmpty()) {
            continue;
          }
          
          Tile tile = tileOptional.get();
          Vec2 tileCoord = Tile.fromTileCoordToWorldCoord(coord);
          FloatRectangle tileRect = new FloatRectangle(
            tileCoord.sub(Tile.SIZE.mul(0.5f)),
            tileCoord.add(Tile.SIZE.mul(0.5f))
          );
          
          if (e.getWorld() == null || e.getBoxToBeCheckedForTileStep().isEmpty()) {
            // Entity decided that it don't want to collide anymore
            return;
          }
          
          if (tileRect.isIntersects(checkBounds)) {
            e.onTileStep(tile, coord);
            tile.steppedBy(e, coord);
          }
        }
      }
    }

    for (Entity e : this.entities.values()) {
      e.tick(deltaTime);
    }

    for (int i = 0; i < 5; i++) {
      for (Entity e : this.entities.values()) {
        checkCollision(e);
        
        if (e.getWorld() == null || e.getCollisionBox().isEmpty()) {
          // Entity decided that it don't want colliding anymore
          continue;
        }
      }
    }
  }

  // Method ini merender entity-entity dalam dunia
  // sama tiles
  protected void renderEntities(Graphics2D g, float deltaTime) {
    for (Entry<IVec2, Tile> coordAndTile : this.tiles.entrySet()) {
      coordAndTile.getValue().render(g, deltaTime, coordAndTile.getKey());
    }

    for (Entity e : this.entities.values()) {
      e.render(g, deltaTime);
      
      // Game memiliki logika kecil untuk debug mode agar mudah mendapatkan
      // beberapa bug, ini hanya tampil jika F3 di klik. Untuk sekarang
      // kode ini menggambar garis hijau menunjukkan ke arah mana entity
      // sedang melihat dan juga merender collision box dan render box
      //
      // Render box kebanyakan tidak dipakai, rencana untuk "culling" dengan
      // megetahui sebesar apa area yang tampil di entity, jika kotak tersebut
      // diluar kotak dunia yang nampak, entity tidak di render.
      //
      // Karena keterbatasan waktu belum di implementasi beserta juga beberapa
      // hal-hal lain -w- yang mungkin ada beberapa method/entity/tile yang tidak
      // terlalu berguna karena belum mendesign hal yang memakainya
      if (this.getGame().isDebugEnabled()) {
        Camera camera = this.game.getCamera();
        
        // Render direction which the entity viewing
        IVec2 start = camera.translateWorldToAWTGraphicsCoord(e.getPos()).round();
        IVec2 end = camera.translateWorldToAWTGraphicsCoord(e.getPos().add(Vec2.unitVectorOfAngle(e.getRotation()).mul(50.0f))).round();
        
        Stroke oldStroke = g.getStroke();
        
        g.setColor(Color.GREEN);
        g.setStroke(new BasicStroke(5.0f * this.game.getRenderScale()));
        g.drawLine(
          start.x(),
          start.y(),
          end.x(),
          end.y()
        );

        // Render the collision box too
        if (e.getCollisionBox().isPresent()) {
          FloatRectangle boxWorld = e.getCollisionBox().get().asRect();
          FloatRectangle boxRender = camera.translateWorldToAWTGraphicsCoord(boxWorld.getCenter(), boxWorld.getSize());
          
          int x = (int) boxRender.getTopLeftCorner().x();
          int y = (int) boxRender.getTopLeftCorner().y();
          int w = (int) boxRender.getSize().x();
          int h = (int) boxRender.getSize().y();
          
          g.drawRect(x, y, w, h);
        }
        
        // Render the render bound
        if (e.getRenderBoundInWorld().isPresent()) {
          g.setColor(Color.MAGENTA);
          FloatRectangle boxWorld = e.getRenderBoundInWorld().get();
          FloatRectangle boxRender = camera.translateWorldToAWTGraphicsCoord(boxWorld.getCenter(), boxWorld.getSize());
          
          int x = (int) boxRender.getTopLeftCorner().x();
          int y = (int) boxRender.getTopLeftCorner().y();
          int w = (int) boxRender.getSize().x();
          int h = (int) boxRender.getSize().y();
          
          g.drawRect(x, y, w, h);
        }
        
        g.setStroke(oldStroke);
      }
    }
  }

  public Optional<Tile> getTileAt(IVec2 pos) {
    return Optional.ofNullable(this.tiles.get(pos));
  }

  public void render(Graphics2D g, float deltaTime) {
    this.renderEntities(g, deltaTime);
  }
  
  public void tick(float deltaTime) {
    this.tickEntities(deltaTime);
  }
  
  public Vec2 validatePos(Vec2 pos) {
    // Mengvalidasi posisi pos dan memperbaikinya
    return this.getWorldBound().clampCoordinate(pos);
  }
  
  public boolean isValidPos(Vec2 pos) {
    // Memeriksa apakah posisi 'pos' valid atau tidak
    return this.getWorldBound().contains(pos);
  }
  
  // Method ini digunakan untuk mencara entity-entit yyang
  // didalam (setengah atau sepenuhnya) didalam sebuah lingkaran
  // sebesar 'radius' dan posisi di 'pos'. Beberapa pengguna dari
  // method ini adalah TurretEntity untuk menscan entity yang terdekat
  // dan FireballEntity untuk menghitung entities yang terdampak oleh
  // bola api nya yang memiliki lingkaran
  public Stream<Entity> findEntities(Vec2 pos, float radius)  {
    return this.entities.values()
      .stream()
      .filter(e -> {
        if (e.getCollisionBox().isEmpty()) {
          return false;
        }
        
        FloatRectangle box = e.getCollisionBox().get().asRect();
        Vec2 topLeft = box.getTopLeftCorner();
        Vec2 bottomRight = box.getBottomRightCorner();
        Vec2 bottomLeft = new Vec2(topLeft.x(), bottomRight.y());
        Vec2 topRight = new Vec2(bottomRight.x(), topLeft.y());
        
        // Disini hanya adalah matematika "sederhana" menggunakan
        // pythogoras (didalam method magnitude), Pertama kita
        // mencari jarak dari 0, 0 yaitu posisi sekarang minus
        // posisi yang ingin dicek. Lalu memanggil method 'magnitude'
        // untuk mendapatkan jarak yang dibanding oleh 'radius'
        //
        // Langkah tersebut dilakukan 5 kali untuk
        // 1. posisi entity nya
        // 2. titik kiri atas
        // 3. titik kanan atas
        // 4. titik kiri bawah
        // 5. titik kanan bawah
        
        if (topLeft.sub(pos).magnitude() <= radius) {
          return true;
        } else if (topRight.sub(pos).magnitude() <= radius) {
          return true;
        } else if (bottomLeft.sub(pos).magnitude() <= radius) {
          return true;
        } else if (bottomRight.sub(pos).magnitude() <= radius) {
          return true;
        } else if (e.getPos().sub(pos).magnitude() <= radius) {
          return true;
        }
        
        return false;
      });
  }
  
  public abstract Vec2 getWorldSpawnPoint();
}
