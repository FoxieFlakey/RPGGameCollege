package foxie.rpg_college.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.input.Keyboard;
import foxie.rpg_college.input.Keyboard.Button;
import foxie.rpg_college.world.World;

public class Player extends LivingEntity {
  private static final Vec2 SIZE = new Vec2(
    60.0f,
    120.0f
  );

  public final Camera camera;
  private final CollisionBox collisionBox = new CollisionBox(new Vec2(0.0f, 0.0f), Player.SIZE);

  public Player(World world, Vec2 viewSize) {
    this.camera = new Camera(world.getWorldBound(), viewSize);
  }

  public void handleInput(float deltaTime) {
    Keyboard keyboard = this.getWorld().getGame().keyboardState;
    
    Vec2 translation = new Vec2(0.0f, 0.0f);
    float moveSpeed = 100.0f; // 20 pixels per second

    if (keyboard.getState(Button.W).isNowPressed()) {
      translation = translation.add(new Vec2(0.0f, -moveSpeed * deltaTime));
    }
    
    if (keyboard.getState(Button.A).isNowPressed()) {
      translation = translation.add(new Vec2(-moveSpeed * deltaTime, 0.0f));
    }

    if (keyboard.getState(Button.S).isNowPressed()) {
      translation = translation.add(new Vec2(0.0f, moveSpeed * deltaTime));
    }

    if (keyboard.getState(Button.D).isNowPressed()) {
      translation = translation.add(new Vec2(moveSpeed * deltaTime, 0.0f));
    }

    this.setPos(this.getPos().add(translation));
    this.camera.setPosition(this.getPos());
  }

  @Override
  public void setPos(Vec2 pos) {
    super.setPos(pos);
    this.camera.setPosition(pos);
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
    FloatRectangle playerCharacterBox = new FloatRectangle(
      this.getPos().sub(Player.SIZE.mul(0.5f)),
      this.getPos().add(Player.SIZE.mul(0.5f))
    );

    FloatRectangle transformed = new FloatRectangle(
      this.camera.translateWorldToAWTGraphicsCoord(playerCharacterBox.getTopLeftCorner()),
      this.camera.translateWorldToAWTGraphicsCoord(playerCharacterBox.getBottomRightCorner())
    );

    int x = (int) transformed.getTopLeftCorner().x();
    int y = (int) transformed.getTopLeftCorner().y();
    int width = (int) transformed.getSize().x();
    int height = (int) transformed.getSize().y();

    g.setColor(Color.ORANGE);
    g.fillRoundRect(
      x, y,
      width, height,
      5, 5
    );

    g.setColor(Color.GREEN);
    Vec2 posTransformed = this.getWorld().getGame().getCamera().translateWorldToAWTGraphicsCoord(this.getPos());
    g.fillOval((int) posTransformed.x(), (int) posTransformed.y(), 10, 10);
  }

  @Override
  public void tick(float deltaTime) {
    
  }

  @Override
  public boolean isVisible(Camera cam) {
    // Player is always visible
    return true;
  }

  @Override
  public void setWorld(World world) {
    super.setWorld(world);
    this.camera.setBound(world.getWorldBound());
  }

  @Override
  public Optional<CollisionBox> getCollisionBox() {
    return Optional.of(this.collisionBox);
  }

  @Override
  public boolean canCollideWith(Entity other) {
    return true;
  }
}
