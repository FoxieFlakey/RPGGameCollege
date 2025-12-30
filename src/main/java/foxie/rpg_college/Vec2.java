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

  // In this one angle of 0 points upward
  public static Vec2 unitVectorOfAngle(float angle) {
    angle = Util.normalizeAngle(angle - 90.0f);

    return new Vec2(
      (float) Math.cos(Math.toRadians(angle)),
      (float) Math.sin(Math.toRadians(angle))
    );
  }
}
