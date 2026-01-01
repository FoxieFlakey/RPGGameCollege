package foxie.rpg_college.entity.controller;

import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.Orientation;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.ArrowEntity;
import foxie.rpg_college.entity.CatEntity;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.entity.LivingEntity;
import foxie.rpg_college.input.Keyboard;
import foxie.rpg_college.input.Keyboard.Button;
import foxie.rpg_college.input.Mouse;

public class EntityController implements AutoCloseable {
  private Optional<Controller> controller;
  private final Camera camera;
  private final ControlEventListener listener;
  
  private float fireArrowCooldown = -1.0f;
  private float spawnCatCooldown = -1.0f;
  
  public EntityController(Controllable entity, Vec2 viewSize) {
    Controller controller = entity.getController();
    this.controller = Optional.of(controller);
    this.camera = new Camera(controller.getEntity().getWorld().getRenderBound(), viewSize);
    
    @SuppressWarnings("resource")
    EntityController self = this;
    this.listener = new ControlEventListener() {
      @Override
      public void onPositionUpdated() {
        self.camera.setPosition(controller.getEntity().getPos());
      }
      
      @Override
      public void onEntityNoLongerControllable() {
        self.controller = Optional.empty();
      }
      
      @Override
      public void onWorldChange() {
        self.camera.setBound(controller.getEntity().getWorld().getRenderBound());
      }
    };
    
    controller.addListener(this.listener);
  }
  
  public Entity getEntity() {
    return this.controller.get().getEntity();
  }
  
  @Override
  public void close() throws Exception {
    if (this.controller.isPresent()) {
      this.controller.get().removeListener(this.listener);
    }
  }
  
  public Optional<LivingEntity> getLivingEntity() {
    if (this.getEntity() instanceof LivingEntity) {
      return Optional.of((LivingEntity) this.getEntity());
    }
    
    return Optional.empty();
  }
  
  public Camera getCamera() {
    return this.camera;
  }
  
  public void handleInput(float deltaTime) {
    if (this.controller.isEmpty()) {
      return;
    }
    
    Controller controller = this.controller.get();
    
    Keyboard keyboard = this.getEntity().getWorld().getGame().keyboardState;
    Mouse mouse = this.getEntity().getWorld().getGame().mouseState;
    Optional<LivingEntity> maybeLiving = this.getLivingEntity();
    
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
      controller.setPos(new Vec2(0.0f, 0.0f));
      
      if (maybeLiving.isPresent()) {
        LivingEntity living = maybeLiving.get();
        living.setHealth(living.getMaxHealth());
        living.resetFlash();
      }
      return;
    }

    if (keyboard.getState(Button.C).isNowPressed() && this.spawnCatCooldown < 0.0f) {
      this.spawnCatCooldown = 0.1f;
      
      // Spawn cat
      CatEntity cat = new CatEntity();
      this.getEntity().getWorld().addEntity(cat);
      
      Vec2 pos = maybeLiving.map(e -> e.getLegPos()).orElse(new Vec2(0.0f, 0.0f));
      cat.setPos(pos);
    }
    
    if (mouse.getButtonState(Mouse.Button.Left).isNowPressed() && this.spawnCatCooldown < 0.0f) {
      this.spawnCatCooldown = 0.1f;
      
      // Spawn cat
      CatEntity cat = new CatEntity();
      this.getEntity().getWorld().addEntity(cat);
      cat.setPos(this.camera.translateAWTGraphicsToWorldCoord(mouse.getButtonPosition()));
    }

    if (controller.shouldControlDisabled()) {
      // Control is disabled temporarily
      return;
    }
    
    if (keyboard.getState(Button.Q).isNowPressed() && this.fireArrowCooldown < 0.0f) {
      this.fireArrowCooldown = 0.1f;
      
      // Spawn arrow
      ArrowEntity arrow = new ArrowEntity(this.getEntity());
      this.getEntity().getWorld().addEntity(arrow);
      arrow.setPos(this.getEntity().getPos());
      arrow.setRotation(this.getEntity().getRotation());
    }
    
    Vec2 moveMultiplier = new Vec2(0.0f, 0.0f);
    if (keyboard.getState(Button.P) == Keyboard.State.Clicked) {
      System.out.println("Coord: " + this.getEntity().getPos().x() + ", " + this.getEntity().getPos().y());
    }

    if (keyboard.getState(Button.W).isNowPressed()) {
      moveMultiplier = moveMultiplier.add(new Vec2(0.0f, -deltaTime));
    }
    
    if (keyboard.getState(Button.A).isNowPressed()) {
      moveMultiplier = moveMultiplier.add(new Vec2(-deltaTime, 0.0f));
    }

    if (keyboard.getState(Button.S).isNowPressed()) {
      moveMultiplier = moveMultiplier.add(new Vec2(0.0f, deltaTime));
    }

    if (keyboard.getState(Button.D).isNowPressed()) {
      moveMultiplier = moveMultiplier.add(new Vec2(deltaTime, 0.0f));
    }
    
    if (keyboard.getState(Button.Control).isNowPressed()) {
      moveMultiplier = moveMultiplier.mul(3.0f);
    }
    
    controller.applyMovement(moveMultiplier);
    
    if (moveMultiplier.x() > 0.0f) {
      controller.setRotation(Orientation.Right.toDegrees());
    } else if (moveMultiplier.x() < 0.0f) {
      controller.setRotation(Orientation.Left.toDegrees());
    }
    
    if (moveMultiplier.y() < 0.0f) {
      controller.setRotation(Orientation.Up.toDegrees());
    } else if (moveMultiplier.y() > 0.0f) {
      controller.setRotation(Orientation.Down.toDegrees());
    }

    if (mouse.getButtonState(Mouse.Button.Right).isNowPressed()) {
      Vec2 playerScreenCoord = this.camera.translateWorldToAWTGraphicsCoord(this.getEntity().getPos());
      Vec2 lookToScreenCoord = mouse.getButtonPosition().sub(playerScreenCoord);

      controller.setRotation(lookToScreenCoord.calculateAngle());
    }
  }
}
