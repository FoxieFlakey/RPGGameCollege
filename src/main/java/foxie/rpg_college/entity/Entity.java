package foxie.rpg_college.entity;

import java.awt.Graphics2D;

import foxie.rpg_college.Camera;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.world.World;

public abstract class Entity {
  // Position would be, at center of hitbox/collisionbox
  // of the entity

  private Vec2 position = new Vec2(0.0f, 0.0f);
  private World currentWorld;

  public Entity(World world) {
    this.currentWorld = world;
  }

  public Vec2 getPos() {
    return this.position;
  }

  public void setPos(Vec2 pos) {
    this.position = this.currentWorld.validatePos(pos);
  }

  public final World getWorld() {
    return this.currentWorld;
  }

  public void setWorld(World world) {
    this.currentWorld = world;
  }

  public abstract boolean isVisible(Camera cam);
  public abstract void render(Graphics2D g, float deltaTime);
  public abstract void tick(float deltaTime);
}
