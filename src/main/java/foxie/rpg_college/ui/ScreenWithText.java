package foxie.rpg_college.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;

import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;

// Subclass dari Screen yang menghandle menampilkan
// teks dilayar dan method lain dibiarkan. Subclass dair
// ini ada HelpScreen dan DeathScreen yang dua-duanya
// ingin menampilkan teks tetapi tidak ingin menduplikatkan
// kode
public abstract class ScreenWithText extends Screen {
  public ScreenWithText(Game game) {
    super(game);
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
    // Mengambil skala gamenya yaitu sebesar apa teks dirender
    // relatif dengan output sebenarnya karena koordinat yang
    // dipakai statis di 1280.0f x 720.0f jadi jika output lebih
    // besar maka skala tentunya lebih besar
    float renderScale = this.getGame().getCamera().getScale().x();
    float width = this.getGame().getOutputWidth();
    float height = this.getGame().getOutputHeight();
    
    // Menghitung dimana teks mulai dan berakhir
    Vec2 textStart = new Vec2(width * 0.10f, height * 0.10f);
    Vec2 textEnd = new Vec2(width * 0.90f, height * 0.90f);
    
    // Pertama merender background
    g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.60f));
    g.fillRect(
      (int) textStart.x(),
      (int) textStart.y(),
      (int) (textEnd.x() - textStart.x()),
      (int) (textEnd.y() - textStart.y())
    );
    
    // Setelah itu merender teks
    g.setColor(Color.WHITE);
    g.setFont(Fonts.getDefault().deriveFont(30.0f * renderScale));
    
    // Karena method drawString pada java.awt.Graphics2D tidak
    // memeriksa newline, maka kita perlu manual
    Iterator<String> lines = this.getText().lines().iterator();
    int lineIndex = 0;

    // Hitung tinggi baris
    int lineHeight = Fonts.getFontHeight(g);
    
    // Lalu mengiterasi tiap baris dengan iterator
    while (lines.hasNext()) {
      String line = lines.next();

      // Setelah itu gambar teks nya
      g.drawString(
        line,
        textStart.x() + (10.0f * renderScale),
        textStart.y() + (30.0f * renderScale) + lineIndex * lineHeight
      );
      
      lineIndex++;
    }
  }
  
  // Definisi method abstrak getText untuk mengambil
  // teks apa yang akan ditampilkan
  public abstract String getText();
}
