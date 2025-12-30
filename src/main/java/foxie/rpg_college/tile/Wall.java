package foxie.rpg_college.tile;

import java.awt.Color;
import java.awt.Graphics2D;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;

public class Wall extends Tile {
  public Wall(Game game) {
    super(game);
  }

  @Override
  public void render(Graphics2D g, float deltaTime, Vec2 position) {
    FloatRectangle wallBox = new FloatRectangle(
      position.sub(Tile.SIZE.mul(0.5f)),
      position.add(Tile.SIZE.mul(0.5f))
    );

    FloatRectangle transformed = new FloatRectangle(
      this.getGame().getCamera().translateWorldToAWTGraphicsCoord(wallBox.getTopLeftCorner()),
      this.getGame().getCamera().translateWorldToAWTGraphicsCoord(wallBox.getBottomRightCorner())
    );

    int x = (int) transformed.getTopLeftCorner().x();
    int y = (int) transformed.getTopLeftCorner().y();
    int width = (int) transformed.getSize().x();
    int height = (int) transformed.getSize().y();

    g.setColor(Color.GRAY);
    g.fillRoundRect(
      x, y,
      width, height,
      15, 15
    );
  }

  @Override
  public void tick(float deltaTime, Vec2 position) {
  }

  @Override
  public boolean isCollisionEnabled() {
    return true;
  }

  @Override
  public boolean canBeTicked() {
    return false;
  }
}
