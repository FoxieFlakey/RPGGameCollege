package foxie.rpg_college.world;

import java.awt.Graphics2D;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.CollisionBox;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.tile.Tile;

public abstract class World {
  private final Game game;
  private final FloatRectangle bound;
  private final LinkedHashMap<Long, Entity> entities = new LinkedHashMap<>();
  private final LinkedHashMap<IVec2, Tile> tiles = new LinkedHashMap<>();

  private final CollisionBox[] worldBorder;
  private final static float BORDER_DEPTH = 200000000.0f;
  private final static float BORDER_INNER_DEPTH = 50.0f;

  public World(Game game, FloatRectangle bound) {
    this.game = game;
    this.bound = bound;

    float left = this.bound.getTopLeftCorner().x();
    float right = this.bound.getBottomRightCorner().x();
    float top = this.bound.getTopLeftCorner().y();
    float bottom = this.bound.getBottomRightCorner().y();

    float width = this.bound.getSize().x();
    float height = this.bound.getSize().y();

    Vec2 center = new Vec2(
      (left + right) * 0.5f,
      (top + bottom) * 0.5f
    );
    
    this.worldBorder = new CollisionBox[] {
      // The top part of world
      new CollisionBox(new Vec2(center.x(), top - World.BORDER_DEPTH * 0.5f), new Vec2(width + World.BORDER_DEPTH * 2.0f, World.BORDER_DEPTH + World.BORDER_INNER_DEPTH * 2.0f)),
      // The bottom part of world
      new CollisionBox(new Vec2(center.x(), bottom + World.BORDER_DEPTH * 0.5f), new Vec2(width + World.BORDER_DEPTH * 2.0f, World.BORDER_DEPTH + World.BORDER_INNER_DEPTH * 2.0f)),

      // Left part of world
      new CollisionBox(new Vec2(left - World.BORDER_DEPTH * 0.5f, center.y()), new Vec2(World.BORDER_DEPTH + World.BORDER_INNER_DEPTH * 2.0f, height + World.BORDER_DEPTH * 2.0f)),
      // Right part of world
      new CollisionBox(new Vec2(right + World.BORDER_DEPTH * 0.5f, center.y()), new Vec2(World.BORDER_DEPTH + World.BORDER_INNER_DEPTH * 2.0f, height + World.BORDER_DEPTH * 2.0f))
    };
  }

  public final Game getGame() {
    return this.game;
  }
  
  public final FloatRectangle getWorldBound() {
    return this.bound;
  }

  public void addTile(IVec2 coord, Tile tile) {
    if (this.tiles.containsKey(coord)) {
      throw new IllegalStateException("Attempting to add more than one tile to same coord");
    }

    this.tiles.put(coord, tile);
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

  void checkCollisionInner(Entity e, Entity other, CollisionBox thisBox) {
    if (other.getCollisionBox().isEmpty() || other == e) {
      return;
    }

    if (other.canCollideWith(e) == false || e.canCollideWith(other) == false) {
      // Prefer collision not happening if there conflicting
      // answers
      return;
    }

    CollisionBox otherBox = other.getCollisionBox().get();
    
    if (thisBox.checkCollisionAndFix(otherBox)) {
      e.onCollision();
    }
  }

  void checkCollision(Entity e) {
    CollisionBox tempBox = new CollisionBox(new Vec2(0.0f, 0.0f), Tile.SIZE);
    if (e.getCollisionBox().isEmpty()) {
      return;
    }

    CollisionBox thisBox = e.getCollisionBox().get();
    if (thisBox.isUnmoveable()) {
      // Current entity cannot be moved by collision, skip
      return;
    }

    // Check collision against all tiles
    for (Entry<IVec2, Tile> coordAndTile : this.tiles.entrySet()) {
      if (!coordAndTile.getValue().isCollisionEnabled()) {
        continue;
      }

      tempBox.setPos(Tile.fromTileCoordToWorldCoord(coordAndTile.getKey()));
      if (thisBox.checkCollisionAndFix(tempBox)) {
        e.onCollision();
      }
    }

    // Try fix the collision with other entities
    for (Entity other : this.entities.values()) {
      checkCollisionInner(e, other, thisBox);
    }

    for (Entity other : this.entities.reversed().values()) {
      checkCollisionInner(e, other, thisBox);
    }

    // Check collision against world border
    for (CollisionBox otherBox : this.worldBorder) {
      if (thisBox.checkCollisionAndFix(otherBox)) {
        e.onCollision();
      }
    }
  }

  protected void tickEntities(float deltaTime) {
    for (Entry<IVec2, Tile> coordAndTile : this.tiles.entrySet()) {
      coordAndTile.getValue().tick(deltaTime, coordAndTile.getKey());
    }

    for (Entity e : this.entities.values()) {
      e.tick(deltaTime);
    }

    // Try resolve collision in 2 times both forward and backward
    for (Entity e : this.entities.values()) {
      checkCollision(e);
    }

    for (Entity e : this.entities.reversed().values()) {
      checkCollision(e);
    }
  }

  protected void renderEntities(Graphics2D g, float deltaTime) {
    for (Entry<IVec2, Tile> coordAndTile : this.tiles.entrySet()) {
      coordAndTile.getValue().render(g, deltaTime, coordAndTile.getKey());
    }

    for (Entity e : this.entities.values()) {
      e.render(g, deltaTime);
    }
  }

  public abstract Vec2 validatePos(Vec2 pos);
  public abstract boolean isValidPos(Vec2 pos);
  public abstract void render(Graphics2D g, float deltaTime);
  public abstract void tick(float deltaTime);
}
