package foxie.rpg_college.world;

import java.awt.Graphics2D;
import java.util.HashMap;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.Entity;

public abstract class World {
  private final Game game;
  private final FloatRectangle bound;
  private final HashMap<Long, Entity> entities = new HashMap<>();

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
  
  public void addEntity(Entity entity) {
    if (this.entities.containsKey(entity.id)) {
      throw new IllegalStateException("Attempt to add same entity twice");
    }

    this.entities.put(entity.id, entity);
    if (entity.getWorld() != null) {
      entity.getWorld().removeEntity(entity);
    }
    entity.setWorld(this);
  }

  public void removeEntity(Entity entity) {
    if (!this.entities.containsKey(entity.id)) {
      throw new IllegalStateException("Attempt to remove unknown entity");
    }

    this.entities.remove(entity.id);
    entity.setWorld(null);
  }

  protected void tickEntities(float deltaTime) {
    for (Entity e : this.entities.values()) {
      e.tick(deltaTime);
    }
  }

  protected void renderEntities(Graphics2D g, float deltaTime) {
    for (Entity e : this.entities.values()) {
      e.render(g, deltaTime);
    }
  }

  public abstract Vec2 validatePos(Vec2 pos);
  public abstract boolean isValidPos(Vec2 pos);
  public abstract void render(Graphics2D g, float deltaTime);
  public abstract void tick(float deltaTime);
}
