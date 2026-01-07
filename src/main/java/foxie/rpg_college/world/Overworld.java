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

  private Overworld(Game game, Vec2 backgroundImageSize, Image backgroundImage) {
    super(game, new FloatRectangle(
      new Vec2(-backgroundImageSize.x() / 2.0f, -backgroundImageSize.y() / 2.0f),
      new Vec2(backgroundImageSize.x() / 2.0f, backgroundImageSize.y() / 2.0f)
    ));
    
    
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
    // Gap
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
    this.addTile(new IVec2(7, 1), game.TILES.PORTAL_TO_BATTLE);

    // Add test for lava tile
    this.addTile(new IVec2(5, 5), game.TILES.LAVA_TILE);
  }
  
  public static Overworld create(Game game) {
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

    return new Overworld(game, new Vec2((float) width, (float) height), backgroundImage);
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
    WorldUtil.renderBackground(this, g, this.backgroundImage);
    super.render(g, deltaTime);
  }
}
