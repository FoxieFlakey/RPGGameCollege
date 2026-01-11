package foxie.rpg_college.entity;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import foxie.rpg_college.Bar;
import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Orientation;
import foxie.rpg_college.Util;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.controller.Controller;
import foxie.rpg_college.tile.Tile;
import foxie.rpg_college.world.World;

// Kelas dasar yang memiliki method-method yang diperlukan
// untuk semua entity
public abstract class Entity {
  // Position would be, at center of hitbox/collisionbox
  // of the entity
  // ---------------------------------------------------
  // Posisi di anggap ditengah kotak tabrakan

  private Vec2 position = new Vec2(0.0f, 0.0f);
  private World currentWorld = null;
  // 0.0f derajat menhadap ke atas, tetapi ingin secara default menghadap
  // ke kanan
  private float rotation = 90.0f;
  // Controller yang sudah dibuat, disimpan disini. Untuk agar digunaka kembali
  private Optional<Controller> controller = Optional.empty();
  // Nama dari entity yang sudah diset
  private String name;
  
  // Entity can also store arbitrary extra data for outside classes
  // to use to store per entity data. Like for example portal tile
  // can store data relating entity's last position and such
  //
  // The key is any arbitrary object, but the object should be
  // unique per user or be careful not accidentally use it 
  // and clash with unrelated objects -w-
  //
  // Best approach is do "new Object()" and save the new object as
  // the key, it doesnt matter the content of it
  private final HashMap<Object, Object> extraData = new HashMap<>();
  
  // Menyimpan bar-bar yang berisi berbagai informasi
  // di kelas dasar, tidak ada bar apa-apa. Kalau di beberapa
  // kelas seperti LivingEntity ada isinya seperti HP nya
  // berapa dan diupdate sesuai HP sebenarnya.
  // Kalau di CharacterEntity dan anak-anaknya ada bar untuk
  // mana UwU
  private final ArrayList<Bar> bars = new ArrayList<>();
  private final Game game;
  
  // ID unik untuk setiap entity yang pernah dibuat
  // dan tidak bisa diedit, Meow meow!
  public final long id;

  // Melacak ID-ID untuk entity, menggunakan atomic agar perubahan
  // dilakukan secara atomic (antara berhasil diubah atau tidak sama
  // sekali)
  //
  // Atomic adalah salah satu primitive untuk multithreading untuk
  // memudahkan menset value dan update, walaupun cukup terbatas
  // apa yang bisa dilakukan. Tetapi ini sangat lebih efisien dibanding
  // menggunakan locks atau statement synchronized. tetapi lebih susah
  // dilakukan
  private static final AtomicLong ID_COUNTER = new AtomicLong(0);

  public Entity(Game game) {
    this.game = game;
    // Buat ID baru, kalau sudah mencapai ID maximum
    // yang bisa disimpan di vairabel tipe long
    // jangan ditambah
    this.id = Entity.ID_COUNTER.getAndUpdate(x -> {
      if (x == Long.MAX_VALUE) {
        return Long.MAX_VALUE;
      } else {
        return x + 1;
      }
    });

    // ID yang didapat adalah Long.MAX_VALUE, yaitu counternya
    // overflow, dari ID ini unik jadi tidak bisa reset ke nol
    // Jadi hanya exception bisa dilempar -w-
    if (this.id == Long.MAX_VALUE) {
      throw new RuntimeException("Counter for entity ID overflowed!");
    }
    
    // Nama sederhana untuk kelas ini yang default
    // subkelas-subkelas dapat mengedit nama nya
    name = this.getClass().getSimpleName() + " #" + this.id;
  }

  public final Vec2 getPos() {
    return this.position;
  }

  public void setPos(Vec2 pos) {
    // Menvalidasi posisi menjadi yang valid
    this.position = this.currentWorld.validatePos(pos);

    // Update collision box jika ada
    if (this.getCollisionBox().isPresent()) {
      this.getCollisionBox().get().setPos(pos);
    }
    
    if (this.controller.isPresent()) {
      // Melakkukan event update posisi jika
      // ada controller, sehingga semua pengendali
      // dapat tau jika entity berpindah tempat
      this.controller.get().dispatchOnPositionUpdated();
    }
  }

  public final World getWorld() {
    return this.currentWorld;
  }
  
  public void addBar(Bar bar) {
    this.bars.add(Optional.of(bar).get());
  }
  
  public Collection<Bar> getBars() {
    return this.bars;
  }

  // Be careful, THIS DOES NOT add/remove
  // itself from corresponding world
  // ------------------------------------
  // 
  public void setWorld(World world) {
    this.currentWorld = world;
    if (this.controller.isPresent()) {
      this.controller.get().dispatchOnWorldChange();
    }
  }
  
  public void onCollision() {
    this.setPos(this.getCollisionBox().get().getPos());
  }

  public final float getRotation() {
    return this.rotation;
  }
  
  public void setRotation(float rotation) {
    // Normalize angle diperlukan agar variabel
    // this.rotation isinya jelas tidak ambigu
    this.rotation = Util.normalizeAngle(rotation);
  }

  public final Orientation getOrientation() {
    return Orientation.fromDegrees(this.rotation);
  }
  
  // Method ini mengambil controller kalau bisa di kontrol
  // atau empty kalau tidak, ini juga memakai controller
  // jika sudah ada kalau belum buat controller baru dengan
  // memanggil method createController
  public final Optional<Controller> getController() {
    if (!this.canBeControlled()) {
      return Optional.empty();
    }
    
    if (this.controller.isEmpty()) {
      this.controller = Optional.of(this.createController());
    }
    
    return Optional.of(this.controller.get());
  }
  
  // Method ini menentukan apakah event-event untuk
  // controller bisa di kirim ke controller seperti
  // perubahan lokasi dan lain-lain
  public final boolean canDispatchControllerEvents() {
    return this.canBeControlled() && this.controller.isPresent();
  }
  
  public final Optional<Object> getExtraData(Object key) {
    return Optional.ofNullable(this.extraData.get(key));
  }
  
  // Terakhir adalah method sama seperti atas, tetapi pemanggil
  // dapat memberi konstructor untuk membuat datanya. Jadi jika data
  // tidak ada maka buat baru dan masukkan
  public final Object getExtraDataOrInsert(Object key, Supplier<Object> constructor) {
    if (!this.extraData.containsKey(key)) {
      Object newData = constructor.get();
      this.extraData.put(key, newData);
      return newData;
    }
    
    return this.extraData.get(key);
  }
  
  public String getName() {
    return this.name;
  }
  
  public void onTileStep(Tile tile, IVec2 tileCoord) {
  }
  
  public final Game getGame() {
    return this.game;
  }
  
  // Method ini merender bar-bar yang ada
  // diatas entity
  protected void renderBars(Graphics2D g) {
    Game game = this.getWorld().getGame();
    float renderScale = game.getRenderScale();
    Vec2 barStart = this.getRenderBound()
      .map(bound -> {
        Vec2 topLeft = bound.getTopLeftCorner();
        float right = bound.getBottomRightCorner().x();
        
        return new Vec2((right + topLeft.x()) / 2.0f, topLeft.y() - (20.0f * renderScale));
      })
      .orElseGet(() -> new Vec2(0.0f, (20.0f * renderScale)));
    
    float x = barStart.x();
    float y = barStart.y();
    
    for (Bar bar : this.getBars()) {
      bar.render(renderScale, g, new Vec2(x, y));
      y -= (Bar.HEIGHT + 5.0f) * renderScale;
    }
  }
  
  // Fungsi ini mengubah render bound menjadi render bound tetapi
  // dalam koordinat dunia
  public Optional<FloatRectangle> getRenderBoundInWorld() {
    return this.getRenderBound().map(bound -> {
      Camera cam = this.getGame().getCamera();
      return new FloatRectangle(
        cam.translateAWTGraphicsToWorldCoord(bound.getTopLeftCorner()),
        cam.translateAWTGraphicsToWorldCoord(bound.getBottomRightCorner())
      );
    });
  }
  
  // Method ini memeriksa apakah kontroller sedang aktif
  // yang berarti entity sedang dikontrol :3
  public boolean isBeingControlled() {
    return this.controller.map(controller -> controller.isActive()).orElse(false);
  }

  // This prefer 'false', so if there two entities
  // one say true other say false, the result is false
  // which mean no collision happens
  public abstract boolean canCollideWith(Entity other);
  public abstract void onTileCollision(IVec2 coord, Tile other);
  public abstract void onEntityCollision(Entity other);
  public abstract void onWorldBorderCollision();
  public abstract Optional<CollisionBox> getCollisionBox();
  public abstract boolean isVisible(Camera cam);
  public abstract Optional<FloatRectangle> getRenderBound();
  public abstract void render(Graphics2D g, float deltaTime);
  public abstract void tick(float deltaTime);
  public abstract Optional<FloatRectangle> getBoxToBeCheckedForTileStep();
  public abstract boolean canBeControlled();
  protected abstract Controller createController();
}
