package foxie.rpg_college.world;

import java.awt.Graphics2D;
import java.util.HashMap;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.CollisionBox;
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
    // Try resolve collision in 3 times
    for (int i = 0; i < 3; i++) {
      for (Entity e : this.entities.values()) {
        if (e.getCollisionBox().isEmpty()) {
          continue;
        }

        CollisionBox thisBox = e.getCollisionBox().get();

        // Try fix the collision with other entities
        for (Entity other : this.entities.values()) {
          if (other.getCollisionBox().isEmpty() || other == e) {
            continue;
          }

          if (other.canCollideWith(e) == false || e.canCollideWith(other) == false) {
            // Prefer collision not happening if there conflicting
            // answers
            continue;
          }

          CollisionBox otherBox = other.getCollisionBox().get();
          
          if (thisBox.checkCollisionAndFix(otherBox)) {
            e.onCollision();
          }
        }
      }
    }

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
