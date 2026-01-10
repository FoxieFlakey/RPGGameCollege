package foxie.rpg_college.ui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.Optional;

import foxie.rpg_college.Util;

// Kelas ini tidak memiliki apapun
// hanya banyak variabel statis yang
// menyimpan font-font yang telah
// di perlukan program. Unutuk sekarang
// hanya satu font.
public class Fonts {
  private static Font defaultFont;

  // Ini method mengreturn font yang "default"
  // bagi pemanggil yang tidak perlu font apa
  // apa. hanya ingin menulis teks, Meow~
  public static Font getDefault() {
    return Optional.ofNullable(Fonts.defaultFont).get();
  }
  
  // Method ini menghitung tinggi font, atau bisa di
  // intepretasi sebagai jarak antara baris :3
  public static int getFontHeight(Graphics2D g, Font font) {
    return g.getFontMetrics(font).getHeight();
  }
  
  // Versi ini sama dengan getFontHeight
  // tetapi mengambil font dengan g.getFont()
  // method tersebut mengambil font apa yang diset
  // oleh g.setFont() sebelumnya
  public static int getFontHeight(Graphics2D g) {
    return Fonts.getFontHeight(g, g.getFont());
  }

  // Versi ini sama dengan calcYOffsetSoItsCenter
  // tetapi mengambil font dengan g.getFont()
  // method tersebut mengambil font apa yang diset
  // oleh g.setFont() sebelumnya
  public static int calcYOffsetSoItsCenter(Graphics2D g) {
    return Fonts.calcYOffsetSoItsCenter(g, g.getFont());
  }

  // Fungsi ini mengambil Graphics2D dan Font lalu menghitung
  // berapa banyak offset Y diperlukan sehingga jika digambar
  // menggunakan graphics yang diberikan koordinat Y yang dikasih
  //
  // g.drawString("string", x, y + offset);
  //
  // Itu menunjuk 'tengah' hurufnya bukan "baseline". Baseline
  // disini adalah titik dimana hurus dimulai seperti a,b,c,d,e,f,h,i,..
  // terleetak pada baseline, sedangkan g, j, p, q, ... melebihi garis
  // baseline.
  public static int calcYOffsetSoItsCenter(Graphics2D g, Font font) {
    FontMetrics metrics = g.getFontMetrics(font);
    int height = metrics.getHeight();
    int ascent = metrics.getAscent();
    
    return ascent - (height / 2);
  }

  // Ini merupakan blok static, di Java ada tersedia blok static
  // yang digunakan untuk menjalakan kode saat kelas diload.
  //
  // Java hanya menload kelas yang dibutuhkan atau lazy-loading
  // atau on demand. Dengan kode seperti ini kita dapat menjalankan
  // kode yang lebih panjang untuk mengisi variabel-variabel statik
  // seperti dalam ini mengisi variabel defaultFont dan menload
  // font nya, method-method yang mengakses defaultFont tidak perlu
  // memeriksa nya apakah sudah diload.
  static {
    Font font;
    try {
      font = Font.createFont(Font.TRUETYPE_FONT, Util.getResource("/DejaVuSans.ttf").openStream());
    } catch (FontFormatException | IOException e) {
      throw new RuntimeException("Cannot load default font", e);
    }

    Fonts.defaultFont = font;
  };
}
