package foxie.rpg_college.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;

import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;

public abstract class ScreenWithText extends Screen {
  public ScreenWithText(Game game) {
    super(game);
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
    float renderScale = this.getGame().getCamera().getScale().x();
    float width = this.getGame().getOutputWidth();
    float height = this.getGame().getOutputHeight();
    
    Vec2 textStart = new Vec2(width * 0.25f, height * 0.25f);
    Vec2 textEnd = new Vec2(width * 0.75f, height * 0.75f);
    
    g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.60f));
    g.fillRect(
      (int) textStart.x(),
      (int) textStart.y(),
      (int) (textEnd.x() - textStart.x()),
      (int) (textEnd.y() - textStart.y())
    );
    
    g.setColor(Color.WHITE);
    g.setFont(Fonts.getDefault().deriveFont(30.0f * renderScale));
    
    Iterator<String> lines = this.getText().lines().iterator();
    int lineIndex = 0;
    int lineHeight = Fonts.getFontHeight(g);
    
    while (lines.hasNext()) {
      String line = lines.next();
      g.drawString(
        line,
        textStart.x() + (10.0f * renderScale),
        textStart.y() + (30.0f * renderScale) + lineIndex * lineHeight
      );
      
      lineIndex++;
    }
  }
  
  public abstract String getText();
}
