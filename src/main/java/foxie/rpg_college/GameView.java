package foxie.rpg_college;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class GameView extends Canvas {
  private Object swapBufferLock = new Object();
  private BufferedImage frontBuffer;
  private BufferedImage backBuffer;

  public GameView(int width, int height) {
    this.frontBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    this.backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
  }

  public int getViewHeight() {
    int height = this.backBuffer.getHeight();
    assert height > 0;
    return height;
  }

  public int getViewWidth() {
    int width = this.backBuffer.getWidth();
    assert width > 0;
    return width;
  }

  public synchronized void runRenderCode(Consumer<Graphics2D> code) {
    Graphics2D g = this.backBuffer.createGraphics();
    try {
      code.accept(g);
    } finally {
      g.dispose();
    }

    synchronized (this.swapBufferLock) {
      BufferedImage tmp = this.backBuffer;
      this.backBuffer = this.frontBuffer;
      this.frontBuffer = tmp;
    }

    this.repaint();
  }

  @Override
  public void paint(Graphics g) {
    int width = this.getWidth();
    int height = this.getHeight();

    g.setClip(0, 0, width, height);
    
    synchronized (this.swapBufferLock) {
      g.drawImage(
        this.frontBuffer,
        0,
        0,
        width,
        height,
        null
      );
    }
  }
}
