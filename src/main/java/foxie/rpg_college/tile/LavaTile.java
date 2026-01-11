package foxie.rpg_college.tile;

import java.awt.Color;
import java.awt.Graphics2D;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.entity.LivingEntity;
import foxie.rpg_college.entity.damage.TileDamageSource;

public class LavaTile extends Tile {
  private static final float DAMAGE = 15.0f;
  private static final float BURN_PERIOD = 1.0f;

  public LavaTile(Game game) {
    super(game);
  }

  @Override
  public boolean isCollisionEnabled() {
    return false;
  }

  @Override
  public boolean canBeTicked() {
    return false;
  }

  @Override
  public void render(Graphics2D g, float deltaTime, IVec2 position) {
    FloatRectangle renderBox = TileHelper.calculateRenderBox(this, position);

    int x = (int) renderBox.getTopLeftCorner().x();
    int y = (int) renderBox.getTopLeftCorner().y();
    int width = (int) renderBox.getSize().x();
    int height = (int) renderBox.getSize().y();

    g.setColor(Color.RED);
    g.fillRect(x, y, width, height);
  }

  @Override
  public void tick(float deltaTime, IVec2 position) { 
  }
  
  @Override
  public void steppedBy(Entity e, IVec2 coord) {
    if (e instanceof LivingEntity) {
      ((LivingEntity) e).burn(LavaTile.BURN_PERIOD, new TileDamageSource(this, coord, LavaTile.DAMAGE));
    }
  }
}
