package foxie.rpg_college;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;

/**
 * Hello world!
 */
// Kelas main ini cukup sederhana saja menparse command line
// argument untuk mengsetting beberapa hal
public class Main {
  public static void main(String[] args) {
    // args adalah array yang berisi argument-arugment command line
    // yang diberikan saat menjalan jar melalui command line
    //
    // java -jar 'game.jar' <args>
    //
    // 'args' akan berisi args yang berasal dari command di atas
    
    System.out.println("Hello World!");
    
    Optional<Float> scaling = Optional.empty();
    boolean isFullscreen = false;
    boolean doubleBuffer = true;
    for (String arg : args) {
      if (arg.startsWith("scaling=")) {
        String argVal = arg.replaceFirst("^scaling=", "");
        scaling = Optional.of(Float.parseFloat(argVal));
      }
      
      if (arg.equals("fullscreen")) {
        isFullscreen = true;
      }
      
      if (arg.equals("no_double_buffering")) {
        doubleBuffer = false;
      }
    }
    
    if (scaling.isPresent() && !doubleBuffer) {
      System.out.println("Scaling render implies double buffering!");
      doubleBuffer = true;
    }
    
    // Pertaama ada syntax Java bername try-use
    // ini berguna untuk melepaskan resource-resource
    // yang dipakai oleh sebuah objek secara otomatis
    // saat keluar dari try block
    //
    // Disini program membuat kelas game yang berisi
    // state game yang diperlukan lalu juga menload
    // hal-hal lain yang dipakai di game.
    try (Game game = new Game()) {
      if (isFullscreen) {
        game.setFullscreen(true);
      }
      
      if (doubleBuffer) {
        game.setDoubleBuffer(true);
      }
      
      if (scaling.isPresent()) {
        game.setRenderScale(scaling.get());
      }
      
      // Setelah ini hampir game memiliki loop
      // ini yang menjalankan logika-logika game
      // dan juga merender selama game belum
      // ditutup
      while (!game.isClosed()) {
        game.runOnce();

        try {
          Thread.sleep(1000 / 60);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    } catch (Exception e) {
      System.err.println("Error cleaning up game:");
      e.printStackTrace();
    }
  }
}


