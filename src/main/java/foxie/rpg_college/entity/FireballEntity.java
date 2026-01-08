package foxie.rpg_college.entity;

import java.awt.Graphics2D;
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
  private static final Vec2 RENDER_SIZE = new Vec2(100.0f, 178.3f);
  private static final float DAMAGE = 70.0f;
  
  private final Texture fireballTexture;
  private final CollisionBox collisionBox = new CollisionBox(
    100.0f,
    new Vec2(0.0f, 0.0f),
    new Vec2(100.0f, 100.0f),
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
    living.doDamage(new EntityDamageSource(this, FireballEntity.DAMAGE));
  }
  
  @Override
  public void onTileCollision(IVec2 coord, Tile other) {
    super.onTileCollision(coord, other);
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
