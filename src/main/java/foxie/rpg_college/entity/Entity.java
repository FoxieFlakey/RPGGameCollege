package foxie.rpg_college.entity;

import java.awt.Graphics2D;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import foxie.rpg_college.Camera;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.world.World;

public abstract class Entity {
  // Position would be, at center of hitbox/collisionbox
  // of the entity

  private Vec2 position = new Vec2(0.0f, 0.0f);
  private World currentWorld = null;
  
  public final long id;
  private static final AtomicLong ID_COUNTER = new AtomicLong(0);

  public Entity() {
    this.id = Entity.ID_COUNTER.getAndUpdate(x -> {
      if (x == Long.MAX_VALUE) {
        return Long.MAX_VALUE;
      } else {
        return x + 1;
      }
    });

    if (this.id == Long.MAX_VALUE) {
      throw new RuntimeException("Counter for entity ID overflowed!");
    }
  }

  public Vec2 getPos() {
    return this.position;
  }

  public void setPos(Vec2 pos) {
    this.position = this.currentWorld.validatePos(pos);

    if (this.getCollisionBox().isPresent()) {
      this.getCollisionBox().get().setPos(pos);
    }
  }

  public final World getWorld() {
    return this.currentWorld;
  }

  // Be careful, THIS DOES NOT add/remove
  // itself from corresponding world
  public void setWorld(World world) {
    this.currentWorld = world;
  }
  
  public void onCollision() {
    this.setPos(this.getCollisionBox().get().getPos());
  }
  
  // This prefer 'false', so if there two entities
  // one say true other say false, the result is false
  // which mean no collision happens
  public abstract boolean canCollideWith(Entity other);

  public abstract Optional<CollisionBox> getCollisionBox();
  public abstract boolean isVisible(Camera cam);
  public abstract void render(Graphics2D g, float deltaTime);
  public abstract void tick(float deltaTime);
}
