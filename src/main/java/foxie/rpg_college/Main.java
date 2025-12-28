package foxie.rpg_college;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

/**
 * Hello world!
 */
public class Main {
  public static void main(String[] args) {
    System.out.println("Hello World!");
    Game game = new Game();

    while (true) {
      game.render();

      try {
        Thread.sleep(1000 / 60);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}


