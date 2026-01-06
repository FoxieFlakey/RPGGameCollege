package foxie.rpg_college.tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Optional;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.world.World;

public class PortalTile extends Tile {
  private static final Object EXTRA_DATA_KEY = new Object();
  private static final float COOLDOWN_TIME = 2.0f;
  
  private final String targetWorldId;
  
  private static class PortalData {
    public final HashMap<World, Vec2> savedPositions = new HashMap<>();
    public float lastStepTime = 0.0f;
  };
  
  public PortalTile(Game game, String targetWorld) {
    super(game);
    this.targetWorldId = targetWorld;
  }
  
  @Override
  public void steppedBy(Entity e, IVec2 coord) {
    PortalData data = (PortalData) e.getExtraDataOrInsert(PortalTile.EXTRA_DATA_KEY, PortalData::new);
    if (e.getWorld().getGame().getGameTime() - data.lastStepTime < PortalTile.COOLDOWN_TIME) {
      // In cooldown
      return;
    }
    
    World targetWorld = this.getGame().getWorldManager().getWorld(this.targetWorldId).get();
    Vec2 targetTeleportCoord = Optional.ofNullable(data.savedPositions.get(targetWorld)).orElseGet(() -> new Vec2(0.0f, 0.0f));
    data.savedPositions.put(e.getWorld(), e.getPos());
    data.lastStepTime = e.getWorld().getGame().getGameTime();
    
    targetWorld.addEntity(e);
    e.setPos(targetTeleportCoord);
  }

  @Override
  public boolean isCollisionEnabled() {
    return false;
  }

  @Override
  public boolean canBeTicked() {
    return false;
  }

  @Override
  public void render(Graphics2D g, float deltaTime, IVec2 position) {
    FloatRectangle renderBox = TileHelper.calculateRenderBox(this, position);

    int x = (int) renderBox.getTopLeftCorner().x();
    int y = (int) renderBox.getTopLeftCorner().y();
    int width = (int) renderBox.getSize().x();
    int height = (int) renderBox.getSize().y();

    g.setColor(Color.GREEN);
    g.fillRect(x, y, width, height);
  }

  @Override
  public void tick(float deltaTime, IVec2 position) {
  }
}
