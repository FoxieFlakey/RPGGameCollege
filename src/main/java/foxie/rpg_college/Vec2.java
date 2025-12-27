package foxie.rpg_college;

public record Vec2(
  float x,
  float y
) {
  public Vec2 add(Vec2 rhs) {
    return new Vec2(this.x + rhs.x, this.y + rhs.y);
  }

  public Vec2 subtract(Vec2 rhs) {
    return new Vec2(this.x - rhs.x, this.y - rhs.y);
  }
}
