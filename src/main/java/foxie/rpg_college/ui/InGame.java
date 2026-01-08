package foxie.rpg_college.ui;

import java.awt.Graphics2D;

import foxie.rpg_college.Game;

public class InGame extends Screen {
  public InGame(Game game) {
    super(game);
  }

  @Override
  public boolean handleInput() {
    return true;
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
  }
}
