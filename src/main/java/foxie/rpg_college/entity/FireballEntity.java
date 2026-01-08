package foxie.rpg_college.entity;

import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.damage.EntityDamageSource;
import foxie.rpg_college.texture.Texture;
import foxie.rpg_college.tile.Tile;

public class FireballEntity extends ProjectileEntity {
  private static final Vec2 RENDER_SIZE = new Vec2(150.0f, 1.783f * 150.0f);
  private static final float DAMAGE_INDIRECT_HIT = 70.0f;
  private static final float DAMAGE_DIRECT_HIT = 90.0f;
  private static final float EFFECT_RADIUS = 300.0f;
  
  private final Texture fireballTexture;
  private final CollisionBox collisionBox = new CollisionBox(
    100.0f,
    new Vec2(0.0f, 0.0f),
    new Vec2(150.0f, 150.0f),
    false
  );
  
  public FireballEntity(Game game, Entity shooter) {
    super(game, shooter, 10.0f, 200.0f);
    this.fireballTexture = game.getTextureManager().getTexture("entity/fireball");
  }

  @Override
  public boolean canBeHit(Entity other) {
    return other instanceof LivingEntity && !((LivingEntity) other).isDead();
  }

  @Override
  public void onHit(Entity other) {
    LivingEntity living = (LivingEntity) other;
    living.doDamage(new EntityDamageSource(this, FireballEntity.DAMAGE_DIRECT_HIT));
    this.doAreaOfEffect(other.getPos(), living);
  }
  
  void doAreaOfEffect(Vec2 pos, Entity avoidThis) {
    Iterator<Entity> entitiesInEffect = this.getWorld()
      .findEntities(pos, EFFECT_RADIUS)
      .iterator();
    
    while (entitiesInEffect.hasNext()) {
      Entity current = entitiesInEffect.next();
      if (!(current instanceof LivingEntity) || current == avoidThis || current == this.getShooter()) {
        // Entity is either not living or its the same one we already damaged
        // or its the shooter
        continue;
      }
      LivingEntity currentLiving = (LivingEntity) current;
      float multiplier = 1.0f - (EntityHelper.distanceBetween(this, currentLiving) / FireballEntity.EFFECT_RADIUS);
      currentLiving.doDamage(new EntityDamageSource(this, FireballEntity.DAMAGE_INDIRECT_HIT * multiplier));
    }
  }
  
  @Override
  public void onWorldBorderCollision() {
    super.onWorldBorderCollision();
    this.doAreaOfEffect(this.getPos(), null);
    this.die();
  }
  
  @Override
  public void onTileCollision(IVec2 coord, Tile other) {
    super.onTileCollision(coord, other);
    this.doAreaOfEffect(this.getPos(), null);
    this.die();
  }

  @Override
  public Optional<CollisionBox> getCollisionBox() {
    return Optional.of(this.collisionBox);
  }

  @Override
  public boolean isVisible(Camera cam) {
    return true;
  }

  @Override
  public Optional<FloatRectangle> getRenderBound() {
    return Optional.of(EntityHelper.calculateRenderBox(this, FireballEntity.RENDER_SIZE));
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
    ProjectileHelper.renderProjectile(this, g, this.fireballTexture.image(), FireballEntity.RENDER_SIZE);
  }
}
