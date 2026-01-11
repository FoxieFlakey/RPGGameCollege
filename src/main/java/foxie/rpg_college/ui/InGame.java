package foxie.rpg_college.ui;

import java.awt.Graphics2D;
import java.util.Optional;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.texture.Texture;

// Screen ini menampilkan berbagai HUD untuk menampilkan
// berbagai informasi ke player saat dalam game. Awalnya
// 
// HUD juga menampilkan health, mana player tetapi saya
// hapus karena kodenya berserakan sama redundan karena
// tiap LivingEntity sama CharacterEntity menampilkan
// mana dan HP diatas entity nya.
//
// Fungsi yang ada hanya menampilkan "selected entity" yang
// menunjukkan entity yang player sedang kendalikan
public class InGame extends Screen {
  // Berapa lama crosshair akan mengadjust posisinya
  // untuk sebuah animasi kecil saat entity yang
  // dikendalikan berganti
  //
  // Animasi dapat dilihat saat game baru mulai atau
  // respawn setelah mati.
  private static final float BEAT_TIME = 0.2f;
  
  // Empat gambar untuk tiap-tiap sudut "selected entity"
  // dari kiri atas, sampai kana bawah
  private final Texture topLeftCorner;
  private final Texture topRightCorner;
  private final Texture bottomRightCorner;
  private final Texture bottomLeftCorner;
  
  // Menyimpan entity yang sedang di kendalikan
  // 
  // Optional juga sebuah kelas yang Java memiliki
  // yang memudahkan pemeriksaan apakah objek ada
  // atau tidak dibanding memakai value null.
  private Optional<Entity> currentEntity = Optional.empty();
  private float beatTimeLeft = 0.0f;
  
  public InGame(Game game) {
    super(game);
    this.topLeftCorner = game.getTextureManager().getTexture("ui/in_game/corner_top_left");
    this.topRightCorner = game.getTextureManager().getTexture("ui/in_game/corner_top_right");
    this.bottomRightCorner = game.getTextureManager().getTexture("ui/in_game/corner_bottom_right");
    this.bottomLeftCorner = game.getTextureManager().getTexture("ui/in_game/corner_bottom_left");
  }

  @Override
  public boolean handleInput() {
    // HUD game tidak memiliki button/atau apapun
    // yang memeriksa input, jadi lanjutkan ke
    // input yang menghandle game
    return true;
  }

  // Tick kodenya adalah memajukan animasi untuk beat
  // dan memulai animasi baru jika entity yang dikendalikan
  // berganti
  @Override
  public void tick(float deltaTime) {
    Optional<Entity> maybeEntity = this.getGame().getPlayer();
    if (!this.currentEntity.equals(maybeEntity)) {
      this.beatTimeLeft = InGame.BEAT_TIME;
    } else {
      this.beatTimeLeft -= deltaTime;
    }
    this.currentEntity = maybeEntity;
    
    if (this.beatTimeLeft < 0.0f) {
      this.beatTimeLeft = 0.0f;
    }
  }
  
  @Override
  public void render(Graphics2D g, float deltaTime) {
    Optional<Entity> maybeEntity = this.getGame().getPlayer();
    float offsetMultiplier = this.beatTimeLeft / InGame.BEAT_TIME;
    
    if (maybeEntity.isEmpty()) {
      // Entity tidak ada, jadi jangan dirender
      return;
    }
    
    Entity e = maybeEntity.get();

    // Mengambil kotak yang berisi entity nya
    // atau hardcode ukuran 100x100 karena
    // tidak ada kotak berisi entity nya
    FloatRectangle renderBox = e.getRenderBoundInWorld().orElseGet(() -> {
      return new FloatRectangle(
        e.getPos().add(new Vec2(-100.0f)),
        e.getPos().add(new Vec2(100.0f))
      );
    });
    
    // Setelah itu menghitung koordinat untuk
    // tiap sudut lalu dirender
    Vec2 cornerSize = new Vec2(20.0f);
    Vec2 topLeft = renderBox.getTopLeftCorner()
      .sub(new Vec2(5.0f))
      .sub(new Vec2(20.0f * offsetMultiplier));
    Vec2 bottomRight = renderBox.getBottomRightCorner()
      .add(new Vec2(5.0f))
      .add(new Vec2(20.0f * offsetMultiplier));
    
    Vec2 topRight = new Vec2(bottomRight.x(), topLeft.y());
    Vec2 bottomLeft = new Vec2(topLeft.x(), bottomRight.y());
    
    // Draw corners
    this.drawCorner(g, topLeft, cornerSize, this.topLeftCorner);
    this.drawCorner(g, topRight, cornerSize, this.topRightCorner);
    this.drawCorner(g, bottomRight, cornerSize, this.bottomRightCorner);
    this.drawCorner(g, bottomLeft, cornerSize, this.bottomLeftCorner);
  }
  
  void drawCorner(Graphics2D g, Vec2 pos, Vec2 size, Texture cornerTexture) {
    // Mentranslate posisi dan ukuran corner
    // lalu langsung dipakai render gambar untuk sebuah sudut
    FloatRectangle renderBox = this.getGame().getCamera().translateWorldToAWTGraphicsCoord(pos, size);
    
    int x = (int) renderBox.getTopLeftCorner().x();
    int y = (int) renderBox.getTopLeftCorner().y();
    int width = (int) renderBox.getSize().x();
    int height = (int) renderBox.getSize().y();
    g.drawImage(
      cornerTexture.image(),
      x, y,
      x + width, y + height,
      0, 0,
      cornerTexture.width(), cornerTexture.height(),
      null
    );
  }
  
  @Override
  public boolean canTickGame() {
    // karena InGame adalah HUD yang menampilkan keadaan
    // game saat berjalan maka return true selalu
    return true;
  }
}
