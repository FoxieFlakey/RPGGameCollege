package foxie.rpg_college;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Optional;

import foxie.rpg_college.entity.CatEntity;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.entity.PlayerEntity;
import foxie.rpg_college.entity.controller.InputToControllerBridge;
import foxie.rpg_college.input.Keyboard;
import foxie.rpg_college.input.Mouse;
import foxie.rpg_college.tile.TileList;
import foxie.rpg_college.ui.InGame;
import foxie.rpg_college.ui.Screen;
import foxie.rpg_college.world.Overworld;
import foxie.rpg_college.world.World;

public class Game implements AutoCloseable {
  private boolean isRunning = false;
  private boolean isClosed = false;

  private final Frame window;
  private final BufferStrategy windowBufferStrategy;

  private final BufferedImage gameBuffer = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
  private final Overworld overworld;
  private final InputToControllerBridge player;
  private final Screen currentScreen;
  
  private float lastRenderTime = Util.getTime();
  
  public final Mouse mouseState;
  public final Keyboard keyboardState;
  public final TileList TILES;
  
  public FloatRectangle outputAreaInWindow;

  public static final int TICK_RATE = 20;
  public static final int REFRESH_RATE = 30;

  public Game() {
    this.window = new Frame();
    this.window.setSize(this.getOutputWidth(), this.getOutputHeight());
    this.window.setFocusable(true);
    this.window.setUndecorated(false);
    this.window.setVisible(true);
    this.outputAreaInWindow = this.calcOutputArea();

    @SuppressWarnings("resource")
    final Game game = this;

    this.window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        game.isClosed = true;
      }
    });
    
    this.window.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        game.outputAreaInWindow = game.calcOutputArea();
        game.mouseState.setWatchedArea(game.outputAreaInWindow);
      }
    });

    this.mouseState = new Mouse(this.window, this.outputAreaInWindow, new Vec2(this.getOutputWidth(), this.getOutputHeight()));
    this.keyboardState = new Keyboard(this.window);
    
    this.window.createBufferStrategy(2);
    this.windowBufferStrategy = Optional.ofNullable(this.window.getBufferStrategy()).get();
    
    this.TILES = new TileList(this);
    this.overworld = new Overworld(this);
    
    PlayerEntity playerEntity = new PlayerEntity();
    this.overworld.addEntity(playerEntity);
    playerEntity.setPos(new Vec2(-100.0f, 300.0f));
    
    CatEntity catEntity = new CatEntity();
    this.overworld.addEntity(catEntity);
    catEntity.setPos(new Vec2(-300.0f, 300.0f));
    
    this.currentScreen = new InGame(this);
    this.player = new InputToControllerBridge(catEntity, new Vec2(1280.0f, 720.0f));
  }
  
  void handleRespawn() {
    // Player request respawn
    CatEntity entity = new CatEntity();
    this.overworld.addEntity(entity);
    this.player.setNewEntityToControl(entity);
    
    entity.setPos(new Vec2(-300.0f, 300.0f));
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
    int ret = this.gameBuffer.getHeight();
    assert ret > 0;
    return ret;
  }

  public int getOutputWidth() {
    int ret = this.gameBuffer.getWidth();
    assert ret > 0;
    return ret;
  }

  public void runOnce() {
    if (this.isRunning) {
      throw new IllegalStateException("Cannot run game inside running game");
    }
    this.isRunning = true;

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
      if (this.keyboardState.getState(Keyboard.Button.R) == Keyboard.State.Clicked) {
        this.handleRespawn();
      }
    }
  }
  
  FloatRectangle calcOutputArea() {
    float renderWidth = (float) this.gameBuffer.getWidth();
    float renderHeight = (float) this.gameBuffer.getHeight();
    float renderAspect = renderWidth / renderHeight;
    
    float actualWidth = (float) this.window.getWidth();
    float actualHeight = (float) this.window.getHeight();
    
    float neededWidthIfHeightIsScaledToFit = actualHeight * renderAspect;
    
    float scale;
    if (neededWidthIfHeightIsScaledToFit > actualWidth) {
      scale = actualWidth / renderWidth;
    } else {
      scale = actualHeight / renderHeight;
    }
    
    float letterBoxedWidth = renderWidth * scale;
    float letterBoxedHeight = renderHeight * scale;
    float xOffset = Math.max((actualWidth - letterBoxedWidth) / 2.0f, 0.0f);
    float yOffset = Math.max((actualHeight - letterBoxedHeight) / 2.0f, 0.0f);
    
    return new FloatRectangle(
      new Vec2(xOffset, yOffset),
      new Vec2(xOffset + letterBoxedWidth, yOffset + letterBoxedHeight)
    );
  }

  void render(float deltaTime) {
    Graphics2D g = this.gameBuffer.createGraphics();
    try {
      this.getCurrentWorld().render(g, deltaTime);
      this.currentScreen.render(g, deltaTime);
    } finally {
      g.dispose();
    }

    do {
      do {
        Graphics gr = this.windowBufferStrategy.getDrawGraphics();
        gr.drawImage(
          this.gameBuffer,
          (int) this.outputAreaInWindow.getTopLeftCorner().x(),
          (int) this.outputAreaInWindow.getTopLeftCorner().y(),
          (int) this.outputAreaInWindow.getSize().x(),
          (int) this.outputAreaInWindow.getSize().y(),
          null
        );
        gr.dispose();
      } while (this.windowBufferStrategy.contentsRestored());

      this.windowBufferStrategy.show();
    } while (this.windowBufferStrategy.contentsLost());
  }

  void tick(float deltaTime) {
    // Tick the world and stuffs :3
    this.getCurrentWorld().tick(deltaTime);
  }
}
