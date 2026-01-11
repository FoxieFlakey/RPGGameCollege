package foxie.rpg_college.tile;

import java.awt.Color;
import java.awt.Graphics2D;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.entity.Entity;

// Tile dinding, kelas yang paling sederhana
// tugasnya hanya menjadi dinding UwU
public class WallTile extends Tile {
  public WallTile(Game game) {
    super(game);
  }

  @Override
  public void render(Graphics2D g, float deltaTime, IVec2 position) {
    // Sederhana saja, menghitung kotak pada output yang tile tinggal
    // pakai di Graphics2D, untuk mengambar tile dan posisi tepat juga
    FloatRectangle renderBox = TileHelper.calculateRenderBox(this, position);

    int x = (int) renderBox.getTopLeftCorner().x();
    int y = (int) renderBox.getTopLeftCorner().y();
    int width = (int) renderBox.getSize().x();
    int height = (int) renderBox.getSize().y();

    g.setColor(Color.LIGHT_GRAY);
    g.fillRoundRect(
      x, y,
      width, height,
      15, 15
    );
  }

  @Override
  public void tick(float deltaTime, IVec2 position) {
    // Dinding tidak ada apa-apa yang dilakukan
    // jadi kosong
  }

  @Override
  public boolean isCollisionEnabled() {
    // Dinding tentunya bisa ditabrak -w-
    return true;
  }

  @Override
  public boolean canBeTicked() {
    // Dinding tidak melakukan apa-apa
    // dinding pasif saja jadi false
    return false;
  }
  
  @Override
  public void steppedBy(Entity e, IVec2 coord) {
    // Dinding tidak melakukan apa-apa
    // dinding pasif saja jadi kosong
  }
}
