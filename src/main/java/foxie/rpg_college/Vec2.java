package foxie.rpg_college;

public record Vec2(
  float x,
  float y
) {
  public Vec2(float n) {
    this(n, n);
  }
  
  public Vec2 add(Vec2 rhs) {
    return new Vec2(this.x + rhs.x, this.y + rhs.y);
  }
  
  public Vec2 mul(Vec2 rhs) {
    return new Vec2(this.x * rhs.x, this.y * rhs.y);
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
  
  public Vec2 div(Vec2 rhs) {
    return new Vec2(this.x / rhs.x, this.y / rhs.y);
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

  public float magnitude() {
    return (float) Math.sqrt(Math.pow(this.x(), 2) + Math.pow(this.y(), 2));
  }

  public Vec2 normalize() {
    float magnitude = this.magnitude();
    if (Util.isZero(magnitude)) {
      // Zero vector, there no magnitude
      // it points to nowhere, give 0
      return this;
    }
    
    return new Vec2(
      this.x() / magnitude,
      this.y() / magnitude
    );
  }

  public float calculateAngle() {
    Vec2 normalized = this.normalize();
    double angleRadians = Math.atan2(normalized.y(), normalized.x());
    double angleDegrees = Math.toDegrees(angleRadians);
    double shiftedAngle = angleDegrees + 90.0f;

    return Util.normalizeAngle((float) shiftedAngle);
  }

  // In this one angle of 0 points upward
  // upward is negative
  //
  // Following AWT's coordinates where zero, zero is top left
  // and bottom right is positve X, positive Y
  public static Vec2 unitVectorOfAngle(float angle) {
    angle = Util.normalizeAngle(angle - 90.0f);

    return new Vec2(
      (float) Math.cos(Math.toRadians(angle)),
      (float) Math.sin(Math.toRadians(angle))
    );
  }
}
