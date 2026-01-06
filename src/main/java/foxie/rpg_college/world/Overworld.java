package foxie.rpg_college.world;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import javax.imageio.ImageIO;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Util;
import foxie.rpg_college.Vec2;

public class Overworld extends World {
  private static final URL backroundImageUrl = Optional.ofNullable(Util.getResource("/world.png")).get();
  
  private final Image backgroundImage;

  public Overworld(Game game) {
    Image backgroundImage;
    try {
      backgroundImage = ImageIO.read(Overworld.backroundImageUrl);
    } catch (IOException e) {
      throw new RuntimeException("Cannot load world's background image", e);
    }

    float width = backgroundImage.getWidth(null);
    float height = backgroundImage.getHeight(null);

    // Height and width must be known now, the world cannot be constructed until
    // that time
    assert width > 0;
    assert height > 0;

    Vec2 backgroundImageSize = new Vec2((float) width, (float) height);

    FloatRectangle bound = new FloatRectangle(
      new Vec2(-backgroundImageSize.x() / 2.0f, -backgroundImageSize.y() / 2.0f),
      new Vec2(backgroundImageSize.x() / 2.0f, backgroundImageSize.y() / 2.0f)
    );

    super(game, bound);
    this.backgroundImage = backgroundImage;

    this.addTile(new IVec2(6, 0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(5, 0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(4, 0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(3, 0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(2, 0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(1, 0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(0, 0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(0, 1), game.TILES.WALL_TILE);
    // Gap
    // Gap
    this.addTile(new IVec2(0, 4), game.TILES.WALL_TILE);
    // Gap
    this.addTile(new IVec2(0, 6), game.TILES.WALL_TILE);
    this.addTile(new IVec2(0, 7), game.TILES.WALL_TILE);
    this.addTile(new IVec2(0, 8), game.TILES.WALL_TILE);
    this.addTile(new IVec2(1, 8), game.TILES.WALL_TILE);
    this.addTile(new IVec2(2, 8), game.TILES.WALL_TILE);
    this.addTile(new IVec2(3, 8), game.TILES.WALL_TILE);
    this.addTile(new IVec2(4, 8), game.TILES.WALL_TILE);
    this.addTile(new IVec2(5, 8), game.TILES.WALL_TILE);
    this.addTile(new IVec2(6, 8), game.TILES.WALL_TILE);
    this.addTile(new IVec2(6, 7), game.TILES.WALL_TILE);
    this.addTile(new IVec2(6, 6), game.TILES.WALL_TILE);
    this.addTile(new IVec2(6, 5), game.TILES.WALL_TILE);
    this.addTile(new IVec2(6, 4), game.TILES.WALL_TILE);
    this.addTile(new IVec2(6, 3), game.TILES.WALL_TILE);
    this.addTile(new IVec2(6, 2), game.TILES.WALL_TILE);
    this.addTile(new IVec2(6, 1), game.TILES.WALL_TILE);

    // Add test for lava tile
    this.addTile(new IVec2(5, 5), game.TILES.LAVA_TILE);
  }

  private void drawBackground(Graphics2D g) {
    WorldUtil.renderBackground(this, g, this.backgroundImage);
  }

  @Override
  public Vec2 validatePos(Vec2 pos) {
    return this.getWorldBound().clampCoordinate(pos);
  }

  @Override
  public boolean isValidPos(Vec2 pos) {
    return this.getWorldBound().contains(pos);
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
    this.drawBackground(g);
  
    // Now the world itself
    this.renderEntities(g, deltaTime);
  }

  @Override
  public void tick(float deltaTime) {
    this.tickEntities(deltaTime);
  }
}
