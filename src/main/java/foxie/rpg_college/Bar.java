package foxie.rpg_college;

import java.awt.Color;
import java.awt.Graphics2D;

public class Bar {
  public static final float WIDTH = 200.0f;
  public static final float HEIGHT = 20.0f;
  
  public float min;
  public float val;
  public float max;
  public Color filledColor;
  public Color unfilledColor;
  public Color borderColor = Color.BLACK;
  
  public Bar(float min, float val, float max, Color filledColor) {
    this.min = min;
    this.val = val;
    this.max = max;
    this.filledColor = filledColor;
    this.unfilledColor = new Color(
      (float) filledColor.getRed() / 255.0f * 0.5f,
      (float) filledColor.getRed() / 255.0f * 0.5f,
      (float) filledColor.getRed() / 255.0f * 0.5f
    );
  }
  
  public Bar(float min, float val, float max, Color filledColor, Color unfilledColor) {
    this.min = min;
    this.val = val;
    this.max = max;
    this.filledColor = filledColor;
    this.unfilledColor = unfilledColor;
  }
  
  // NOTE: 'pos' is center of the bar
  public void render(float renderScale, Graphics2D g, Vec2 pos) {
    float percent = Util.clamp((this.val - this.min) / (this.max - this.min), 0.0f, 1.0f);
    int x = (int) (pos.x() - ((Bar.WIDTH * renderScale) / 2.0f));
    int y = (int) (pos.y());
    
    // Draw background
    g.setColor(this.borderColor);
    g.fillRect(
      x,
      y,
      (int) (Bar.WIDTH * renderScale),
      (int) (Bar.HEIGHT * renderScale)
    );
    
    // Draw the empty bar
    g.setColor(this.unfilledColor);
    g.fillRect(
      (int) (x + (5.0f * renderScale)),
      (int) (y + (5.0f * renderScale)),
      (int) ((Bar.WIDTH - 10.0f) * renderScale),
      (int) ((Bar.HEIGHT - 10.0f) * renderScale)
    );
    
    // Draw the filled bar
    g.setColor(this.filledColor);
    g.fillRect(
      (int) (x + (5.0f * renderScale)),
      (int) (y + (5.0f * renderScale)),
      (int) ((Bar.WIDTH - 10.0f) * percent * renderScale),
      (int) ((Bar.HEIGHT - 10.0f) * renderScale)
    );
  }
}
