package foxie.rpg_college.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;

import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.PlayerEntity;

public class InGame extends Screen {
  public InGame(Game game) {
    super(game);
  }

  @Override
  public boolean handleInput() {
    return true;
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
    Vec2 hudStart = new Vec2(10.0f, this.getGame().getOutputHeight() - 60.0f);
    Vec2 hudSize = new Vec2(600.0f, 50.0f);

    Vec2 healthBar = new Vec2(hudStart.x() + 190.0f, hudStart.y() + 10.0f);
    Vec2 healthBarSize = new Vec2(400.0f, 30.0f);

    Vec2 healthTextStart = new Vec2(hudStart.x() + 10.0f, healthBar.y() + healthBarSize.y() * 0.5f);

    PlayerEntity player = (PlayerEntity) this.getGame().getPlayer();
    float healthPercent = player.getHealth() / player.getMaxHealth();

    Color healthBarColor = new Color(0.9f, 0.0f, 0.0f, 1.0f);
    if (player.getFlashState()) {
      healthBarColor = new Color(0.9f, 0.8f, 0.8f, 1.0f);
    }

    // Create the background
    g.setColor(Color.GRAY);
    g.fillRect(
      (int) hudStart.x(),
      (int) hudStart.y(),
      (int) hudSize.x(),
      (int) hudSize.y()
    );

    // Create the empty health bar
    g.setColor(new Color(0.7f, 0.4f, 0.4f, 1.0f));
    if (player.isDead()) {
      g.setColor(new Color(0.3f, 0.0f, 0.0f, 1.0f));
    }

    g.fillRect(
      (int) healthBar.x(),
      (int) healthBar.y(),
      (int) healthBarSize.x(),
      (int) healthBarSize.y()
    );

    // Create the health bar filled with actual health
    g.setColor(healthBarColor);
    g.fillRect(
      (int) healthBar.x(),
      (int) healthBar.y(),
      (int) (healthBarSize.x() * healthPercent),
      (int) healthBarSize.y()
    );

    // Now the border
    Stroke oldStroke = g.getStroke();
    g.setStroke(new BasicStroke(5.0f));

    g.setColor(Color.BLACK);
    g.drawRect(
      (int) healthBar.x(),
      (int) healthBar.y(),
      (int) (healthBarSize.x() - healthPercent),
      (int) healthBarSize.y()
    );

    g.setStroke(oldStroke);

    // Draw text 'health'
    g.setColor(Color.WHITE);
    g.setFont(Fonts.getDefault().deriveFont(Font.BOLD, 30));
    g.drawString("Health: ", (int) healthTextStart.x(), (int) healthTextStart.y() + Fonts.calcYOffsetSoItsCenter(g));
  }
}
