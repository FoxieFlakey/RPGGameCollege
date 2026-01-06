package foxie.rpg_college.tile;

import java.awt.Color;
import java.awt.Graphics2D;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.world.World;

public class PortalTile extends Tile {
  private final String targetWorldId;
  
  public PortalTile(Game game, String targetWorld) {
    super(game);
    this.targetWorldId = targetWorld;
  }
  
  @Override
  public void steppedBy(Entity e, IVec2 coord) {
    World targetWorld = this.getGame().getWorldManager().getWorld(this.targetWorldId).get();
    targetWorld.addEntity(e);
    e.setPos(new Vec2(0.0f, 0.0f));
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

    g.setColor(Color.GREEN);
    g.fillRect(x, y, width, height);
  }

  @Override
  public void tick(float deltaTime, IVec2 position) {
  }
}
