package foxie.rpg_college.ui;

import java.awt.Graphics2D;

import foxie.rpg_college.Game;

// Screen adalah kelas abstrak yang menrepresentasikan
// layar dalam game. Tiap screen itu terikat ke sebuah
// game
public abstract class Screen {
  private final Game game;

  public Screen(Game game) {
    this.game = game;
  }

  public Game getGame() {
    return this.game;
  }

  // Return true to passthrough game
  // a.k.a such as clicks happens in
  // empty zone where there no UI element
  // -------------------------------------
  // Method handleInput, dipanggil game untuk
  // memeriksa apakah screen ingin menghandle
  // input. Jika return true, maka input
  // akan ditangangi oleh kode berikutnya
  public abstract boolean handleInput();
  
  // Method ini bertugas untuk memajukan
  // "state" internal jika ada animasi atau
  // logika yang bergantung pada waktu
  public abstract void tick(float deltaTime);
  
  // method ini memanggil implementasi layar
  // untuk menampilkan UI-nya
  public abstract void render(Graphics2D g, float deltaTime);
  
  // Method ini memberitahu pada Game jika logika
  // dalam game dapat dijalankan. Seperti contohnya
  // pause screen tentunya mempause game jadi
  // returnya false. Untuk menpause game
  public abstract boolean canTickGame();
}

