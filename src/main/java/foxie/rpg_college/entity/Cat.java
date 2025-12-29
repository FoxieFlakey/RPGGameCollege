package foxie.rpg_college.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Vec2;

public class Cat extends LivingEntity {
  private static final Vec2 SIZE = new Vec2(20.0f, 20.0f);
  private final CollisionBox collisionBox = new CollisionBox(new Vec2(0.0f, 0.0f), Cat.SIZE);

  @Override
  public boolean canCollideWith(Entity other) {
    return true;
  }

  @Override
  public Optional<CollisionBox> getCollisionBox() {
    return Optional.of(this.collisionBox);
  }

  @Override
  public boolean isVisible(Camera cam) {
    return true;
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
    FloatRectangle box = new FloatRectangle(
      this.getPos().sub(Cat.SIZE.mul(0.5f)),
      this.getPos().add(Cat.SIZE.mul(0.5f))
    );

    FloatRectangle transformedBox = new FloatRectangle(
      this.getWorld().getGame().getCamera().translateWorldToAWTGraphicsCoord(box.getTopLeftCorner()),
      this.getWorld().getGame().getCamera().translateWorldToAWTGraphicsCoord(box.getBottomRightCorner())
    );

    int x = (int) transformedBox.getTopLeftCorner().x();
    int y = (int) transformedBox.getTopLeftCorner().y();
    int width = (int) transformedBox.getSize().x();
    int height = (int) transformedBox.getSize().y();

    g.setColor(Color.BLUE);
    g.fillRoundRect(
      x, y,
      width, height,
      5, 5
    );
  }

  @Override
  public void tick(float deltaTime) {
    
  }
  
}
