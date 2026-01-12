package foxie.rpg_college.entity;

import java.awt.Graphics2D;
import java.awt.image.RescaleOp;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.damage.DamageSource;
import foxie.rpg_college.entity.damage.ProjectileDamageSource;
import foxie.rpg_college.texture.Texture;

public class ArcherCharacter extends HeroCharacter implements Attackable, Defenseable {
  private static final Vec2 SIZE = new Vec2(
    140.0f,
    200.0f
  );
  private static final float ATTACK_MANA_POINT = 7.0f;
  
  private static final float MINIMAL_MANA_POINT_TO_DODGE = 50.0f;
  private static final float MINIMAL_REDUCED_ARROW_DAMAGE_MANA_POINT = 30.0f;
  private static final float DODGE_MANA_POINT = 15.0f;
  private static final float REDUCE_ARROW_DAMAGE_MANA_POINT = 10.0f;

  private final CollisionBox collisionBox = new CollisionBox(10.0f, new Vec2(0.0f, 0.0f), ArcherCharacter.SIZE);
  private float fireArrowCooldown = -1.0f;
  
  private final Texture facingDownTexture;
  private final Texture facingDownTextureFlashed;
  private final Texture deadTexture;
  
  public ArcherCharacter(Game game) {
    super(game);
    this.facingDownTexture = game.getTextureManager().getTexture("character/archer/facing_down");
    this.deadTexture = game.getTextureManager().getTexture("character/archer/dead");
    
    float[] scale = { 2.0f, 1.3f, 1.3f, 1.0f };
    float[] offset = new float[4];
    this.facingDownTextureFlashed = new Texture(new RescaleOp(scale, offset, null).filter(this.facingDownTexture.image(), null));
  }
  
  @Override
  public float getMaxHealth() {
    return 100.0f;
  }
  
  @Override
  public float getMaxManaPoint() {
    return 100.0f;
  }
  
  @Override
  public float getManaRefillRate() {
    return 40.0f;
  }
  
  @Override
  public float getManaRefillPeriod() {
    return 2.0f;
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
    super.render(g, deltaTime);
    
    Texture texture = this.facingDownTexture;
    
    if (this.getFlashState()) {
      texture = this.facingDownTextureFlashed;
    }
    
    if (this.isDead()) {
      texture = this.deadTexture;
    }
    
    FloatRectangle renderBox = EntityHelper.calculateRenderBox(this, ArcherCharacter.SIZE);

    int x = (int) renderBox.getTopLeftCorner().x();
    int y = (int) renderBox.getTopLeftCorner().y();
    int width = (int) renderBox.getSize().x();
    int height = (int) renderBox.getSize().y();
    
    g.drawImage(
      texture.image(),
      x,
      y,
      x + width,
      y + height,
      0,
      0,
      texture.width(),
      texture.height(),
      null
    );
  }

  @Override
  public boolean isVisible(Camera cam) {
    // Player is always visible
    return true;
  }

  @Override
  public Optional<CollisionBox> getCollisionBox() {
    return Optional.of(this.collisionBox);
  }

  @Override
  public boolean canCollideWith(Entity other) {
    return true;
  }

  @Override
  public FloatRectangle getLegBox() {
    FloatRectangle collision = this.collisionBox.asRect();
    Vec2 topLeftCollision = collision.getTopLeftCorner();
    Vec2 bottomRightCollision = collision.getBottomRightCorner();
    
    return new FloatRectangle(
      new Vec2(
        topLeftCollision.x(),
        bottomRightCollision.y() - ArcherCharacter.SIZE.y() * 0.5f
      ),
      bottomRightCollision
    );
  }
  
  @Override
  public float getMovementSpeed() {
    return 100.0f;
  }
  
  @Override
  public void tick(float deltaTime) {
    super.tick(deltaTime);
    this.fireArrowCooldown -= deltaTime;
    if (this.fireArrowCooldown < 0.0f) {
      this.fireArrowCooldown = -1.0f;
    }
  }
  
  @Override
  public boolean attack() {
    if (!this.canAttack()) {
      return false;
    }
    
    if (!this.consumeManaPoint(ArcherCharacter.ATTACK_MANA_POINT)) {
      return false;
    }
    
    this.fireArrowCooldown = 0.1f;
    
    // Spawn arrow
    ArrowEntity arrow = new ArrowEntity(this.getGame(), this);
    this.getWorld().addEntity(arrow);
    arrow.setPos(this.getPos());
    arrow.setRotation(this.getRotation());
    
    return true;
  }

  @Override
  public boolean canAttack() {
    return this.fireArrowCooldown < 0.0f && this.getManaPoint() >= ArcherCharacter.ATTACK_MANA_POINT && !this.isDead();
  }
  
  @Override
  public boolean canDefense() {
    return
      this.getManaPoint() >= ArcherCharacter.MINIMAL_MANA_POINT_TO_DODGE ||
      this.getManaPoint() >= ArcherCharacter.MINIMAL_REDUCED_ARROW_DAMAGE_MANA_POINT;
  }
  
  @Override
  public void defend(DamageSource source) {
    if (source instanceof ProjectileDamageSource) {
      ProjectileDamageSource entitySource = (ProjectileDamageSource) source;
      if (entitySource.getProjectile() instanceof ArrowEntity) {
        if (this.getManaPoint() >= ArcherCharacter.MINIMAL_MANA_POINT_TO_DODGE) {
          if (this.consumeManaPoint(ArcherCharacter.DODGE_MANA_POINT)) {
            source.setDamagePoint(0.0f);
          }
        } else if (this.getManaPoint() >= ArcherCharacter.MINIMAL_REDUCED_ARROW_DAMAGE_MANA_POINT) {
          if (this.consumeManaPoint(ArcherCharacter.REDUCE_ARROW_DAMAGE_MANA_POINT)) {
            // Take 75% less damage from arrows. So only 25% of
            // damage is inflicted
            source.setDamagePoint(source.getDamagePoint() * 0.25f);
          }
        }
      }
    }
  }
  
  @Override
  public Optional<FloatRectangle> getRenderBound() {
    return Optional.of(EntityHelper.calculateRenderBox(this, ArcherCharacter.SIZE));
  }
}
