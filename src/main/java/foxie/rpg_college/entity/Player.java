package foxie.rpg_college.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Orientation;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.input.Keyboard;
import foxie.rpg_college.input.Mouse;
import foxie.rpg_college.input.Keyboard.Button;
import foxie.rpg_college.world.World;

public class Player extends LivingEntity {
  private static final Vec2 SIZE = new Vec2(
    50.0f,
    100.0f
  );

  public final Camera camera;
  private final CollisionBox collisionBox = new CollisionBox(10.0f, new Vec2(0.0f, 0.0f), Player.SIZE);
  
  private float fireArrowCooldown = -1.0f;
  private float spawnCatCooldown = -1.0f;

  public Player(World world, Vec2 viewSize) {
    this.camera = new Camera(world.getRenderBound(), viewSize);
    this.setHealth(this.getMaxHealth());
  }

  public void handleInput(float deltaTime) {
    Keyboard keyboard = this.getWorld().getGame().keyboardState;
    Mouse mouse = this.getWorld().getGame().mouseState;
    
    this.fireArrowCooldown -= deltaTime;
    if (this.fireArrowCooldown < 0.0f) {
      this.fireArrowCooldown = -1.0f;
    }
    
    this.spawnCatCooldown -= deltaTime;
    if (this.spawnCatCooldown < 0.0f) {
      this.spawnCatCooldown = -1.0f;
    }

    if (keyboard.getState(Button.R) == Keyboard.State.Clicked) {
      // Respawn player
      this.setHealth(this.getMaxHealth());
      this.setPos(new Vec2(0.0f, 0.0f));
      this.resetFlash();
      return;
    }

    if (keyboard.getState(Button.C).isNowPressed() && this.spawnCatCooldown < 0.0f) {
      this.spawnCatCooldown = 0.1f;
      
      // Spawn cat
      Cat cat = new Cat();
      this.getWorld().addEntity(cat);
      cat.setPos(this.getLegPos());
    }
    
    if (mouse.getButtonState(Mouse.Button.Left).isNowPressed() && this.spawnCatCooldown < 0.0f) {
      this.spawnCatCooldown = 0.1f;
      
      // Spawn cat
      Cat cat = new Cat();
      this.getWorld().addEntity(cat);
      cat.setPos(this.camera.translateAWTGraphicsToWorldCoord(mouse.getButtonPosition()));
    }

    if (this.isDead()) {
      // Dead cannot do anything
      return;
    }
    
    if (keyboard.getState(Button.Q).isNowPressed() && this.fireArrowCooldown < 0.0f) {
      this.fireArrowCooldown = 0.1f;
      
      // Spawn arrow
      Arrow arrow = new Arrow(this);
      this.getWorld().addEntity(arrow);
      arrow.setPos(this.getPos());
      arrow.setRotation(this.getRotation());
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
    
    if (translation.x() > 0.0f) {
      this.setRotation(Orientation.Right.toDegrees());
    } else if (translation.x() < 0.0f) {
      this.setRotation(Orientation.Left.toDegrees());
    }
    
    if (translation.y() < 0.0f) {
      this.setRotation(Orientation.Up.toDegrees());
    } else if (translation.y() > 0.0f) {
      this.setRotation(Orientation.Down.toDegrees());
    }

    if (mouse.getButtonState(Mouse.Button.Right).isNowPressed()) {
      Vec2 playerScreenCoord = this.camera.translateWorldToAWTGraphicsCoord(this.getPos());
      Vec2 lookToScreenCoord = mouse.getButtonPosition().sub(playerScreenCoord);

      this.setRotation(lookToScreenCoord.calculateAngle());
    }
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

    Color color = new Color(0.98f, 0.63f, 0.28f, 1.00f);
    if (this.getFlashState()) {
      color = new Color(1.00f, 0.93f, 0.58f, 1.00f);
    }

    if (this.isDead()) {
      color = new Color(0.68f, 0.33f, 0.00f, 1.00f);
    }

    g.setColor(color);

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
    this.camera.setBound(world.getRenderBound());
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
  public FloatRectangle getLegBox() {
    FloatRectangle collision = this.collisionBox.asRect();
    Vec2 topLeftCollision = collision.getTopLeftCorner();
    Vec2 bottomRightCollision = collision.getBottomRightCorner();
    
    return new FloatRectangle(
      new Vec2(
        topLeftCollision.x(),
        bottomRightCollision.y() - Player.SIZE.y() * 0.5f
      ),
      bottomRightCollision
    );
  }
  
  @Override
  public void die() {
    // Player cannot die
  }
}
