package foxie.rpg_college;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import foxie.rpg_college.world.World;

public class Game {
  private final JFrame window;
  private final GameView gameView;
  private boolean isRendering = false;

  private final JPanel gamePanel;
  private final JPanel actionPanel;

  private World currentWorld = new World(this);
  private final Camera camera = new Camera(this.currentWorld.getWorldBound(), new Vec2(1280.0f, 720.0f));

  public static final int TICK_RATE = 20;
  public static final int REFRESH_RATE = 30;

  public Game() {
    /**
     * The hierrarchy of the window is
     * root
     *  |
     *  -> Game panel
     *     |
     *     -> Game view (the game's output)
     *     -> Game control (controls for the game, like attack, defense, etc)
     */

    this.gameView = new GameView(1280, 720);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

    this.gamePanel = new JPanel();
    this.actionPanel = new JPanel();
    
    this.initGamePanel();

    panel.add(this.gamePanel);

    this.window = new JFrame("Game");
    this.window.getContentPane().add(panel);
    this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.window.setResizable(false);
    this.window.validate();
    this.window.pack();
    this.window.setVisible(true);
  }

  void initGamePanel() {
    this.gamePanel.setLayout(new BoxLayout(this.gamePanel, BoxLayout.Y_AXIS));
    this.gamePanel.add(this.gameView);
    
    Dimension gameViewDimension = new Dimension(this.gameView.getViewWidth(), this.gameView.getViewHeight());
    this.gameView.setMinimumSize(gameViewDimension);
    this.gameView.setPreferredSize(gameViewDimension);
    this.gameView.setMaximumSize(gameViewDimension);
    
    this.camera.setPosition(new Vec2(0.0f, 0.0f));

    this.actionPanel.setLayout(new FlowLayout());
    this.actionPanel.setMinimumSize(new Dimension(0, 100));
    this.actionPanel.setPreferredSize(new Dimension(0, 100));

    JButton button = new JButton("Hello");
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("Clicked");
      }
    });
    this.actionPanel.add(button);
    
    this.gamePanel.add(this.actionPanel);
  }

  public World getCurrentWorld() {
    return this.currentWorld;
  }

  public Camera getCamera() {
    return this.camera;
  }

  private boolean positive = true;
  public void render() {
    if (this.isRendering) {
      throw new IllegalStateException("Cannot render inside another render");
    }
    this.isRendering = true;

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

    this.gameView.runRenderCode((g) -> {
      this.currentWorld.render(g);
    });

    this.isRendering = false;
  }

  void tick() {
    // Tick the world and stuffs :3
  }
}
