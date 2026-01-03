package foxie.rpg_college;

import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.Iterator;
import java.util.Optional;

import foxie.rpg_college.entity.CatEntity;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.entity.PlayerEntity;
import foxie.rpg_college.entity.controller.InputToControllerBridge;
import foxie.rpg_college.input.Keyboard;
import foxie.rpg_college.input.Mouse;
import foxie.rpg_college.input.State;
import foxie.rpg_college.tile.TileList;
import foxie.rpg_college.ui.InGame;
import foxie.rpg_college.ui.Screen;
import foxie.rpg_college.world.Overworld;
import foxie.rpg_college.world.World;

public class Game implements AutoCloseable {
  private Object lock = new Object();
  private boolean shared_isClosed = false;
  private int shared_currentRenderWidth = INITIAL_RENDER_WIDTH;
  private int shared_currentRenderHeight = INITIAL_RENDER_HEIGHT;
  private FloatRectangle shared_outputAreaInWindow;
  
  private boolean isRunning = false;
  private boolean isClosed = false;
  private int currentRenderWidth = INITIAL_RENDER_WIDTH;
  private int currentRenderHeight = INITIAL_RENDER_HEIGHT;
  private FloatRectangle outputAreaInWindow;

  private final Frame window;
  private final BufferStrategy windowBufferStrategy;

  private final Overworld overworld;
  private final InputToControllerBridge player;
  private final Screen currentScreen;
  
  private static final float VIEW_WIDTH = 1280.0f;
  private static final float VIEW_HEIGHT = 720.0f;
  
  private static final int INITIAL_RENDER_WIDTH = 1280;
  private static final int INITIAL_RENDER_HEIGHT = 200;
  
  private float lastRenderTime = Util.getTime();
  
  public final Mouse mouseState;
  public final Keyboard keyboardState;
  public final TileList TILES;
  
  public static final int TICK_RATE = 20;
  public static final int REFRESH_RATE = 30;

  public Game() {
    this.window = new Frame();
    this.window.setSize(this.getOutputWidth(), this.getOutputHeight());
    this.window.setFocusable(true);
    this.window.setUndecorated(false);
    this.window.setVisible(true);
    
    this.mouseState = new Mouse(this.window, this.outputAreaInWindow, new Vec2(Game.VIEW_WIDTH, Game.VIEW_HEIGHT));
    this.keyboardState = new Keyboard(this.window);
    
    this.window.createBufferStrategy(2);
    this.windowBufferStrategy = Optional.ofNullable(this.window.getBufferStrategy()).get();
    
    this.TILES = new TileList(this);
    this.overworld = new Overworld(this);
    
    PlayerEntity playerEntity = new PlayerEntity();
    this.overworld.addEntity(playerEntity);
    playerEntity.setPos(new Vec2(-500.0f, 300.0f));
    
    CatEntity catEntity = new CatEntity();
    this.overworld.addEntity(catEntity);
    catEntity.setPos(new Vec2(-300.0f, 300.0f));
    
    this.currentScreen = new InGame(this);
    this.player = new InputToControllerBridge(catEntity, new Vec2(Game.VIEW_WIDTH, Game.VIEW_HEIGHT), new Vec2(this.currentRenderWidth, this.currentRenderHeight));
    
    this.updateState();
    this.captureState();
    
    @SuppressWarnings("resource")
    final Game game = this;

    this.window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        synchronized (game.lock) {
          game.isClosed = true;
        }
      }
    });
    
    this.window.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        game.updateState();
      }
    });
  }
  
  void captureState() {
    synchronized (this.lock) {
      this.outputAreaInWindow = this.shared_outputAreaInWindow;
      this.currentRenderWidth = this.shared_currentRenderWidth;
      this.currentRenderHeight = this.shared_currentRenderHeight;
      this.isClosed = this.shared_isClosed;
    }
  }
  
  void updateState() {
    synchronized (this.lock) {
      FloatRectangle outputArea = this.calcOutputArea();
      this.mouseState.setWatchedArea(outputArea);
      this.shared_outputAreaInWindow = outputArea;
      this.shared_currentRenderWidth = (int) outputArea.getSize().x();
      this.shared_currentRenderHeight = (int) outputArea.getSize().y();
      this.getCamera().setOutputSize(outputArea.getSize());
    }
  }
  
  void handleRespawnCat() {
    // Player request respawn
    CatEntity entity = new CatEntity();
    this.overworld.addEntity(entity);
    this.player.setNewEntityToControl(entity);
    
    entity.setPos(new Vec2(-300.0f, 300.0f));
  }
  
  void handleRespawnPlayer() {
    // Player request respawn
    PlayerEntity entity = new PlayerEntity();
    this.overworld.addEntity(entity);
    this.player.setNewEntityToControl(entity);
    
    entity.setPos(new Vec2(-500.0f, 300.0f));
  }

  @Override
  public void close() throws Exception {
    this.windowBufferStrategy.dispose();
    this.window.dispose();
  }

  public boolean isClosed() {
    return this.isClosed;
  }

  public World getCurrentWorld() {
    return this.player.getWorld();
  }

  public Optional<Entity> getPlayer() {
    return this.player.getEntity();
  }

  public Screen getScreen() {
    return this.currentScreen;
  }

  public Camera getCamera() {
    return this.player.getCamera();
  }

  public int getOutputHeight() {
    return this.currentRenderHeight;
  }

  public int getOutputWidth() {
    return this.currentRenderWidth;
  }

  public void runOnce() {
    if (this.isRunning) {
      throw new IllegalStateException("Cannot run game inside running game");
    }
    this.isRunning = true;

    this.captureState();
    this.mouseState.updateState();
    this.keyboardState.updateState();

    float now = Util.getTime();
    float deltaTime = now - this.lastRenderTime;
    this.lastRenderTime = now;

    this.handleInput(deltaTime);
    this.tick(deltaTime);
    this.render(deltaTime);

    this.isRunning = false;
  }

  void handleInput(float deltaTime) {
    this.player.handleInput(deltaTime);
    
    if (this.getPlayer().isEmpty()) {
      if (this.keyboardState.getState(Keyboard.Button.R) == State.Clicked) {
        this.handleRespawnCat();
      } else if (this.keyboardState.getState(Keyboard.Button.T) == State.Clicked) {
        this.handleRespawnPlayer();
      }
    }
    
    if (this.mouseState.getButtonState(Mouse.Button.Middle) == State.Clicked) {
      Vec2 selectedPoint = this.getCamera().translateScreenToWorldCoord(this.mouseState.getMousePosition());
      
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
  
  FloatRectangle calcOutputArea() {
    float somethingWidth = Game.VIEW_WIDTH;
    float somethingHeight = Game.VIEW_HEIGHT;
    float somethingAspect = somethingWidth / somethingHeight;
    
    float actualWidth = (float) this.window.getWidth();
    float actualHeight = (float) this.window.getHeight();
    
    float neededWidthIfHeightIsScaledToFit = actualHeight * somethingAspect;
    
    float scale;
    if (neededWidthIfHeightIsScaledToFit > actualWidth) {
      scale = actualWidth / somethingWidth;
    } else {
      scale = actualHeight / somethingHeight;
    }
    
    float letterBoxedWidth = somethingWidth * scale;
    float letterBoxedHeight = somethingHeight * scale;
    float xOffset = Math.max((actualWidth - letterBoxedWidth) / 2.0f, 0.0f);
    float yOffset = Math.max((actualHeight - letterBoxedHeight) / 2.0f, 0.0f);
    
    return new FloatRectangle(
      new Vec2(xOffset, yOffset),
      new Vec2(xOffset + letterBoxedWidth, yOffset + letterBoxedHeight)
    );
  }

  void render(float deltaTime) {
    do {
      do {
        FloatRectangle outputAreaInWindow = this.outputAreaInWindow;
        
        Graphics2D g = (Graphics2D) this.windowBufferStrategy.getDrawGraphics();
        g.setClip(
          (int) outputAreaInWindow.getTopLeftCorner().x(),
          (int) outputAreaInWindow.getTopLeftCorner().y(),
          (int) outputAreaInWindow.getSize().x(),
          (int) outputAreaInWindow.getSize().y()
        );
        g.translate(outputAreaInWindow.getTopLeftCorner().x(), outputAreaInWindow.getTopLeftCorner().y());
        
        try {
          this.getCurrentWorld().render(g, deltaTime);
          this.currentScreen.render(g, deltaTime);
        } finally {
          g.dispose();
        }
        g.dispose();
      } while (this.windowBufferStrategy.contentsRestored());

      this.windowBufferStrategy.show();
    } while (this.windowBufferStrategy.contentsLost());
  }

  void tick(float deltaTime) {
    // Tick the world and stuffs :3
    this.getCurrentWorld().tick(deltaTime);
  }
}
