package foxie.rpg_college;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Optional;

import foxie.rpg_college.world.World;

public class Game implements AutoCloseable {
  private boolean isRunning = false;
  private boolean isClosed = false;

  private final Frame window;
  private final BufferStrategy windowBufferStrategy;

  private World currentWorld = new World(this);
  private final BufferedImage gameBuffer = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
  private final Camera camera = new Camera(this.currentWorld.getWorldBound(), new Vec2(1280.0f, 720.0f));

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

  private boolean positive = true;
  public void runOnce() {
    if (this.isRunning) {
      throw new IllegalStateException("Cannot run game inside running game");
    }
    this.isRunning = true;

    this.tick();
    this.render();

    this.isRunning = false;
  }

  void render() {
    Vec2 newPos = this.camera.getPosition();
    if (positive) {
      newPos = newPos.add(new Vec2(20.0f, 0.0f));
    } else {
      newPos = newPos.add(new Vec2(-20.0f, 0.0f));
    }

    if (newPos.x() > 200.0) {
      this.positive = false;
    } else if (newPos.x() < -200.0) {
      this.positive = true;
    }
    this.camera.setPosition(newPos);

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
