package foxie.rpg_college;

/**
 * Hello world!
 */
public class Main {
  public static void main(String[] args) {
    System.out.println("Hello World!");
    try (Game game = new Game()) {
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


