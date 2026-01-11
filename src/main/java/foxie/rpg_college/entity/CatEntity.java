package foxie.rpg_college.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.tile.Tile;

public class CatEntity extends LivingEntity implements Attackable {
  // Ukuran kucing nya
  private static final Vec2 SIZE = new Vec2(Tile.SIZE.x() * 0.7f, Tile.SIZE.x() * 0.7f);
  
  // Damage pedang
  private static final float SWORD_DAMAGE = 35.0f;
  
  private final CollisionBox collisionBox = new CollisionBox(1.0f, new Vec2(0.0f, 0.0f), CatEntity.SIZE);
  
  // Kucing ini entah mengapa memiliki pedang :3
  // ini menyimpan pedang yand sedang aktif
  private Optional<SwordEntity> sword = Optional.empty();
  
  public CatEntity(Game game) {
    super(game);
  }
  
  @Override
  public float getMaxHealth() {
    return 20.0f;
  }

  @Override
  public boolean canCollideWith(Entity other) {
    return true;
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
  public void render(Graphics2D g, float deltaTime) {
    super.render(g, deltaTime);
    
    // Kode disini basic saja hanya menggambar kotak
    // sebesar ukuran yang diinginkan lalu mengganti
    // warna tergantung getFlashState dan warna lain
    // kalau mati
    FloatRectangle renderBox = EntityHelper.calculateRenderBox(this, CatEntity.SIZE);

    int x = (int) renderBox.getTopLeftCorner().x();
    int y = (int) renderBox.getTopLeftCorner().y();
    int width = (int) renderBox.getSize().x();
    int height = (int) renderBox.getSize().y();

    Color color = new Color(0.00f, 0.00f, 0.60f, 1.00f);
    if (this.getFlashState()) {
      color = new Color(0.00f, 0.00f, 0.90f, 1.00f);
    }

    if (this.isDead()) {
      color = new Color(0.00f, 0.00f, 0.30f, 1.00f);
    }

    g.setColor(color);
    g.fillRoundRect(
      x, y,
      width, height,
      5, 5
    );
    
    if (this.sword.isPresent() && !this.sword.get().isDoneSwinging()) {
      // Method render pada sword tidak melakukan apa-apa karena hashmap
      // yang saya pakai iterasi nya tidak tertentu -w-
      // Pedang perlu render diatas entity selalu
      this.sword.get().renderSword(g, deltaTime);
    }
  }

  @Override
  public FloatRectangle getLegBox() {
    // Satu collision box nya adalah kakinya
    // jadi tidak perlu kalkulasi apa-apa
    return this.collisionBox.asRect();
  }
  
  @Override
  public Vec2 getLegPos() {
    // Letak kaki nya dibuat tepat dibawah
    return new Vec2(
      this.getPos().x(),
      this.getPos().y() - CatEntity.SIZE.y() * 0.5f
    );
  }
  
  @Override
  public float getMovementSpeed() {
    return 200.0f;
  }
  
  @Override
  public Optional<FloatRectangle> getRenderBound() {
    return Optional.of(EntityHelper.calculateRenderBox(this, CatEntity.SIZE));
  }

  @Override
  public boolean canAttack() {
    return this.sword.isEmpty() || this.sword.get().isDoneSwinging();
  }

  @Override
  public boolean attack() {
    if (!this.canAttack()) {
      return false;
    }
    
    // Method ini melakukan attack untuk kucing
    
    boolean isClockwise;
    
    // Berdasarkan arah pedang entah putar searah jam
    // atau berlawanan jam
    switch (this.getOrientation()) {
      case Right:
      case Down:
      case Up:
      default:
        isClockwise = true;
        break;
      case Left:
        isClockwise = false;
        break;
    }
    
    // Lalu buat entity pedang baru dan
    // membuat nya mengayun dari sudut sekarang - 80.0f
    // ke sudut sekarang + 80.0f
    // dan pedangnya agak lebih jauh dari tengah
    SwordEntity sword = new SwordEntity(
      this.getGame(),
      this,
      CatEntity.SWORD_DAMAGE,
      this.getRotation() - 80.0f,
      this.getRotation() + 80.0f,
      isClockwise,
      Vec2.unitVectorOfAngle(this.getRotation())
        .mul(
          Float.min(
            CatEntity.SIZE.x(),
            CatEntity.SIZE.y()
          ) / 2.0f - 25.0f
        )
    );
    this.getWorld().addEntity(sword);
    sword.updatePos();
    
    // Simpan pedang nya biar bisa diperiksa
    this.sword = Optional.of(sword);
    return true;
  }
}
