package foxie.rpg_college.ui;

import java.awt.Graphics2D;
import java.util.Optional;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.texture.Texture;

public class InGame extends Screen {
  private final Texture topLeftCorner;
  private final Texture topRightCorner;
  private final Texture bottomRightCorner;
  private final Texture bottomLeftCorner;
  
  public InGame(Game game) {
    super(game);
    this.topLeftCorner = game.getTextureManager().getTexture("ui/in_game/corner_top_left");
    this.topRightCorner = game.getTextureManager().getTexture("ui/in_game/corner_top_right");
    this.bottomRightCorner = game.getTextureManager().getTexture("ui/in_game/corner_bottom_right");
    this.bottomLeftCorner = game.getTextureManager().getTexture("ui/in_game/corner_bottom_left");
  }

  @Override
  public boolean handleInput() {
    return true;
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
    Optional<Entity> maybeEntity = this.getGame().getPlayer();
    if (maybeEntity.isEmpty()) {
      return;
    }
    
    Entity e = maybeEntity.get();
    FloatRectangle renderBox = e.getRenderBound().orElseGet(() -> {
      return new FloatRectangle(
        e.getPos().add(new Vec2(-100.0f)),
        e.getPos().add(new Vec2(100.0f))
      );
    });
    
    Vec2 cornerSize = new Vec2(20.0f);
    Vec2 drawTopLeft = renderBox.getTopLeftCorner()
      .sub(new Vec2(5.0f))
      .sub(cornerSize.mul(0.5f));
    Vec2 drawBottomRight = renderBox.getBottomRightCorner()
      .add(new Vec2(5.0f))
      .sub(cornerSize.mul(0.5f));
    
    Vec2 drawTopRight = new Vec2(drawBottomRight.x(), drawTopLeft.y());
    Vec2 drawBottomLeft = new Vec2(drawTopLeft.x(), drawBottomRight.y());
    
    // Fix the coords
    // drawTopLeft = this.getGame()
    //   .getCamera()
    //   .translateWorldToAWTGraphicsCoord(drawTopLeft);
    // drawBottomRight = this.getGame()
    //   .getCamera()
    //   .translateWorldToAWTGraphicsCoord(drawBottomRight);
    // drawTopRight = this.getGame()
    //   .getCamera()
    //   .translateWorldToAWTGraphicsCoord(drawTopRight);
    // drawBottomLeft = this.getGame()
    //   .getCamera()
    //   .translateWorldToAWTGraphicsCoord(drawBottomLeft);
    
    // Draw top left
    g.drawImage(
      this.topLeftCorner.image(),
      (int) drawTopLeft.x(), (int) drawTopLeft.y(),
      (int) (drawTopLeft.x() + cornerSize.x()), (int) (drawTopLeft.y() + cornerSize.y()),
      0, 0,
      this.topLeftCorner.width(), this.topLeftCorner.height(),
      null
    );
    
    // Draw top right
    g.drawImage(
      this.topRightCorner.image(),
      (int) drawTopRight.x(), (int) drawTopRight.y(),
      (int) (drawTopRight.x() + cornerSize.x()), (int) (drawTopRight.y() + cornerSize.y()),
      0, 0,
      this.topRightCorner.width(), this.topRightCorner.height(),
      null
    );
    
    // Draw bottom right
    g.drawImage(
      this.bottomRightCorner.image(),
      (int) drawBottomRight.x(), (int) drawBottomRight.y(),
      (int) (drawBottomRight.x() + cornerSize.x()), (int) (drawBottomRight.y() + cornerSize.y()),
      0, 0,
      this.bottomRightCorner.width(), this.bottomRightCorner.height(),
      null
    );
    
    // Draw bottom left
    g.drawImage(
      this.bottomLeftCorner.image(),
      (int) drawBottomLeft.x(), (int) drawBottomLeft.y(),
      (int) (drawBottomLeft.x() + cornerSize.x()), (int) (drawBottomLeft.y() + cornerSize.y()),
      0, 0,
      this.bottomLeftCorner.width(), this.bottomLeftCorner.height(),
      null
    );
  }
}
