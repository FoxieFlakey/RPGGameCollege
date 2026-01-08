package foxie.rpg_college.world;

import java.awt.Graphics2D;
import java.util.ArrayList;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.TurretEntity;
import foxie.rpg_college.texture.Texture;

public class BattleArena extends World {
  private static final float RESPAWN_DELAY = 6.0f;
  
  private final Texture backgroundTexture;
  private final ArrayList<TurretEntity> turrets = new ArrayList<>();
  
  private boolean allTurretDestroyed = false;
  private float turretRespawnDelay = -1.0f;
  
  private int turretCount = 1;

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
    
    this.addTile(new IVec2(1,0), game.TILES.WALL_TILE);
    this.addTile(new IVec2(1,1), game.TILES.WALL_TILE);
    this.addTile(new IVec2(1,2), game.TILES.WALL_TILE);
    this.addTile(new IVec2(1,3), game.TILES.WALL_TILE);
    
    this.spawnTurrets();
  }
  
  @Override
  public Vec2 getWorldSpawnPoint() {
    return new Vec2(-100.0f, 300.0f);
  }
  
  private void spawnTurrets() {
    float currentY = -this.backgroundTexture.height() / 2.0f + 100.0f;
    float maxY = this.backgroundTexture.height() / 2.0f;
    float intervalY = (maxY - currentY) / this.turretCount;
    float x = this.backgroundTexture.width() / 2.0f - 100.0f;
    
    while (currentY < maxY) {
      TurretEntity turret = new TurretEntity(this.getGame());
      this.addEntity(turret);
      turret.setPos(new Vec2(x, currentY));
      currentY += intervalY;
      
      this.turrets.add(turret);
    }
  }
  
  @Override
  public void tick(float deltaTime) {
    super.tick(deltaTime);
    
    if (this.allTurretDestroyed) {
      this.turretRespawnDelay -= deltaTime;
      if (this.turretRespawnDelay < 0.0f) {
        this.allTurretDestroyed = false;
        this.spawnTurrets();
      }
    } else {
      // Check if all turrets destroyed
      int liveCount = 0;
      for (TurretEntity e : this.turrets) {
        if (!e.isDead()) {
          liveCount += 1;
        }
      }
      
      if (liveCount == 0) {
        this.allTurretDestroyed = true;
        this.turretRespawnDelay = BattleArena.RESPAWN_DELAY;
        this.turretCount += 1;
        this.turrets.clear();
      }
    }
  }
  
  @Override
  public void render(Graphics2D g, float deltaTime) {
    WorldUtil.renderBackground(this, g, this.backgroundTexture.image());
    super.render(g, deltaTime);
  }
}
