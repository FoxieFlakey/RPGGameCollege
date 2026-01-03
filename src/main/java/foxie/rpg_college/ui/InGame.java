package foxie.rpg_college.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Optional;

import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.CharacterEntity;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.entity.LivingEntity;

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
    Vec2 hudStart = new Vec2(10.0f, this.getGame().getOutputHeight() - 20.0f);
    Vec2 hudSize = new Vec2(620.0f, 10.0f);

    // Name bar sizing
    Vec2 nameBarSize = new Vec2(400.0f, 30.0f);
    boolean hasNameBar = false;

    // Health bar sizing
    Vec2 healthBarSize = new Vec2(400.0f, 30.0f);
    boolean hasHealthBar = false;
    
    // Mana bar sizing
    Vec2 manaBarSize = new Vec2(400.0f, 30.0f);
    boolean hasManaBar = false;
    
    float padding = 10.0f;
    
    Optional<Entity> maybePlayer = this.getGame().getPlayer();
    
    // Determining what HUD elements can be rendered
    if (maybePlayer.isPresent()) {
      hudStart = hudStart.sub(new Vec2(0.0f, nameBarSize.y() + padding));
      hudSize = hudSize.add(new Vec2(0.0f, nameBarSize.y() + padding));
      hasNameBar = true;
    }
    
    if (maybePlayer.isPresent() && maybePlayer.get() instanceof LivingEntity) {
      hudStart = hudStart.sub(new Vec2(0.0f, healthBarSize.y() + padding));
      hudSize = hudSize.add(new Vec2(0.0f, healthBarSize.y() + padding));
      hasHealthBar = true;
    }
    
    if (maybePlayer.isPresent() && maybePlayer.get() instanceof CharacterEntity) {
      hudStart = hudStart.sub(new Vec2(0.0f, manaBarSize.y() + padding));
      hudSize = hudSize.add(new Vec2(0.0f, manaBarSize.y() + padding));
      hasManaBar = true;
    }

    // Create the background
    g.setColor(Color.GRAY);
    g.fillRect(
      (int) hudStart.x(),
      (int) hudStart.y(),
      (int) hudSize.x(),
      (int) hudSize.y()
    );
    
    Vec2 currentContentStart = hudStart.add(new Vec2(10.0f, 10.0f));
    
    // Now do the rendering
    if (hasNameBar) {
      this.renderNameBar(g, deltaTime, maybePlayer.get(), currentContentStart, nameBarSize);
      currentContentStart = currentContentStart.add(new Vec2(0.0f, nameBarSize.y() + padding));
    }
    
    if (hasHealthBar) {
      this.renderHealthBar(g, deltaTime, (LivingEntity) maybePlayer.get(), currentContentStart, healthBarSize);
      currentContentStart = currentContentStart.add(new Vec2(0.0f, healthBarSize.y() + padding));
    }
    
    if (hasManaBar) {
      this.renderManaBar(g, deltaTime, (CharacterEntity) maybePlayer.get(), currentContentStart, manaBarSize);
      currentContentStart = currentContentStart.add(new Vec2(0.0f, manaBarSize.y() + padding));
    }
  }
  
  void renderNameBar(Graphics2D g, float deltaTime, Entity player, Vec2 pos, Vec2 size) {
    Vec2 textStart = new Vec2(pos.x(), pos.y() + size.y() * 0.5f);
    g.setColor(Color.WHITE);
    g.setFont(Fonts.getDefault().deriveFont(Font.BOLD, 30));
    g.drawString("Name: " + player.getName(), (int) textStart.x(), (int) textStart.y() + Fonts.calcYOffsetSoItsCenter(g));
  }
  
  void renderManaBar(Graphics2D g, float deltaTime, CharacterEntity player, Vec2 manaBarPos, Vec2 manaBarSize) {
    float manaPercent = player.getManaPoint() / player.getMaxManaPoint();
    Vec2 healthTextStart = new Vec2(manaBarPos.x(), manaBarPos.y() + manaBarSize.y() * 0.5f);
    Vec2 bar = new Vec2(manaBarPos.x() + 200.0f, manaBarPos.y());

    Color manaBarColor = new Color(0.0f, 0.0f, 0.9f, 1.0f);

    // Create the mana bar
    g.setColor(new Color(0.7f, 0.4f, 0.4f, 1.0f));
    if (player.isDead()) {
      g.setColor(new Color(0.0f, 0.0f, 0.3f, 1.0f));
      manaPercent = 0.0f;
    }

    g.fillRect(
      (int) bar.x(),
      (int) bar.y(),
      (int) manaBarSize.x(),
      (int) manaBarSize.y()
    );

    // Create the health bar filled with actual health
    g.setColor(manaBarColor);
    g.fillRect(
      (int) bar.x(),
      (int) bar.y(),
      (int) (manaBarSize.x() * manaPercent),
      (int) manaBarSize.y()
    );

    // Now the border
    Stroke oldStroke = g.getStroke();
    g.setStroke(new BasicStroke(5.0f));

    g.setColor(Color.BLACK);
    g.drawRect(
      (int) bar.x(),
      (int) bar.y(),
      (int) (manaBarSize.x() - manaPercent),
      (int) manaBarSize.y()
    );

    g.setStroke(oldStroke);

    // Draw text 'health'
    g.setColor(Color.WHITE);
    g.setFont(Fonts.getDefault().deriveFont(Font.BOLD, 30));
    g.drawString("Mana: ", (int) healthTextStart.x(), (int) healthTextStart.y() + Fonts.calcYOffsetSoItsCenter(g));
  }
  
  void renderHealthBar(Graphics2D g, float deltaTime, LivingEntity player, Vec2 healthBarPos, Vec2 healthBarSize) {
    float healthPercent = player.getHealth() / player.getMaxHealth();
    Vec2 healthTextStart = new Vec2(healthBarPos.x(), healthBarPos.y() + healthBarSize.y() * 0.5f);
    Vec2 bar = new Vec2(healthBarPos.x() + 200.0f, healthBarPos.y());

    Color healthBarColor = new Color(0.9f, 0.0f, 0.0f, 1.0f);
    if (player.getFlashState()) {
      healthBarColor = new Color(0.9f, 0.8f, 0.8f, 1.0f);
    }

    // Create the empty health bar
    g.setColor(new Color(0.7f, 0.4f, 0.4f, 1.0f));
    if (player.isDead()) {
      g.setColor(new Color(0.3f, 0.0f, 0.0f, 1.0f));
    }

    g.fillRect(
      (int) bar.x(),
      (int) bar.y(),
      (int) healthBarSize.x(),
      (int) healthBarSize.y()
    );

    // Create the health bar filled with actual health
    g.setColor(healthBarColor);
    g.fillRect(
      (int) bar.x(),
      (int) bar.y(),
      (int) (healthBarSize.x() * healthPercent),
      (int) healthBarSize.y()
    );

    // Now the border
    Stroke oldStroke = g.getStroke();
    g.setStroke(new BasicStroke(5.0f));

    g.setColor(Color.BLACK);
    g.drawRect(
      (int) bar.x(),
      (int) bar.y(),
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
