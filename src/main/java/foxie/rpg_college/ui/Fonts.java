package foxie.rpg_college.ui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.Optional;

import foxie.rpg_college.Util;

public class Fonts {
  private static Font defaultFont;

  public static Font getDefault() {
    return Optional.ofNullable(Fonts.defaultFont).get();
  }

  public static int calcYOffsetSoItsCenter(Graphics2D g) {
    return Fonts.calcYOffsetSoItsCenter(g, g.getFont());
  }

  public static int calcYOffsetSoItsCenter(Graphics2D g, Font font) {
    FontMetrics metrics = g.getFontMetrics(font);
    int height = metrics.getHeight();
    int ascent = metrics.getAscent();
    
    return ascent - (height / 2);
  }

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
