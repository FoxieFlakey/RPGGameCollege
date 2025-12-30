package foxie.rpg_college.tile;

import java.awt.Graphics2D;

import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;

// A tile instance is reused multiple time
// for differing positions
public abstract class Tile {
  // Tiles are 64 x 64 pixels in size
  public static final Vec2 SIZE = new Vec2(64.0f, 64.0f);

  protected final Game game;

  public Tile(Game game) {
    this.game = game;
  }
  
  public final Game getGame() {
    return this.game;
  }
  
  public abstract boolean isCollisionEnabled();
  public abstract boolean canBeTicked();
  public abstract void render(Graphics2D g, float deltaTime, Vec2 position);
  public abstract void tick(float deltaTime, Vec2 position);
}
