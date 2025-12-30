package foxie.rpg_college.world;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import javax.imageio.ImageIO;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.Util;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.Cat;
import foxie.rpg_college.tile.Wall;

public class Overworld extends World {
  private static final URL backroundImageUrl = Optional.ofNullable(Util.getResource("/world.png")).get();
  
  private final Image backgroundImage;
  private final Vec2 backgroundImageSize;

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
    this.backgroundImageSize = backgroundImageSize;
    this.backgroundImage = backgroundImage;

    Wall wall = new Wall(game);
    this.addTile(new Vec2(0.0f, 0.0f), wall);

    wall = new Wall(game);
    this.addTile(new Vec2(20.0f, -500.0f), wall);

    Cat cat = new Cat();
    this.addEntity(cat);
    cat.setPos(new Vec2(0.0f, 0.0f));
  }

  private void drawBackground(Graphics2D g) {
    FloatRectangle visible = this.getGame().getCamera().getVisibleWorld();
    Vec2 coord = visible.getTopLeftCorner().add(this.backgroundImageSize.mul(0.5f));
    Vec2 visibleSize = visible.getSize();

    // Image might be not ready, but lets ignore
    g.drawImage(
      this.backgroundImage,
      // Dest coords
      0, 0,
      (int) visibleSize.x(), (int) visibleSize.y(),
      // Source coords
      (int) coord.x(), (int) coord.y(),
      (int) visibleSize.x() + (int) coord.x(), (int) visibleSize.y() + (int) coord.y(),
      null
    );
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
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

    this.drawBackground(g);
  
    // Now the world itself
    this.renderEntities(g, deltaTime);
  }

  @Override
  public void tick(float deltaTime) {
    this.tickEntities(deltaTime);
  }
}
