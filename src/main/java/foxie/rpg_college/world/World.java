package foxie.rpg_college.world;

import java.awt.Color;
import java.awt.Graphics;
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

public class World {
  private final Game game;
  private final FloatRectangle bound;
  private static final URL backroundImageUrl = Optional.ofNullable(Util.getResource("/world.png")).get();
  
  private final Image backgroundImage;
  private final Vec2 backgroundImageSize;

  public World(Game game) {
    this.game = game;

    try {
      backgroundImage = ImageIO.read(World.backroundImageUrl);
    } catch (IOException e) {
      throw new RuntimeException("Cannot load world's background image", e);
    }

    float width = backgroundImage.getWidth(null);
    float height = backgroundImage.getHeight(null);

    // Height and width must be known now, the world cannot be constructed until
    // that time
    assert width > 0;
    assert height > 0;

    this.backgroundImageSize = new Vec2((float) width, (float) height);

    this.bound = new FloatRectangle(
      new Vec2(-backgroundImageSize.x() / 2.0f, -backgroundImageSize.y() / 2.0f),
      new Vec2(backgroundImageSize.x() / 2.0f, backgroundImageSize.y() / 2.0f)
    );
  }

  public FloatRectangle getWorldBound() {
    return this.bound;
  }

  private void drawBackground(Graphics2D g) {
    FloatRectangle visible = this.game.getCamera().getVisibleWorld();
    Vec2 coord = visible.getTopLeftCorner();
    Vec2 visibleSize = visible.getSize();

    Vec2 imageCoord = coord.add(this.backgroundImageSize.mul(0.5f));

    // Image might be not ready, but lets ignore
    g.drawImage(
      this.backgroundImage,
      // Dest coords
      0, 0,
      (int) visibleSize.x(), (int) visibleSize.y(),
      // Source coords
      (int) imageCoord.x(), (int) imageCoord.y(),
      (int) visibleSize.x() + (int) imageCoord.x(), (int) visibleSize.y() + (int) imageCoord.y(),
      null
    );
  }

  public void render(Graphics graphics) {
    if (!(graphics instanceof Graphics2D)) {
      throw new RuntimeException("Cannot render to non Graphics2D target");
    }
    Graphics2D g = (Graphics2D) graphics;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

    this.drawBackground(g);

    FloatRectangle box = new FloatRectangle(
      new Vec2(200.0f, 150.0f),
      new Vec2(20.0f, 50.0f)
    );

    FloatRectangle boxInGraphics = new FloatRectangle(
      this.game.getCamera().translateWorldToAWTGraphicsCoord(box.getTopLeftCorner()),
      this.game.getCamera().translateWorldToAWTGraphicsCoord(box.getBottomRightCorner())
    );

    Vec2 boxPos = boxInGraphics.getTopLeftCorner();
    Vec2 boxSize = boxInGraphics.getSize();

    int x = (int) boxPos.x();
    int y = (int) boxPos.y();
    int width = (int) boxSize.x();
    int height = (int) boxSize.y();

    g.setColor(Color.PINK);
    g.fillRect(x, y, width, height);

    // Now the world itself
  }
}
