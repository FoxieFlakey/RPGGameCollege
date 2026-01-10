package foxie.rpg_college.entity.damage;

import foxie.rpg_college.IVec2;
import foxie.rpg_college.tile.Tile;

public class TileDamageSource extends DamageSource {
  private final Tile source;
  private final IVec2 coord;
  
  public TileDamageSource(Tile source, IVec2 coord, float damagePoint) {
    super(damagePoint);
    this.source = source;
    this.coord = coord;
  }
  
  public Tile getSource() {
    return this.source;
  }
  
  public IVec2 getCoord() {
    return this.coord;
  }
  
  @Override
  public String getName() {
    return this.source.getName(coord);
  }
}
