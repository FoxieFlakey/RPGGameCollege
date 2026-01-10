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

public class Game implements AutoCloseable {
  private boolean isRunning = false;
  
  // Double buffer stuffs
  private int currentWidth = 0;
  private int currentHeight = 0;
  private BufferedImage buffer = null;
  private boolean doubleBuffer = false;
  
  // Render scaling (essentially what the size the game actually
  // rendering at which is window size x render scale)
  private float renderScale = 1.0f;

  private final Window window;

  private final WorldManager worldManager = new WorldManager();
  private final InputToControllerBridge player;
  private final TextureManager textureManager = new TextureManager();
  
  private static final float VIEW_WIDTH = 1280.0f;
  private static final float VIEW_HEIGHT = 720.0f;
  
  private static final int INITIAL_RENDER_WIDTH = 1280;
  private static final int INITIAL_RENDER_HEIGHT = 720;
  
  private static final float SPAWN_COOLDOWN = 0.3f;
  
  private float lastRenderTime = Util.getTime();
  private float gameTime = 0.0f;
  private boolean debugEnabled = false;
  private float spawnCatCooldown = -1.0f;
  private Screen currentScreen;
  
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
    
    ArcherCharacter playerEntity = new ArcherCharacter(this);
    overworld.addEntity(playerEntity);
    playerEntity.setPos(new Vec2(-500.0f, 300.0f));
    
    CatEntity catEntity = new CatEntity(this);
    overworld.addEntity(catEntity);
    catEntity.setPos(new Vec2(-300.0f, 300.0f));
    
    this.currentScreen = new InGame(this);
    this.player = new InputToControllerBridge(catEntity, new Vec2(Game.VIEW_WIDTH, Game.VIEW_HEIGHT), new Vec2(this.window.getRenderWidth(), this.window.getRenderHeight()));
    
    // Tick the game once to stabilize things
    this.runOnce();
    this.currentScreen = new HelpScreen(this, this.currentScreen);
  }
  
  void updateState() {
    this.window.updateState();
    this.updateCamera();
  }
  
  void updateCamera() {
    this.getCamera().setOutputSize(new Vec2(this.getOutputWidth(), this.getOutputHeight()));
  }
  
  public void respawn(Entity respawnedAs) {
    this.getCurrentWorld().addEntity(respawnedAs);
    respawnedAs.setPos(this.getCurrentWorld().getWorldSpawnPoint());
    
    this.player.setNewEntityToControl(respawnedAs);
    this.currentScreen = new InGame(this);
    
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
  
  public int getUnscaledOutputHeight() {
    return this.window.getRenderHeight();
  }
  
  public int getUnscaledOutputWidth() {
    return this.window.getRenderWidth();
  }
  
  public Keyboard getKeyboard() {
    return this.window.keyboard;
  }
  
  public Mouse getMouse() {
    return this.window.mouse;
  }
  
  public WorldManager getWorldManager() {
    return this.worldManager;
  }
  
  public float getGameTime() {
    return this.gameTime;
  }

  public void runOnce() {
    if (this.isRunning) {
      throw new IllegalStateException("Cannot run game inside running game");
    }
    this.isRunning = true;

    this.updateState();

    float now = Util.getTime();
    float deltaTime = now - this.lastRenderTime;
    this.lastRenderTime = now;

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
      throw new IllegalArgumentException("scale must be above 0.1");
    }
    this.renderScale = scale;
    this.doubleBuffer = true;
    
    this.updateCamera();
  }
  
  public float getRenderScale() {
    return this.renderScale;
  }
  
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

    Vec2 spawnPos = maybeLiving.map(e -> e.getLegPos())
        .orElseGet(() -> {
          return maybeEntity.map(e -> e.getPos())
            .orElseGet(() -> new Vec2(0.0f));
        });
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
    
    if (mouse.getButtonState(Mouse.Button.Left).isNowPressed() && this.spawnCatCooldown < 0.0f) {
      this.spawnCatCooldown = 0.1f;
      
      // Spawn cat
      CatEntity cat = new CatEntity(this.player.getWorld().getGame());
      this.player.getWorld().addEntity(cat);
      cat.setPos(camera.translateScreenToWorldCoord(mouse.getMousePosition()));
    }
    
    if (this.getMouse().getButtonState(Mouse.Button.Middle) == State.Clicked) {
      Vec2 selectedPoint = this.getCamera().translateScreenToWorldCoord(this.getMouse().getMousePosition());
      
      // Control other entity lol
      Iterator<Entity> eligibleEntities = this.getCurrentWorld()
        .findEntitiesOverlaps(selectedPoint)
        .filter(e -> e.canBeControlled())
        .iterator();
      
      if (eligibleEntities.hasNext()) {
        Entity entity = eligibleEntities.next();
        this.player.setNewEntityToControl(entity);
      }
    }
  }

  void handleInput(float deltaTime) {
    if (this.getKeyboard().getState(Keyboard.Button.Minus) == State.Clicked) {
      this.setRenderScale(Float.max(0.1f, this.renderScale - 0.1f));
    }
    if (this.getKeyboard().getState(Keyboard.Button.Equal) == State.Clicked) {
      this.setRenderScale(Float.min(3.0f, this.renderScale + 0.1f));
    }
    
    if (this.getKeyboard().getState(Keyboard.Button.F3) == State.Clicked) {
      this.debugEnabled = !this.debugEnabled;
    }
    
    if (this.getKeyboard().getState(Keyboard.Button.F11) == State.Clicked) {
      this.window.toggleFullscreen();
    }
    
    // Input is consumed by screen
    if (!this.getScreen().handleInput()) {
      return;
    }
    
    if (this.getKeyboard().getState(Keyboard.Button.F1) == State.Clicked) {
      this.currentScreen = new HelpScreen(this, this.currentScreen);
      return;
    }
    
    if (this.isDebugEnabled()) {
      this.handleDebugInputs(deltaTime);
    }
    
    this.player.handleInput(deltaTime);
  }
  
  void translateAndClipGraphics2D(Graphics2D g) {
    FloatRectangle outputAreaInWindow = this.window.getOutputArea();
    
    // Clear posibly empty left
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
    g.setClip(
      (int) outputAreaInWindow.getTopLeftCorner().x(),
      (int) outputAreaInWindow.getTopLeftCorner().y(),
      (int) outputAreaInWindow.getSize().x(),
      (int) outputAreaInWindow.getSize().y()
    );
    
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
        this.currentWidth = width;
        this.currentHeight = height;
        this.buffer = new BufferedImage(this.currentWidth, this.currentHeight, BufferedImage.TYPE_INT_RGB);
      }
      
      Graphics2D g = this.buffer.createGraphics();
      try {
        this.renderContent(g, deltaTime);
      } finally {
        g.dispose();
      }
      
      Graphics g2 = this.window.window.getGraphics();
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
      Graphics g = this.window.window.getGraphics();
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
    this.getWorldManager().tick(deltaTime);
    
    boolean isPlayerDead = this.getPlayer().isEmpty();
    Optional<DamageSource> deathReason = Optional.empty();
    if (this.getPlayer().isPresent() && this.getPlayer().get() instanceof LivingEntity) {
      LivingEntity player = (LivingEntity) this.getPlayer().get();
      isPlayerDead = player.isDead();
      deathReason = player.getDeathReason();
    }
    
    if (isPlayerDead) {
      // Player is dead
      this.currentScreen = new DeathScreen(this, deathReason);
    }
    
    gameTime += deltaTime;
  }
}
