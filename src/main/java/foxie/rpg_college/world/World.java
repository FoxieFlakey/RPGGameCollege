package foxie.rpg_college.world;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.CollisionBox;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.tile.Tile;

public abstract class World {
  private final Game game;
  private final FloatRectangle renderBound;
  private final FloatRectangle validBound;
  private final ConcurrentHashMap<Long, Entity> entities = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<IVec2, Tile> tiles = new ConcurrentHashMap<>();

  private final CollisionBox[] worldBorder;
  private final static float BORDER_DEPTH = 200000000.0f;
  private final static float BORDER_INNER_DEPTH = 50.0f;

  public World(Game game, FloatRectangle bound) {
    this.game = game;
    this.renderBound = bound;
    this.validBound = new FloatRectangle(
      this.renderBound.getTopLeftCorner().add(new Vec2(World.BORDER_INNER_DEPTH - 20.0f)),
      this.renderBound.getBottomRightCorner().sub(new Vec2(World.BORDER_INNER_DEPTH - 20.0f))
    );

    float left = this.renderBound.getTopLeftCorner().x();
    float right = this.renderBound.getBottomRightCorner().x();
    float top = this.renderBound.getTopLeftCorner().y();
    float bottom = this.renderBound.getBottomRightCorner().y();

    float width = this.renderBound.getSize().x();
    float height = this.renderBound.getSize().y();
    
    if (width < World.BORDER_INNER_DEPTH || height < World.BORDER_INNER_DEPTH) {
      throw new IllegalArgumentException("World bound is too small");
    }

    Vec2 center = new Vec2(
      (left + right) * 0.5f,
      (top + bottom) * 0.5f
    );
    
    this.worldBorder = new CollisionBox[] {
      // The top part of world
      new CollisionBox(new Vec2(center.x(), top - World.BORDER_DEPTH * 0.5f), new Vec2(width + World.BORDER_DEPTH * 2.0f, World.BORDER_DEPTH + World.BORDER_INNER_DEPTH * 2.0f), true),
      // The bottom part of world
      new CollisionBox(new Vec2(center.x(), bottom + World.BORDER_DEPTH * 0.5f), new Vec2(width + World.BORDER_DEPTH * 2.0f, World.BORDER_DEPTH + World.BORDER_INNER_DEPTH * 2.0f), true),

      // Left part of world
      new CollisionBox(new Vec2(left - World.BORDER_DEPTH * 0.5f, center.y()), new Vec2(World.BORDER_DEPTH + World.BORDER_INNER_DEPTH * 2.0f, height + World.BORDER_DEPTH * 2.0f), true),
      // Right part of world
      new CollisionBox(new Vec2(right + World.BORDER_DEPTH * 0.5f, center.y()), new Vec2(World.BORDER_DEPTH + World.BORDER_INNER_DEPTH * 2.0f, height + World.BORDER_DEPTH * 2.0f), true)
    };
  }

  public final Game getGame() {
    return this.game;
  }
  
  public final FloatRectangle getWorldBound() {
    return this.validBound;
  }
  
  public final FloatRectangle getRenderBound() {
    return this.renderBound;
  }

  public void addTile(IVec2 coord, Tile tile) {
    if (this.tiles.containsKey(coord)) {
      throw new IllegalStateException("Attempting to add more than one tile to same coord");
    }
    
    Vec2 coordInWorld = Tile.fromTileCoordToWorldCoord(coord);
    if (!this.isValidPos(coordInWorld)) {
      throw new IllegalArgumentException("Attempting to add tile to outside of world");
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
    
    // Clamp the pos to the valid position
    if (!this.isValidPos(entity.getPos())) {
      entity.setPos(this.validatePos(entity.getPos()));
    }
  }

  public void removeEntity(Entity entity) {
    if (!this.entities.containsKey(entity.id)) {
      throw new IllegalStateException("Attempt to remove unknown entity");
    }

    this.entities.remove(entity.id);
    entity.setWorld(null);
  }
  
  // The iterator must not be saved as new entitity may be added later
  public Stream<Entity> findEntitiesOverlaps(Vec2 point) {
    return this.entities.values()
      .stream()
      .filter(e -> {
        Optional<CollisionBox> maybeBox = e.getCollisionBox();
        if (maybeBox.isEmpty()) {
          return false;
        }
        
        return maybeBox.get().asRect().contains(point);
      });
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
      other.onCollision();
      
      e.onEntityCollision(other);
      other.onEntityCollision(e);
    }
  }

  void checkCollisionWithTiles(Entity e, CollisionBox thisBox) {
    CollisionBox tempBox = new CollisionBox(new Vec2(0.0f, 0.0f), Tile.SIZE, true);
    for (Entry<IVec2, Tile> coordAndTile : this.tiles.entrySet()) {
      if (!coordAndTile.getValue().isCollisionEnabled()) {
        continue;
      }

      if (e.getWorld() == null || e.getCollisionBox().isEmpty()) {
        // Entity decided that it don't want colliding anymore
        return;
      }

      tempBox.setPos(Tile.fromTileCoordToWorldCoord(coordAndTile.getKey()));
      if (thisBox.checkCollisionAndFix(tempBox)) {
        e.onCollision();
        e.onTileCollision(coordAndTile.getKey(), coordAndTile.getValue());
      }
    }
  }

  void checkCollision(Entity e) {
    if (e.getWorld() == null || e.getCollisionBox().isEmpty()) {
      return;
    }

    CollisionBox thisBox = e.getCollisionBox().get();
    // Check collision against all tiles
    this.checkCollisionWithTiles(e, thisBox);

    // Try fix the collision with other entities
    for (Entity other : this.entities.values()) {
      if (e.getWorld() == null || e.getCollisionBox().isEmpty()) {
        // Entity decided it doesnt want colliding
        return;
      }
      
      checkCollisionInner(e, other, thisBox);
      
      if (other.getCollisionBox().isEmpty()) {
        // Other entity decided that it don't want colliding anymore
        continue;
      }
    }

    // Check collision against all tiles
    this.checkCollisionWithTiles(e, thisBox);

    // Check collision against world border
    for (CollisionBox otherBox : this.worldBorder) {
      if (thisBox.checkCollisionAndFix(otherBox)) {
        if (e.getWorld() == null || e.getCollisionBox().isEmpty()) {
          // Entity decided that it don't want colliding anymore
          return;
        }
        
        e.onCollision();
      }
    }
  }

  protected void tickEntities(float deltaTime) {
    for (Entry<IVec2, Tile> coordAndTile : this.tiles.entrySet()) {
      coordAndTile.getValue().tick(deltaTime, coordAndTile.getKey());
    }
    
    for (Entity e : this.entities.values()) {
      Optional<FloatRectangle> maybeCheckBounds = e.getBoxToBeCheckedForTileStep();
      if (maybeCheckBounds.isEmpty()) {
        continue;
      }
      
      FloatRectangle checkBounds = maybeCheckBounds.get();
      Vec2 topLeft = checkBounds.getTopLeftCorner().div(Tile.SIZE.x());
      Vec2 bottomRight = checkBounds.getBottomRightCorner().div(Tile.SIZE.x());
      
      IVec2 tileToCheckStart = new IVec2(
        (int) Math.floor((double) topLeft.x()) - 1,
        (int) Math.floor((double) topLeft.y()) - 1
      );
      IVec2 tileToCheckEnd = new IVec2(
        (int) Math.ceil((double) bottomRight.x()) + 1,
        (int) Math.ceil((double) bottomRight.y()) + 1
      );
      
      for (int y = tileToCheckStart.y(); y < tileToCheckEnd.y(); y++) {
        for (int x = tileToCheckStart.x(); x < tileToCheckEnd.x(); x++) {
          IVec2 coord = new IVec2(x, y);
          Optional<Tile> tileOptional = this.getTileAt(coord);
          if (tileOptional.isEmpty()) {
            continue;
          }
          
          Tile tile = tileOptional.get();
          Vec2 tileCoord = Tile.fromTileCoordToWorldCoord(coord);
          FloatRectangle tileRect = new FloatRectangle(
            tileCoord.sub(Tile.SIZE.mul(0.5f)),
            tileCoord.add(Tile.SIZE.mul(0.5f))
          );
          
          if (e.getWorld() == null || e.getBoxToBeCheckedForTileStep().isEmpty()) {
            // Entity decided that it don't want to collide anymore
            return;
          }
          
          if (tileRect.isIntersects(checkBounds)) {
            e.onTileStep(tile, coord);
            tile.steppedBy(e, coord);
          }
        }
      }
    }

    for (Entity e : this.entities.values()) {
      e.tick(deltaTime);
    }

    for (int i = 0; i < 5; i++) {
      for (Entity e : this.entities.values()) {
        checkCollision(e);
        
        if (e.getWorld() == null || e.getCollisionBox().isEmpty()) {
          // Entity decided that it don't want colliding anymore
          continue;
        }
      }
    }
  }

  protected void renderEntities(Graphics2D g, float deltaTime) {
    for (Entry<IVec2, Tile> coordAndTile : this.tiles.entrySet()) {
      coordAndTile.getValue().render(g, deltaTime, coordAndTile.getKey());
    }

    for (Entity e : this.entities.values()) {
      e.render(g, deltaTime);
      
      if (this.getGame().isDebugEnabled()) {
        // Render direction which the entity viewing
        IVec2 start = this.game.getCamera().translateWorldToAWTGraphicsCoord(e.getPos()).round();
        IVec2 end = this.game.getCamera().translateWorldToAWTGraphicsCoord(e.getPos().add(Vec2.unitVectorOfAngle(e.getRotation()).mul(50.0f))).round();
        
        Stroke oldStroke = g.getStroke();
        
        g.setColor(Color.GREEN);
        g.setStroke(new BasicStroke(5.0f * this.game.getRenderScale()));
        g.drawLine(
          start.x(),
          start.y(),
          end.x(),
          end.y()
        );

        g.setStroke(oldStroke);
      }
    }
  }

  public Optional<Tile> getTileAt(IVec2 pos) {
    return Optional.ofNullable(this.tiles.get(pos));
  }

  public void render(Graphics2D g, float deltaTime) {
    this.renderEntities(g, deltaTime);
  }
  
  public void tick(float deltaTime) {
    this.tickEntities(deltaTime);
  }
  
  public Vec2 validatePos(Vec2 pos) {
    return this.getWorldBound().clampCoordinate(pos);
  }
  
  public boolean isValidPos(Vec2 pos) {
    return this.getWorldBound().contains(pos);
  }
  
  public Stream<Entity> findEntities(Vec2 pos, float radius)  {
    return this.entities.values()
      .stream()
      .filter(e -> {
        if (e.getCollisionBox().isEmpty()) {
          return false;
        }
        
        FloatRectangle box = e.getCollisionBox().get().asRect();
        Vec2 topLeft = box.getTopLeftCorner();
        Vec2 bottomRight = box.getBottomRightCorner();
        Vec2 bottomLeft = new Vec2(topLeft.x(), bottomRight.y());
        Vec2 topRight = new Vec2(bottomRight.x(), topLeft.y());
        
        if (topLeft.sub(pos).magnitude() <= radius) {
          return true;
        } else if (topRight.sub(pos).magnitude() <= radius) {
          return true;
        } else if (bottomLeft.sub(pos).magnitude() <= radius) {
          return true;
        } else if (bottomRight.sub(pos).magnitude() <= radius) {
          return true;
        } else if (e.getPos().sub(pos).magnitude() <= radius) {
          return true;
        }
        
        return false;
      });
  }
}
