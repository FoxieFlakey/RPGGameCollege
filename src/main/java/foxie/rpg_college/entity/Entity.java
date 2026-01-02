package foxie.rpg_college.entity;

import java.awt.Graphics2D;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Orientation;
import foxie.rpg_college.Util;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.controller.Controller;
import foxie.rpg_college.tile.Tile;
import foxie.rpg_college.world.World;

public abstract class Entity {
  // Position would be, at center of hitbox/collisionbox
  // of the entity

  private Vec2 position = new Vec2(0.0f, 0.0f);
  private World currentWorld = null;
  private float rotation = 90.0f;
  private Optional<Controller> controller = Optional.empty();
  private String name;
  
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
    
    name = this.getClass().getName() + " #" + this.id;
  }

  public final Vec2 getPos() {
    return this.position;
  }

  public void setPos(Vec2 pos) {
    this.position = this.currentWorld.validatePos(pos);

    if (this.getCollisionBox().isPresent()) {
      this.getCollisionBox().get().setPos(pos);
    }
    
    if (this.controller.isPresent()) {
      this.controller.get().dispatchOnPositionUpdated();
    }
  }

  public final World getWorld() {
    return this.currentWorld;
  }

  // Be careful, THIS DOES NOT add/remove
  // itself from corresponding world
  public void setWorld(World world) {
    this.currentWorld = world;
    if (this.controller.isPresent()) {
      this.controller.get().dispatchOnWorldChange();
    }
  }
  
  public void onCollision() {
    this.setPos(this.getCollisionBox().get().getPos());
  }

  public final float getRotation() {
    return this.rotation;
  }
  
  public void setRotation(float rotation) {
    this.rotation = Util.normalizeAngle(rotation);
  }

  public final Orientation getOrientation() {
    return Orientation.fromDegrees(this.rotation);
  }
  
  public final Optional<Controller> getController() {
    if (!this.canBeControlled()) {
      return Optional.empty();
    }
    
    if (this.controller.isEmpty()) {
      this.controller = Optional.of(this.createController());
    }
    
    return Optional.of(this.controller.get());
  }
  
  public final boolean canDispatchControllerEvents() {
    return this.canBeControlled() && this.controller.isPresent();
  }
  
  public String getName() {
    return this.name;
  }

  // This prefer 'false', so if there two entities
  // one say true other say false, the result is false
  // which mean no collision happens
  public abstract boolean canCollideWith(Entity other);
  public abstract void onTileCollision(IVec2 coord, Tile other);
  public abstract void onEntityCollision(Entity other);
  public abstract Optional<CollisionBox> getCollisionBox();
  public abstract boolean isVisible(Camera cam);
  public abstract void render(Graphics2D g, float deltaTime);
  public abstract void tick(float deltaTime);
  public abstract void onTileStep(Tile tile, IVec2 tileCoord);
  public abstract Optional<FloatRectangle> getBoxToBeCheckedForTileStep();
  public abstract boolean canBeControlled();
  protected abstract Controller createController();
}
