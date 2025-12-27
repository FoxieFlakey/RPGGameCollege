package foxie.rpg_college;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Game {
  private final JFrame window;
  private final GameView gameView;
  private boolean isRunning = false;

  private final JPanel gamePanel;
  private final JPanel actionPanel;

  public static final int TICK_RATE = 20;

  // This has to be called from within Java's swing thread
  public Game() {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException("Attempting to construct Game from wrong thread");
    }

    /**
     * The hierrarchy of the window is
     * root
     *  |
     *  -> Game panel
     *     |
     *     -> Game view (the game's output)
     *     -> Game control (controls for the game, like attack, defense, etc)
     */

    this.gameView = new GameView();

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
    
    this.gameView.setMinimumSize(new Dimension(1280, 720));
    this.gameView.setPreferredSize(new Dimension(1280, 720));
    this.gameView.setMaximumSize(new Dimension(1280, 720));
    
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

  public void run() {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException("Attempting to run Game from wrong thread");
    }

    if (this.isRunning) {
      throw new IllegalStateException("Game already running");
    }
    this.isRunning = true;

    Timer timer = new Timer(1000 / TICK_RATE, (e) -> {
      this.tick();
    });
    timer.start();
  }

  void tick() {
    // Tick the world and stuffs :3
  }
}
