package foxie.rpg_college;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Optional;

import foxie.rpg_college.input.Keyboard;
import foxie.rpg_college.input.Mouse;
import foxie.rpg_college.input.Keyboard.Button;
import foxie.rpg_college.world.World;

public class Game implements AutoCloseable {
  private boolean isRunning = false;
  private boolean isClosed = false;

  private final Frame window;
  private final BufferStrategy windowBufferStrategy;

  private World currentWorld = new World(this);
  private final BufferedImage gameBuffer = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
  private final Camera camera = new Camera(this.currentWorld.getWorldBound(), new Vec2(1280.0f, 720.0f));

  private float lastRenderTime = Util.getTime();

  public final Mouse mouseState;
  public final Keyboard keyboardState;

  public static final int TICK_RATE = 20;
  public static final int REFRESH_RATE = 30;

  public Game() {
    this.window = new Frame();
    this.window.setSize(this.getOutputWidth(), this.getOutputHeight());
    this.window.setFocusable(true);
    this.window.setUndecorated(false);
    this.window.setVisible(true);

    @SuppressWarnings("resource")
    final Game game = this;

    this.window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        game.isClosed = true;
      }
    });

    this.mouseState = new Mouse(this.window);
    this.keyboardState = new Keyboard(this.window);

    this.window.createBufferStrategy(2);
    this.windowBufferStrategy = Optional.ofNullable(this.window.getBufferStrategy()).get();
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
    return this.currentWorld;
  }

  public Camera getCamera() {
    return this.camera;
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

    this.handleInput();
    this.tick();
    this.render();

    this.isRunning = false;
  }

  void handleInput() {
    if (this.mouseState.getButtonState(Mouse.Button.Left) == Mouse.State.Clicked) {
      System.out.println("Left button clicked");
    }
  }

  void render() {
    float now = Util.getTime();
    float deltaTime = now - this.lastRenderTime;
    this.lastRenderTime = now;

    Vec2 translation = new Vec2(0.0f, 0.0f);
    float moveSpeed = 100.0f; // 20 pixels per second

    if (this.keyboardState.getState(Button.W).isNowPressed()) {
      translation = translation.add(new Vec2(0.0f, -moveSpeed * deltaTime));
    }
    
    if (this.keyboardState.getState(Button.A).isNowPressed()) {
      translation = translation.add(new Vec2(-moveSpeed * deltaTime, 0.0f));
    }

    if (this.keyboardState.getState(Button.S).isNowPressed()) {
      translation = translation.add(new Vec2(0.0f, moveSpeed * deltaTime));
    }

    if (this.keyboardState.getState(Button.D).isNowPressed()) {
      translation = translation.add(new Vec2(moveSpeed * deltaTime, 0.0f));
    }

    this.camera.setPosition(this.camera.getPosition().add(translation));

    Graphics2D g = this.gameBuffer.createGraphics();
    try {
      this.currentWorld.render(g);
    } finally {
      g.dispose();
    }

    do {
      do {
        Graphics gr = this.windowBufferStrategy.getDrawGraphics();
        gr.drawImage(
          this.gameBuffer,
          0,
          0,
          null
        );
        gr.dispose();
      } while (this.windowBufferStrategy.contentsRestored());

      this.windowBufferStrategy.show();
    } while (this.windowBufferStrategy.contentsLost());
  }

  void tick() {
    // Tick the world and stuffs :3
  }
}
