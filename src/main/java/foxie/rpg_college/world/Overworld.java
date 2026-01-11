package foxie.rpg_college.world;

import java.awt.Graphics2D;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.texture.Texture;

// Salah satu contoh dunia yang ada dalam game adalah
// overworld, overworld adalah dunia yang tidak terlalu
// memiliki tujuan tertentu untuk saat ini -w- Hanya
// ada agar bisa mentest berbagai fitur digame -w-
//
// Dunia ini juga dunia yang pertama ditambahkan kegame
// init :3
public class Overworld extends World {
  private final Texture backgroundTexture;
  
  public Overworld(Game game) {
    // Untuk sederhana ambil ukuran dunia
    // sebesar texturenya saja
    super(game, new FloatRectangle(
      new Vec2(
        -game.getTextureManager().getTexture("world/overworld/background").width() / 2.0f,
        -game.getTextureManager().getTexture("world/overworld/background").height() / 2.0f
      ),
      new Vec2(
        game.getTextureManager().getTexture("world/overworld/background").width() / 2.0f,
        game.getTextureManager().getTexture("world/overworld/background").height() / 2.0f
      )
    ));
    
    this.backgroundTexture = game.getTextureManager().getTexture("world/overworld/background");

    // Menambahkan beberapa tiles basic saja
    // seperti dinding dan membentuk kira-kira
    // seperiti rumah untuk mentest tabrakan saat
    // dunia ini pertama ditambahkan
    this.addTile(new IVec2(6, 0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(5, 0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(4, 0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(3, 0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(2, 0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(1, 0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(0, 0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(0, 1), game.TILES.WALL_TILE);
    // Gap
    // Gap
    // Gap
    // Gap
    this.addTile(new IVec2(0, 6), game.TILES.WALL_TILE);
    this.addTile(new IVec2(0, 7), game.TILES.WALL_TILE);
    this.addTile(new IVec2(0, 8), game.TILES.WALL_TILE);
    this.addTile(new IVec2(1, 8), game.TILES.WALL_TILE);
    this.addTile(new IVec2(2, 8), game.TILES.WALL_TILE);
    this.addTile(new IVec2(3, 8), game.TILES.WALL_TILE);
    this.addTile(new IVec2(4, 8), game.TILES.WALL_TILE);
    this.addTile(new IVec2(5, 8), game.TILES.WALL_TILE);
    this.addTile(new IVec2(6, 8), game.TILES.WALL_TILE);
    this.addTile(new IVec2(6, 7), game.TILES.WALL_TILE);
    this.addTile(new IVec2(6, 6), game.TILES.WALL_TILE);
    this.addTile(new IVec2(6, 5), game.TILES.WALL_TILE);
    this.addTile(new IVec2(6, 4), game.TILES.WALL_TILE);
    this.addTile(new IVec2(6, 3), game.TILES.WALL_TILE);
    this.addTile(new IVec2(6, 2), game.TILES.WALL_TILE);
    this.addTile(new IVec2(6, 1), game.TILES.WALL_TILE);
    this.addTile(new IVec2(7, 1), game.TILES.PORTAL_TO_BATTLE);

    // Add test for lava tile
    // ----------------------
    // Lava tile ditambah juga pada saat dunia ini
    // untuk mentest logika dan fungsi untuk damage dari
    // lava
    this.addTile(new IVec2(5, 5), game.TILES.LAVA_TILE);
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
    // Fungsi render disini basic saja, hanya merender
    // background setelah ini memanggil fungsi yang di
    // kelas World untuk merender hal lain-lainnya
    // seperti entities and tiles di atas dunia
    WorldUtil.renderBackground(this, g, this.backgroundTexture.image());
    super.render(g, deltaTime);
  }
  
  // Method ini tersedia agar portal
  // dan mekanik untuk respawn tau
  // meletakkan player dimana. untuk
  // test menaruh di -300.0f, 0.0f saja
  @Override
  public Vec2 getWorldSpawnPoint() {
    return new Vec2(-300.0f, 0.0f);
  }
}
