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

public class BattleArena extends World {
  private static final URL backroundImageUrl = Optional.ofNullable(Util.getResource("/battle_arena.png")).get();
  
  private final Image backgroundImage;

  private BattleArena(Game game, Vec2 backgroundImageSize, Image backgroundImage) {
    super(game, new FloatRectangle(
      new Vec2(-backgroundImageSize.x() / 2.0f, -backgroundImageSize.y() / 2.0f),
      new Vec2(backgroundImageSize.x() / 2.0f, backgroundImageSize.y() / 2.0f)
    ));
    this.backgroundImage = backgroundImage;
    this.addTile(new IVec2(4,0), game.TILES.LAVA_TILE);
    this.addTile(new IVec2(4,4), game.TILES.PORTAL_TO_OVERWORLD);
  }
  
  public static BattleArena create(Game game) {
    Image backgroundImage;
    try {
      backgroundImage = ImageIO.read(BattleArena.backroundImageUrl);
    } catch (IOException e) {
      throw new RuntimeException("Cannot load world's background image", e);
    }

    float width = backgroundImage.getWidth(null);
    float height = backgroundImage.getHeight(null);

    // Height and width must be known now, the world cannot be constructed until
    // that time
    assert width > 0;
    assert height > 0;

    return new BattleArena(game, new Vec2((float) width, (float) height), backgroundImage);
  }

  @Override
  public void render(Graphics2D g, float deltaTime) {
    WorldUtil.renderBackground(this, g, this.backgroundImage);
    super.render(g, deltaTime);
  }
}
