package foxie.rpg_college;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

public class GameView extends Canvas {
  @Override
  public void paint(Graphics g) {
    int width = this.getWidth();
    int height = this.getHeight();

    g.setClip(0, 0, width, height);

    g.setColor(Color.GRAY);
    g.fillRect(0, 0, width, height);

    g.setColor(Color.PINK);
    g.drawRect(10, 10, width - 20, height - 20);
  }
}
