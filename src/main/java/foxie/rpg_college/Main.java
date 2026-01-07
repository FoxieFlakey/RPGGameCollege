package foxie.rpg_college;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;

/**
 * Hello world!
 */
public class Main {
  public static void main(String[] args) {
    System.out.println("Hello World!");
    List<String> argsList = Arrays.asList(args);
    
    Optional<Float> scaling = Optional.empty();
    boolean isFullscreen = false;
    boolean doubleBuffer = false;
    for (String arg : argsList) {
      if (arg.startsWith("scaling=")) {
        String argVal = arg.replaceFirst("^scaling=", "");
        scaling = Optional.of(Float.parseFloat(argVal));
      }
    }
    
    if (argsList.contains("fullscreen")) {
      isFullscreen = true;
    }
      
    if (argsList.contains("double_buffering")) {
      doubleBuffer = true;
    }
    
    if (scaling.isPresent() && !doubleBuffer) {
      System.out.println("Scaling render implies double buffering!");
      doubleBuffer = true;
    }
    
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


