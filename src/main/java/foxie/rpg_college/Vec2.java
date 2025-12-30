package foxie.rpg_college;

public record Vec2(
  float x,
  float y
) {
  public Vec2 add(Vec2 rhs) {
    return new Vec2(this.x + rhs.x, this.y + rhs.y);
  }

  public Vec2 mul(float factor) {
    return new Vec2(
      this.x * factor,
      this.y * factor
    );
  }

  public Vec2 div(float factor) {
    return new Vec2(
      this.x / factor,
      this.y / factor
    );
  }

  public Vec2 sub(Vec2 rhs) {
    return new Vec2(this.x - rhs.x, this.y - rhs.y);
  }

  public IVec2 round() {
    return new IVec2(
      Math.round(this.x),
      Math.round(this.y)
    );
  }
}
