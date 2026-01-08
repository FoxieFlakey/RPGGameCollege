package foxie.rpg_college.world;

import java.awt.Graphics2D;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.texture.Texture;

public class BattleArena extends World {
  private final Texture backgroundTexture;

  public BattleArena(Game game) {
    super(game, new FloatRectangle(
      new Vec2(
        -game.getTextureManager().getTexture("world/battle_arena/background").width() / 2.0f,
        -game.getTextureManager().getTexture("world/battle_arena/background").height() / 2.0f
      ),
      new Vec2(
        game.getTextureManager().getTexture("world/battle_arena/background").width() / 2.0f,
        game.getTextureManager().getTexture("world/battle_arena/background").height() / 2.0f
      )
    ));
    this.backgroundTexture = game.getTextureManager().getTexture("world/battle_arena/background");
    
    this.addTile(new IVec2(4,0), game.TILES.LAVA_TILE);
    this.addTile(new IVec2(4,4), game.TILES.PORTAL_TO_OVERWORLD);
  }
  
  @Override
  public void render(Graphics2D g, float deltaTime) {
    WorldUtil.renderBackground(this, g, this.backgroundTexture.image());
    super.render(g, deltaTime);
  }
}
