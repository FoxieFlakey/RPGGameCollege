package foxie.rpg_college;

import java.awt.Graphics;

// An object which can be rendered to screen
// and be updated

public abstract class GameObject {
  private Vec2 position;

  public Vec2 getPosition() {
    return this.position;
  }

  public abstract void render(Graphics g);
  public abstract void tick();
}
