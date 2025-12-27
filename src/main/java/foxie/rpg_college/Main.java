package foxie.rpg_college;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

/**
 * Hello world!
 */
public class Main {
  public static void main(String[] args) {
    System.out.println("Hello World!");
    try {
      SwingUtilities.invokeAndWait(() -> {
        new Game().run();
      });
    } catch (InvocationTargetException e) {
      System.err.println("Error running game: ");
      e.printStackTrace();
    } catch (InterruptedException _) {
    }
  }
}


