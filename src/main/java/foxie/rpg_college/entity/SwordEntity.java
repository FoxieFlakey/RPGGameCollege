package foxie.rpg_college.entity;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Util;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.controller.Controller;
import foxie.rpg_college.entity.damage.BasicDamageSource;
import foxie.rpg_college.texture.Texture;
import foxie.rpg_college.tile.Tile;

public class SwordEntity extends Entity {
  private static final Vec2 SIZE = new Vec2(120.0f, 2.63f * 120.0f);
  private static final float Y_OFFSET = SIZE.y() * 0.25f;
  private static final float SWING_DISTANCE = SIZE.y() - Y_OFFSET;
  private static final float SWING_SPEED = 360.0f /* deg/s */;
  
  private final Entity wielder;
  private final float swingStart;
  private final Texture texture;
  private final boolean isClockwise;
  private final float angleDone;
  private final HashSet<Long> damagedEntities = new HashSet<>();
  private final float damage;
  private final Vec2 offset;
  
  private float angleCurrent = 0.0f;
  private boolean doneSwinging = false;
  
  /*
  
  
  
  Jilid hijau
  
  
  
  
  
  
  
  
  */
  
  public SwordEntity(Game game, Entity wielder, float damage, float swingStart, float swingEnd, boolean isClockwise, Vec2 offset) {
    super(game);
    
    if (isClockwise) {
      this.swingStart = Util.normalizeAngle(swingStart);
    } else {
      this.swingStart = Util.normalizeAngle(swingEnd);
    }
    this.angleDone = Math.abs(swingEnd - swingStart);
    
    this.wielder = wielder;
    this.damage = damage;
    this.offset = offset;
    this.isClockwise = isClockwise;
    this.texture = game.getTextureManager().getTexture("entity/sword");
  }
  
  public SwordEntity(Game game, Entity wielder, float damage, float swingStart, float swingEnd, boolean isClockwise) {
    this(game, wielder, damage, swingStart, swingEnd, isClockwise, new Vec2(0.0f));
  }
  
  public boolean isDoneSwinging() {
    return this.doneSwinging;
  }
  
  public void renderSword(Graphics2D g, float deltaTime) {
    AffineTransform transform = EntityHelper.calculateCameraTransform(this);
    transform.translate(-SwordEntity.SIZE.x() * 0.5f, -(SwordEntity.SIZE.y() - Y_OFFSET));
    transform.scale(
      SwordEntity.SIZE.x() / this.texture.width(),
      SwordEntity.SIZE.y() / this.texture.height()
    );
    
    g.drawImage(this.texture.image(), transform, null);
  }
  
  public void updatePos() {
    this.setPos(this.wielder.getPos().add(this.offset));
  }
  
  @Override
  public boolean canCollideWith(Entity other) {
    return false;
  }
  
  @Override
  public void onTileCollision(IVec2 coord, Tile other) {
  }
  
  @Override
  public void onWorldBorderCollision() {
  }
  
  @Override
  public void onEntityCollision(Entity other) {
  }
  
  @Override
  public Optional<CollisionBox> getCollisionBox() {
    return Optional.empty();
  }
  
  @Override
  public boolean isVisible(Camera cam) {
    return true;
  }
  
  @Override
  public Optional<FloatRectangle> getRenderBound() {
    return Optional.empty();
  }
  
  @Override
  public void render(Graphics2D g, float deltaTime) {
    // NOTE: THe actual rendering for sword happened at above, by call from the wielder!
  }
  
  @Override
  public void tick(float deltaTime) {
    if (this.doneSwinging) {
      return;
    }
    
    this.angleCurrent += SwordEntity.SWING_SPEED * deltaTime;
    
    if (this.angleCurrent > this.angleDone) {
      this.angleCurrent = this.angleDone;
      this.doneSwinging = true;
      this.getWorld().removeEntity(this);
      return;
    }
    
    this.updatePos();
    this.setRotation(this.swingStart + this.angleCurrent * (this.isClockwise ? 1.0f : -1.0f));
    
    Iterator<LivingEntity> affectedEntities = this.getWorld().findEntities(this.getPos(), SwordEntity.SWING_DISTANCE)
      .filter(e -> e != this.wielder)
      .filter(e -> e instanceof LivingEntity)
      .map(e -> (LivingEntity) e)
      .filter(e -> !e.isDead())
      .iterator();
    
    while (affectedEntities.hasNext()) {
      LivingEntity affected = affectedEntities.next();
      
      float angleToLookatIt = affected.getPos().sub(this.getPos()).calculateAngle();
      if (Math.abs(this.getRotation() - angleToLookatIt) <= 5.0f) {
        if (!this.damagedEntities.contains(affected.id)) {
          this.damagedEntities.add(affected.id);
          
          // Entity is affected
          affected.doDamage(new BasicDamageSource("sword attack", this.damage));
        }
      }
    }
  }
  
  @Override
  public Optional<FloatRectangle> getBoxToBeCheckedForTileStep() {
    return Optional.empty();
  }
  
  @Override
  public boolean canBeControlled() {
    return false;
  }
  
  @Override
  protected Controller createController() {
    return null;
  }
}
