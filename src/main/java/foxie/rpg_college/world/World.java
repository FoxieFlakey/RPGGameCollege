package foxie.rpg_college.world;

import java.awt.Graphics2D;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;

public abstract class World {
  private final Game game;
  private final FloatRectangle bound;

  public World(Game game, FloatRectangle bound) {
    this.game = game;
    this.bound = bound;
  }

  public final Game getGame() {
    return this.game;
  }
  
  public final FloatRectangle getWorldBound() {
    return this.bound;
  }

  public abstract Vec2 validatePos(Vec2 pos);
  public abstract boolean isValidPos(Vec2 pos);
  public abstract void render(Graphics2D g, float deltaTime);
  public abstract void tick(float deltaTime);
}
