package foxie.rpg_college;

import java.util.List;
import java.util.Arrays;

/**
 * Hello world!
 */
public class Main {
  public static void main(String[] args) {
    System.out.println("Hello World!");
    List<String> argsList = Arrays.asList(args);
    
    try (Game game = new Game()) {
      if (argsList.contains("fullscreen")) {
        game.setFullscreen(true);
      }
      
      if (argsList.contains("double_buffering")) {
        game.setDoubleBuffer(true);
      }
      
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


