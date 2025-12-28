package foxie.rpg_college;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

public class GameView extends Canvas {
  private final Game game;

  public GameView(Game game) {
    this.game = game;
  }

  @Override
  public void paint(Graphics g) {
    int width = this.getWidth();
    int height = this.getHeight();

    g.setClip(0, 0, width, height);

    g.setColor(Color.GRAY);
    g.fillRect(0, 0, width, height);

    this.game.getCurrentWorld().render(g);;
  }
}
