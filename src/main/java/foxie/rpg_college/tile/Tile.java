package foxie.rpg_college.tile;

import java.awt.Graphics2D;

import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.Entity;

// A tile instance is reused multiple time
// for differing positions
// ----------------------------------------
// Tile adalah dasar kelas yang digunakan untuk
// tile-tile di dunia posisi nya dalam bentuk integer
// yang berkelipatan dari Tile.SIZE. Kelas ini hanya
// dikonstruksi sekali untuk tiap game. Kalau ada dua
// variant tile yang same tetapi bervariase sedikit
// bisa dikonstruksi lagi seperti contohnya portal
//
// Portal dapat memliki beberapa tujuan berbeda jadi
// portal dapat dikonstruksi dengan tujuan dunia yang
// berbeda
public abstract class Tile {
  // Tiles are 64 x 64 pixels in size
  public static final Vec2 SIZE = new Vec2(64.0f, 64.0f);

  protected final Game game;

  // Menyimpan nama dari tile, untuk sekarang ini
  // menggunakan reflection di Java untuk mengakses
  // nama kelas concrete yang jadi namanya. Kalau tile
  // ada nama yang berbeda method getName bisa dioverride
  // sesuai keperluan
  private String name = this.getClass().getSimpleName();

  public Tile(Game game) {
    this.game = game;
  }
  
  public final Game getGame() {
    return this.game;
  }
  
  public String getName(IVec2 coord) {
    return this.name;
  }

  // Sebuah method static pembantu yang mengubah koordinat
  // tile menjadi koordinat di dunia. Biasanya digunakan
  // untuk merender karena render bekerja pada koordinat
  // dunia bukan koordinat tile
  public static Vec2 fromTileCoordToWorldCoord(IVec2 coord) {
    return new Vec2(
      (float) coord.x() * Tile.SIZE.x(),
      (float) coord.y() * Tile.SIZE.y()
    );
  }
  
  // Method ini dipanggil dari kelas dunia jika mendeteksi
  // kalau tile di-injak oleh sebuah entity. Jika entity
  // bertahan dan berdiri di tile yang sama, method ini dipanggil
  // tiap tick walaupun sudah dipanggil sebelumnya. Subclass
  // tile harus menghandle-nya
  public abstract void steppedBy(Entity e, IVec2 coord);

  // Beberapa tile tidak bisa ditabrak, dan ada yang
  // bisa jadi methodnya abstrak karena tidak ada jawaban
  // default yang bisa diterima
  public abstract boolean isCollisionEnabled();

  // Mereturn apakah method tick perlu dipanggil
  // beberapa tile ada logika yang dijalankan tiap detik
  // ada yang tidak contohnya WallTile, dinding tidak memiliki
  // fungsi yang dijalankan di tick, karena tentunya dinding
  // tetap dinding tidak melakukan apa-apa
  public abstract boolean canBeTicked();

  // Render menggambar tile dan tick menjalan logika
  // seolah-olah 'deltaTime' telah lewat
  public abstract void render(Graphics2D g, float deltaTime, IVec2 position);
  public abstract void tick(float deltaTime, IVec2 position);
}
