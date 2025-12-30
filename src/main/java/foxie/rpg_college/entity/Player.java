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
    50.0f,
    100.0f
  );

  public final Camera camera;
  private final CollisionBox collisionBox = new CollisionBox(new Vec2(0.0f, 0.0f), Player.SIZE);

  public Player(World world, Vec2 viewSize) {
    this.camera = new Camera(world.getWorldBound(), viewSize);
    this.setHealth(this.getMaxHealth());
  }

  public void handleInput(float deltaTime) {
    Keyboard keyboard = this.getWorld().getGame().keyboardState;
    if (keyboard.getState(Button.R) == Keyboard.State.Clicked) {
      // Respawn player
      this.setHealth(this.getMaxHealth());
      this.setPos(new Vec2(0.0f, 0.0f));
      return;
    }

    if (this.isDead()) {
      // Dead cannot do anything
      return;
    }
    
    Vec2 translation = new Vec2(0.0f, 0.0f);
    float moveSpeed = 100.0f; // 20 pixels per second
    if (keyboard.getState(Button.Control).isNowPressed()) {
      moveSpeed *= 3.0f;
    }

    if (keyboard.getState(Button.P) == Keyboard.State.Clicked) {
      System.out.println("Coord: " + this.getPos().x() + ", " + this.getPos().y());
    }

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
  }

  @Override
  public void setPos(Vec2 pos) {
    super.setPos(pos);
    this.camera.setPosition(pos);
  }

  @Override
  public float getMaxHealth() {
    return 100.0f;
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
    FloatRectangle renderBox = EntityHelper.calculateRenderBox(this, Player.SIZE);

    int x = (int) renderBox.getTopLeftCorner().x();
    int y = (int) renderBox.getTopLeftCorner().y();
    int width = (int) renderBox.getSize().x();
    int height = (int) renderBox.getSize().y();

    g.setColor(Color.ORANGE);
    if (this.isDead()) {
      g.setColor(Color.BLACK);
    }

    g.fillRoundRect(
      x, y,
      width, height,
      5, 5
    );
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

  @Override
  public Vec2 getLegPos() {
    return new Vec2(
      this.getPos().x(),
      this.getPos().y() + Player.SIZE.y() * 0.5f
    );
  }
}
