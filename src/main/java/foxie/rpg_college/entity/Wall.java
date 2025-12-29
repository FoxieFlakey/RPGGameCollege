package foxie.rpg_college.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Vec2;

public class Wall extends Entity {
  private static final Vec2 SIZE = new Vec2(180.0f, 100.0f);
  
  private final CollisionBox collisionBox = new CollisionBox(new Vec2(0.0f, 0.0f), SIZE);

  @Override
  public Optional<CollisionBox> getCollisionBox() {
    return Optional.of(collisionBox);
  }

  @Override
  public boolean isVisible(Camera cam) {
    return true;
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
    FloatRectangle wallBox = new FloatRectangle(
      this.getPos().sub(Wall.SIZE.mul(0.5f)),
      this.getPos().add(Wall.SIZE.mul(0.5f))
    );

    FloatRectangle transformed = new FloatRectangle(
      this.getWorld().getGame().getCamera().translateWorldToAWTGraphicsCoord(wallBox.getTopLeftCorner()),
      this.getWorld().getGame().getCamera().translateWorldToAWTGraphicsCoord(wallBox.getBottomRightCorner())
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

    g.setColor(Color.GREEN);
    Vec2 posTransformed = this.getWorld().getGame().getCamera().translateWorldToAWTGraphicsCoord(this.getPos());
    g.fillOval((int) posTransformed.x(), (int) posTransformed.y(), 10, 10);
  }

  @Override
  public boolean canCollideWith(Entity other) {
    return true;
  }

  @Override
  public void tick(float deltaTime) {
  }

  @Override
  public void onCollision() {
    // Wall does not move
    this.collisionBox.setPos(this.getPos());
  }
}
