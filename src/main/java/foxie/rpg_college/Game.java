package foxie.rpg_college;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Optional;

import foxie.rpg_college.entity.CatEntity;
import foxie.rpg_college.entity.DummyLivingEntity;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.entity.LivingEntity;
import foxie.rpg_college.entity.MageCharacter;
import foxie.rpg_college.entity.TurretEntity;
import foxie.rpg_college.entity.WarriorCharacter;
import foxie.rpg_college.entity.ArcherCharacter;
import foxie.rpg_college.entity.controller.InputToControllerBridge;
import foxie.rpg_college.entity.damage.DamageSource;
import foxie.rpg_college.input.Keyboard;
import foxie.rpg_college.input.Keyboard.Button;
import foxie.rpg_college.input.Mouse;
import foxie.rpg_college.input.State;
import foxie.rpg_college.texture.TextureManager;
import foxie.rpg_college.tile.TileList;
import foxie.rpg_college.ui.DeathScreen;
import foxie.rpg_college.ui.HelpScreen;
import foxie.rpg_college.ui.InGame;
import foxie.rpg_college.ui.Screen;
import foxie.rpg_college.world.BattleArena;
import foxie.rpg_college.world.Overworld;
import foxie.rpg_college.world.World;

// Kelas game, kelas ini menggabung banyak hal
// dan menjadi penyimpan utama dari berbagai bagian
// dari game, dimulai dari rendering, manajemen dunia,
// manajeman input/output, menghandle fullscreen/etc, etc
//
// Ininya kelas Game menjadi orchestrator dalam game :3
// Seperti analoginya konduktor dalam pertunjukkan
// musik. Mengatur dan mengarahkan banyak hal berbeda-beda
//
// Karena banyak hal-hal yang ingin di ikat dan di atur
// kelas ini memang besar, tidak bisa dipisah lagi karena
// there too many functions and splitting it is unnecessary
// cuz there always a need for a single class that
// manages other things -w-
public class Game implements AutoCloseable {
  // Boolean untuk memeriksa apakah game
  // sedang berjalan di method Game#runOnce
  // Itu diperlukan agar menghindar tidak sengaja
  // memanggil method it selama game sedang
  // berjalan
  private boolean isRunning = false;
  
  // Double buffer stuffs
  // -----------------------
  // Ini penting untuk fungsi double buffering
  // manual untuk menghindar screen tearing
  private int currentWidth = 0;
  private int currentHeight = 0;
  private BufferedImage buffer = null;
  private boolean doubleBuffer = false;
  
  // Render scaling (essentially what the size the game actually
  // rendering at which is window size x render scale)
  // -------------------------------------------------------------
  // Render scale adalah skala output game dibanding ukuran
  // window, ini mengizinkan program merender di resolusi
  // contohnnya 50% lebih rendah dibanding output sebenarnya
  // salah satu kegunaan nya adalah mungkin komputer tidak
  // sanggup merender atau menjalankan game karena memang
  // banyak hal berjalan di satu thread dan masih belum tau
  // mengapa proses menggambar Graphics2D's Java menggunakan
  // CPU.
  //
  // Kalau berjalan di versi web, web dapat meberikan skala
  // seperti 30% untuk merendahkan beban. web lagi pula
  // bukan desktop semuanya di emulasi :3
  private float renderScale = 1.0f;

  private final Window window;

  private final WorldManager worldManager = new WorldManager();
  private final InputToControllerBridge player;
  private final TextureManager textureManager = new TextureManager();
  
  // Konstanta untuk ukuran sebagai referensi untuk berbagai
  // kode untuk sizenya. Logika game dan render berjalan
  // seolah-olah outputnya ke 1280x720 tetapi sebenarnya kelas
  // input, output, camera, etc otomatis mengubahnya menjadi
  // output sebenarnya. Dari pada render fixed ke buffer sebesar
  // 1280x720, game dapat render pada resolusi native tetapi
  // logika/unit ukuran tetap berjalan seolah-olah di render
  // pakai layar 1280x720
  private static final float VIEW_WIDTH = 1280.0f;
  private static final float VIEW_HEIGHT = 720.0f;
  
  // Ukuran default dan minimum untuk window
  private static final int INITIAL_RENDER_WIDTH = 1280;
  private static final int INITIAL_RENDER_HEIGHT = 720;
  
  // Seberapa lama seblum entity baru bisa dispawn
  // di mode debug
  private static final float SPAWN_COOLDOWN = 0.3f;
  
  // Menyimpan waktu terakhir render agar dapat
  // menghitung deltaTime
  private float lastRenderTime = Util.getTime();
  // Melacak berapa detik game berjalan
  private float gameTime = 0.0f;
  // apakah mode debug hidup
  private boolean debugEnabled = false;
  // cooldown untuk spawn entity pada mode debug
  // agar tidak menspam tidak sengaja
  private float spawnCatCooldown = -1.0f;
  // Layar sekarang lagi aktif
  private Screen currentScreen;
  
  // Menyimpan list tiles yang ada dalam game
  // dan ini konstanta karena tidak berubah
  public final TileList TILES;
  
  public static final int TICK_RATE = 20;
  public static final int REFRESH_RATE = 30;

  public Game() {
    this.window = new Window(
      new IVec2(Game.INITIAL_RENDER_WIDTH, Game.INITIAL_RENDER_HEIGHT),
      new IVec2(Game.INITIAL_RENDER_WIDTH, Game.INITIAL_RENDER_HEIGHT),
      new Vec2(Game.VIEW_WIDTH, Game.VIEW_HEIGHT),
      VIEW_WIDTH / VIEW_HEIGHT
    );
    
    // Load textures
    this.textureManager.addTexture("character/archer/dead", "/archer_dead.png");
    this.textureManager.addTexture("character/archer/facing_down", "/archer_facing_down.png");
    this.textureManager.addTexture("entity/arrow", "/arrow.png");
    this.textureManager.addTexture("entity/sword", "/sword.png");
    this.textureManager.addTexture("entity/fireball", "/fireball.png");
    this.textureManager.addTexture("entity/turret/dead", "/turret_dead.png");
    this.textureManager.addTexture("entity/turret/ready", "/turret_ready.png");
    this.textureManager.addTexture("entity/turret/not_ready", "/turret_not_ready.png");
    this.textureManager.addTexture("world/battle_arena/background", "/battle_arena.png");
    this.textureManager.addTexture("world/overworld/background", "/world.png");
    this.textureManager.addTexture("ui/in_game/corner_top_left", "/corner_top_left.png");
    this.textureManager.addTexture("ui/in_game/corner_top_right", "/corner_top_right.png");
    this.textureManager.addTexture("ui/in_game/corner_bottom_left", "/corner_bottom_left.png");
    this.textureManager.addTexture("ui/in_game/corner_bottom_right", "/corner_bottom_right.png");
    
    this.textureManager.loadAll();
    
    this.TILES = new TileList(this);
    Overworld overworld = new Overworld(this);
    this.worldManager.addWorld(WorldManager.OVERWORLD_ID, overworld);
    this.worldManager.addWorld(WorldManager.BATTLE_ARENA_ID, new BattleArena(this));
    
    // Menambahkan beberapa entity basic
    // ini juga kode yang lama ada sejak
    // awal, hanya berubah bentuk saja
    ArcherCharacter playerEntity = new ArcherCharacter(this);
    overworld.addEntity(playerEntity);
    playerEntity.setPos(new Vec2(-500.0f, 300.0f));
    
    // Ini juga sama, ditambahkan saat ingin
    // mentest collision dan pengendalian
    // entity lain
    CatEntity catEntity = new CatEntity(this);
    overworld.addEntity(catEntity);
    catEntity.setPos(new Vec2(-300.0f, 300.0f));
    
    // Setelah itu buat layar baru yang menjadi menampilan
    // game
    this.currentScreen = new InGame(this);

    // Lalu memasukkan pengendali yang dari awal
    // mengendalikan kucing
    this.player = new InputToControllerBridge(catEntity, new Vec2(Game.VIEW_WIDTH, Game.VIEW_HEIGHT), new Vec2(this.window.getRenderWidth(), this.window.getRenderHeight()));
    
    // Tick the game once to stabilize things
    // --------------------------------------
    // Menjalankan game sekali untuk menstabil
    // hal
    this.runOnce();

    // Lalu buka help screen
    // sehingga user dapat
    // mengetahui input-input yang ada
    this.currentScreen = new HelpScreen(this, this.currentScreen);
  }
  
  // Mengupdate keadaan window sama camera
  // agar dapat melihat hal yant telah diupdate
  void updateState() {
    this.window.updateState();
    this.updateCamera();
  }
  
  void updateCamera() {
    this.getCamera().setOutputSize(new Vec2(this.getOutputWidth(), this.getOutputHeight()));
  }
  
  // Meminta player untuk respawn sebagai
  // entity iini yang masih belum ditambahkan
  // ke dunia
  public void respawn(Entity respawnedAs) {
    // tambahkan entity nya lalu set lokasinya
    // ke spawn point dunia
    this.getCurrentWorld().addEntity(respawnedAs);
    respawnedAs.setPos(this.getCurrentWorld().getWorldSpawnPoint());
    
    // Lalu mengganti entity jadi yang dikendalikan
    // oleh player
    this.player.setNewEntityToControl(respawnedAs);

    // Lalu kembali ke layar game
    this.currentScreen = new InGame(this);
    
    // Jika entitynya hidup, buat entity kedip
    // sekali
    if (respawnedAs instanceof LivingEntity) {
      ((LivingEntity) respawnedAs).flash();
    }
  }
  
  @Override
  public void close() throws Exception {
    this.window.close();
  }

  public boolean isClosed() {
    return this.window.isClosed();
  }

  public World getCurrentWorld() {
    return this.player.getWorld();
  }

  public Optional<Entity> getPlayer() {
    return this.player.getEntity();
  }

  public void setScreen(Screen newScreen) {
    this.currentScreen = newScreen;
  }
  
  public Screen getScreen() {
    return this.currentScreen;
  }
  
  public TextureManager getTextureManager() {
    return this.textureManager;
  }

  public Camera getCamera() {
    return this.player.getCamera();
  }

  public int getOutputHeight() {
    return (int) Float.max((float) this.window.getRenderHeight() * this.renderScale, Game.VIEW_HEIGHT * 0.05f);
  }

  public int getOutputWidth() {
    return (int) Float.max((float) this.window.getRenderWidth() * this.renderScale, Game.VIEW_WIDTH * 0.05f);
  }
  
  // Method ini mengambil output yang utuh
  // langsung dari window, sesuai dengan area
  // yang ditampilkan setelah diadjust oleh
  // algoritma letter boxing
  public int getUnscaledOutputHeight() {
    return this.window.getRenderHeight();
  }
  
  public int getUnscaledOutputWidth() {
    return this.window.getRenderWidth();
  }
  
  public Keyboard getKeyboard() {
    return this.window.getKeyboard();
  }
  
  public Mouse getMouse() {
    return this.window.getMouse();
  }
  
  public WorldManager getWorldManager() {
    return this.worldManager;
  }
  
  public float getGameTime() {
    return this.gameTime;
  }

  // Menjalankan logika game
  // sekali
  public void runOnce() {
    if (this.isRunning) {
      throw new IllegalStateException("Cannot run game inside running game");
    }
    this.isRunning = true;

    // Pertama updae statemnya
    this.updateState();

    // Mengambil waktu sekarang lalu mendapatkan
    // deltaTime dan update lastRenderTime
    float now = Util.getTime();
    float deltaTime = now - this.lastRenderTime;
    this.lastRenderTime = now;

    // Lalu lakukan hal berikut dalam urutan ini
    // tangani input user, lalu jalankan logika sekali
    // akhirnya rende routputnya :3
    this.handleInput(deltaTime);
    this.tick(deltaTime);
    this.render(deltaTime);

    this.isRunning = false;
  }
  
  public boolean isDebugEnabled() {
    return this.debugEnabled;
  }
  
  public void setDoubleBuffer(boolean val) {
    this.doubleBuffer = val;
  }
  
  public void setFullscreen(boolean val) {
    this.window.setFullscreen(val);
  }
  
  public void setRenderScale(float scale) {
    if (scale < 0.1f) {
      // Skala secara sengaja dibatasi paling
      // kecil 10%, agar angka-angka tidak menjadi
      // terlalu kecil dan men glitch game.
      // Lagi pula 10% hampir tidak ada apa-apa untuk
      // dilahat -w-
      throw new IllegalArgumentException("scale must be above 0.1");
    }

    // Setting skala juga memerlukan double
    // buffering, karena memang butuh dua
    // satu yang kecil/besar sesuai skala
    // buffer kedua adalah outputnya
    this.renderScale = scale;
    this.doubleBuffer = true;
    
    // Lalu jangan lupa update kamera juga, agar
    // sesuai dengan ukuran output baru
    this.updateCamera();
  }
  
  public float getRenderScale() {
    return this.renderScale;
  }
  
  // handle input-input untuk mode debug
  void handleDebugInputs(float deltaTime) {
    Camera camera = this.getCamera();
    Keyboard keyboard = this.getKeyboard();
    Mouse mouse = this.getMouse();
    
    Optional<Entity> maybeEntity = this.player.getEntity();
    Optional<LivingEntity> maybeLiving = this.player.getLivingEntity();
    
    this.spawnCatCooldown -= deltaTime;
    if (this.spawnCatCooldown < 0.0f) {
      this.spawnCatCooldown = -1.0f;
    }

    // Method Optional#map mengubah value dalamnya
    // dari satu ke lain, disini kita mencoba mengambil
    // posisi kaki kalau hidup atau mengambil posisi
    // entity nya sendiri kalau mati. Kalau tidak ada
    // entity sama sekali, default nya ke 0, 0 titiknya
    Vec2 spawnPos = maybeLiving.map(e -> e.getLegPos())
        .orElseGet(() -> {
          return maybeEntity.map(e -> e.getPos())
            .orElseGet(() -> new Vec2(0.0f));
        });
    
    // Kode-kode berikut banyak duplikat saja untuk menspawn
    // berbagai jenis yang ditulis sebagai berikut
    //
    // C => Kucing
    // V => Archer
    // B => Mage
    // N => Turret
    // M => Dummy living entity (HP tidak terbatas
    //   untuk jadi boneka test saja untuk attack)
    // , => Spawn warrior
    if (keyboard.getState(Button.C).isNowPressed() && this.spawnCatCooldown < 0.0f) {
      this.spawnCatCooldown = SPAWN_COOLDOWN;
      
      // Spawn cat
      CatEntity cat = new CatEntity(this.player.getWorld().getGame());
      this.player.getWorld().addEntity(cat);
      cat.setPos(spawnPos);
    }
    
    if (keyboard.getState(Button.V).isNowPressed() && this.spawnCatCooldown < 0.0f) {
      this.spawnCatCooldown = SPAWN_COOLDOWN;
      
      // Spawn cat
      ArcherCharacter archer = new ArcherCharacter(this.player.getWorld().getGame());
      this.player.getWorld().addEntity(archer);
      archer.setPos(spawnPos);
    }
    
    if (keyboard.getState(Button.B).isNowPressed() && this.spawnCatCooldown < 0.0f) {
      this.spawnCatCooldown = SPAWN_COOLDOWN;
      
      MageCharacter mage = new MageCharacter(this.player.getWorld().getGame());
      this.player.getWorld().addEntity(mage);
      mage.setPos(spawnPos);
    }
    
    if (keyboard.getState(Button.N).isNowPressed() && this.spawnCatCooldown < 0.0f) {
      this.spawnCatCooldown = SPAWN_COOLDOWN;
      
      TurretEntity turret = new TurretEntity(this.player.getWorld().getGame());
      this.player.getWorld().addEntity(turret);
      turret.setPos(spawnPos);
    }
    
    if (keyboard.getState(Button.M).isNowPressed() && this.spawnCatCooldown < 0.0f) {
      this.spawnCatCooldown = SPAWN_COOLDOWN;
      
      DummyLivingEntity turret = new DummyLivingEntity(this.player.getWorld().getGame());
      this.player.getWorld().addEntity(turret);
      turret.setPos(spawnPos);
    }
    
    if (keyboard.getState(Button.Comma).isNowPressed() && this.spawnCatCooldown < 0.0f) {
      this.spawnCatCooldown = SPAWN_COOLDOWN;
     
      WarriorCharacter turret = new WarriorCharacter(this.player.getWorld().getGame());
      this.player.getWorld().addEntity(turret);
      turret.setPos(spawnPos);
    }
    
    // Menspawn kucing di posisi dimana mouse diklik
    if (mouse.getButtonState(Mouse.Button.Left).isNowPressed() && this.spawnCatCooldown < 0.0f) {
      this.spawnCatCooldown = 0.1f;
      
      // Spawn cat
      CatEntity cat = new CatEntity(this.player.getWorld().getGame());
      this.player.getWorld().addEntity(cat);

      // pertama mengambil posisi mouse nya dan translate dari layar
      // ke koordinat dunia melalui kamera sehingga spawnnya tepat
      // dibawah kursor kurang lebih
      cat.setPos(camera.translateScreenToWorldCoord(mouse.getMousePosition()));
    }
    
    // Kalau klik tengah mengendalikan entity yang dibawah
    // kursor
    if (this.getMouse().getButtonState(Mouse.Button.Middle) == State.Clicked) {
      Vec2 selectedPoint = this.getCamera().translateScreenToWorldCoord(this.getMouse().getMousePosition());
      
      // Control other entity lol
      Iterator<Entity> eligibleEntities = this.getCurrentWorld()
        .findEntitiesOverlaps(selectedPoint)
        // Hanya memerlukan entity yang dapat dikontrol
        .filter(e -> e.canBeControlled())
        .iterator();
      
      if (eligibleEntities.hasNext()) {
        // Kita mengambil hasil pertama jika ada untuk dikontrol:3
        Entity entity = eligibleEntities.next();
        this.player.setNewEntityToControl(entity);
      }
    }
  }

  void handleInput(float deltaTime) {
    if (this.getKeyboard().getState(Keyboard.Button.Minus) == State.Clicked) {
      // Jika tombol minus diklik kurangi skala rendernya
      this.setRenderScale(Float.max(0.1f, this.renderScale - 0.1f));
    }
    if (this.getKeyboard().getState(Keyboard.Button.Equal) == State.Clicked) {
      // Jika tombol sama dengan diklik naikan skala rendernya
      this.setRenderScale(Float.min(3.0f, this.renderScale + 0.1f));
    }
    
    if (this.getKeyboard().getState(Keyboard.Button.F3) == State.Clicked) {
      // Kalau F3, toggle mode debug
      this.debugEnabled = !this.debugEnabled;
    }
    
    if (this.getKeyboard().getState(Keyboard.Button.F11) == State.Clicked) {
      // Toggle fullscreen untuk game nya
      this.window.toggleFullscreen();
    }
    
    // Input is consumed by screen
    // -----------------------------
    // Coba tangani input yang mungkin layar perlu seblum input-input lain
    if (!this.getScreen().handleInput()) {
      // Layar meminta input tidak dilanjutkan jadi return disini
      return;
    }
    
    if (this.getKeyboard().getState(Keyboard.Button.F1) == State.Clicked) {
      // Menampilkan layar help dengan F1
      this.currentScreen = new HelpScreen(this, this.currentScreen);
      return;
    }
    
    if (this.isDebugEnabled()) {
      // kalau dalam mode debug, tangani input untuk debug
      this.handleDebugInputs(deltaTime);
    }
    
    // Lalu handle input-input untuk pergerakan
    this.player.handleInput(deltaTime);
  }
  
  // Ini mengambil Graphics2D lalu mengubahnya
  // sehingga koordinat 0, 0 tepat diarea outputnya
  //
  // Digunakan untuk mode yang tidak double buffering
  void translateAndClipGraphics2D(Graphics2D g) {
    FloatRectangle outputAreaInWindow = this.window.getOutputArea();
    
    // Clear posibly empty left
    // -------------------------
    // Sisanya menghandle untuk membersihkan pixel-pixel disisi
    // yang kosong setelah letterboxing
    int left = (int) outputAreaInWindow.getTopLeftCorner().x();
    int right = (int) outputAreaInWindow.getBottomRightCorner().x();
    int top = (int) outputAreaInWindow.getTopLeftCorner().y();
    int bottom = (int) outputAreaInWindow.getBottomRightCorner().y();
    g.setColor(Color.BLACK);
    g.fillRect(
      0,
      0,
      left,
      bottom
    );
    
    // Clear posibly empty right
    g.setColor(Color.BLACK);
    g.fillRect(
      right,
      0,
      this.window.getWindowWidth() - right,
      bottom
    );
    
    // Clear posibly empty top
    g.setColor(Color.BLACK);
    g.fillRect(
      0,
      0,
      this.window.getWindowWidth(),
      top
    );
    
    // Clear posibly empty bottom
    g.setColor(Color.BLACK);
    g.fillRect(
      0,
      bottom,
      this.window.getWindowWidth(),
      this.window.getWindowHeight() - bottom
    );
    
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    // setelah itu diklip jadi bagian yang di tampilkan hanya di dalam output yang benar
    g.setClip(
      (int) outputAreaInWindow.getTopLeftCorner().x(),
      (int) outputAreaInWindow.getTopLeftCorner().y(),
      (int) outputAreaInWindow.getSize().x(),
      (int) outputAreaInWindow.getSize().y()
    );
    
    // Setelah itu mentranslate sehingga 0,0 letaknya benar di
    // output
    g.translate((int) outputAreaInWindow.getTopLeftCorner().x(), (int) outputAreaInWindow.getTopLeftCorner().y());
  }
  
  void renderContent(Graphics2D g, float deltaTime) {
    this.getCurrentWorld().render(g, deltaTime);
    this.currentScreen.render(g, deltaTime);
  }
  
  void render(float deltaTime) {
    if (this.doubleBuffer) {
      int width = this.getOutputWidth();
      int height = this.getOutputHeight();
      
      if (this.currentWidth != width || this.currentHeight != height) {
        // Jika ukuran output nya berubah, buat buffer baru
        // kalau tidak pakai buffer yang sama
        this.currentWidth = width;
        this.currentHeight = height;
        this.buffer = new BufferedImage(this.currentWidth, this.currentHeight, BufferedImage.TYPE_INT_RGB);
      }
      
      Graphics2D g = this.buffer.createGraphics();
      try {
        // Render game kedalam buffer image
        this.renderContent(g, deltaTime);
      } finally {
        g.dispose();
      }
      
      // Setelah itu akhirnya dicopy ke hasil windownya
      Graphics g2 = this.window.getWindow().getGraphics();
      try {
        this.translateAndClipGraphics2D((Graphics2D) g2);
        g2.drawImage(
          this.buffer,
          0,
          0,
          this.window.getRenderWidth(),
          this.window.getRenderHeight(),
          0,
          0,
          this.currentWidth,
          this.currentHeight,
          null
        );
      } finally {
        g2.dispose();
      }
    } else {
      // Mode tidak ada double buffering
      // mode ini mengasumsi kalau getGraphics untuk window
      // sebuah instance dari Graphics2D, dan metode ini
      // sangat rentan dengan screen tearing atau flickering

      Graphics g = this.window.getWindow().getGraphics();
      try {
        this.translateAndClipGraphics2D((Graphics2D) g);
        this.renderContent((Graphics2D) g, deltaTime);
      } finally {
        g.dispose();
      }
    }
  }

  void tick(float deltaTime) {
    this.getScreen().tick(deltaTime);
    if (!this.getScreen().canTickGame()) {
      return;
    }
    
    // Tick the world and stuffs :3
    // ----------------------------
    // Majukan simulasi dunia :3
    this.getWorldManager().tick(deltaTime);
    
    boolean isPlayerDead = this.getPlayer().isEmpty();
    Optional<DamageSource> deathReason = Optional.empty();
    if (this.getPlayer().isPresent() && this.getPlayer().get() instanceof LivingEntity) {
      // Dapatkan value untuk apakah player mati kalau bisa juga alasan mati nya
      LivingEntity player = (LivingEntity) this.getPlayer().get();
      isPlayerDead = player.isDead();
      deathReason = player.getDeathReason();
    }
    
    if (isPlayerDead) {
      // Player is dead
      // ----------------
      // Player mati jadi tampilkan layar mati
      this.currentScreen = new DeathScreen(this, deathReason);
    }
    
    gameTime += deltaTime;
  }
}
