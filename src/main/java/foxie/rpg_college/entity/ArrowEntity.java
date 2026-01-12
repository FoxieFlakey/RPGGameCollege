package foxie.rpg_college.entity;

import java.awt.Graphics2D;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.damage.ProjectileDamageSource;
import foxie.rpg_college.texture.Texture;

public class ArrowEntity extends ProjectileEntity {
  // Ukuran arrow yang tertampil sebenarnya lebih besar dari kotak tabraknnya :3
  private static final Vec2 RENDER_SIZE = new Vec2(12.0f, 28.0f).mul(2.5f);
  private static final Vec2 COLLISION_SIZE = new Vec2(5.0f, 5.0f);
  
  // berat arrow dibuat sangat ringan karena arrow ringan relatif
  // banyak target
  private CollisionBox collisionBox = new CollisionBox(0.0001f, new Vec2(0.0f, 0.0f), ArrowEntity.COLLISION_SIZE);
  
  private float damage = 5.0f;
  
  private final Texture arrowTexture;
  
  public ArrowEntity(Game game, Entity shooter) {
    super(game, shooter, 5.0f, 400.0f);
    this.arrowTexture = game.getTextureManager().getTexture("entity/arrow");
  }
  
  @Override
  public boolean canBeHit(Entity other) {
    if (other instanceof LivingEntity) {
      LivingEntity living = (LivingEntity) other;
      if (living.isDead()) {
        // Dont collide with dead entity
        // -------------------------------
        // Entity yang sudah mati tidak bisa
        // ditabrak
        return false;
      }
      return true;
    }
    
    // Arrow hanya melakukan damage ke entity
    // hidup
    return false;
  }
  
  @Override
  public Optional<CollisionBox> getCollisionBox() {
    if (this.hasProjectileHitSomething()) {
      return Optional.empty();
    }
    return Optional.of(this.collisionBox);
  }
  
  @Override
  public void onHit(Entity other) {
    // Arrow telah menyentuh atau menabrak target
    // ini castnya safe karena onHit tidak akan dipanggil
    // kalau canBeHit returnya false
    //
    // Jika disini dipanggil berarti canBeHit returnya true
    // dan dimethod tersebut yang true hanya kalau 'other'
    // bisa di cast ke LivingEntity
    LivingEntity living = (LivingEntity) other;
    living.doDamage(new ProjectileDamageSource(this, this.damage));
  }
  
  @Override
  public void render(Graphics2D g, float deltaTime) {
    ProjectileHelper.renderProjectile(this, g, this.arrowTexture.image(), ArrowEntity.RENDER_SIZE);
  }
  
  @Override
  public Optional<FloatRectangle> getRenderBound() {
    return Optional.of(EntityHelper.calculateRenderBox(this, ArrowEntity.RENDER_SIZE));
  }

  @Override
  public boolean isVisible(Camera cam) {
    return true;
  }
}
