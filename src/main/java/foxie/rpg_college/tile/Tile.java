package foxie.rpg_college.tile;

import java.awt.Graphics2D;

import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.Entity;

// A tile instance is reused multiple time
// for differing positions
public abstract class Tile {
  // Tiles are 64 x 64 pixels in size
  public static final Vec2 SIZE = new Vec2(64.0f, 64.0f);

  protected final Game game;
  private String name = this.getClass().getName();

  public Tile(Game game) {
    this.game = game;
  }
  
  public final Game getGame() {
    return this.game;
  }
  
  public String getName(IVec2 coord) {
    return this.name;
  }

  public static Vec2 fromTileCoordToWorldCoord(IVec2 coord) {
    return new Vec2(
      (float) coord.x() * Tile.SIZE.x(),
      (float) coord.y() * Tile.SIZE.y()
    );
  }
  
  public abstract void steppedBy(Entity e, IVec2 coord);
  public abstract boolean isCollisionEnabled();
  public abstract boolean canBeTicked();
  public abstract void render(Graphics2D g, float deltaTime, IVec2 position);
  public abstract void tick(float deltaTime, IVec2 position);
}
