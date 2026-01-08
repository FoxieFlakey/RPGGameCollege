package foxie.rpg_college.ui;

import java.awt.Graphics2D;

import foxie.rpg_college.Game;

public abstract class Screen {
  private final Game game;

  public Screen(Game game) {
    this.game = game;
  }

  public Game getGame() {
    return this.game;
  }

  // Return true to passthrough game
  // a.k.a such as clicks happens in
  // empty zone where there no UI element
  public abstract boolean handleInput();
  public abstract void render(Graphics2D g, float deltaTime);
  public abstract boolean canTickGame();
}

