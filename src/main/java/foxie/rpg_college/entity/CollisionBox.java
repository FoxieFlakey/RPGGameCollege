package foxie.rpg_college.entity;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Vec2;

public class CollisionBox {
  private Vec2 pos;
  private Vec2 size;
  private float weight = 0.0f;
  private boolean isUnmoveable = false;

  public CollisionBox(float weight, Vec2 pos, Vec2 size) {
    this(weight, pos, size, false);
  }

  public CollisionBox(float weight, Vec2 pos, Vec2 size, boolean isUnmoveable) {
    if (Math.signum(weight) < 1.0f && !isUnmoveable) {
      throw new IllegalArgumentException("Attempt to give weight of 0 to moveable box");
    } else if (Math.signum(weight) != 0.0f && isUnmoveable) {
      throw new IllegalArgumentException("Weight of 0 must be given for unmoveable box");
    }

    this.size = size;
    this.weight = weight;
    this.setPos(pos);
    this.isUnmoveable = isUnmoveable;
  }

  public CollisionBox(Vec2 pos, Vec2 size, boolean isUnmoveable) {
    this(0.0f, pos, size, isUnmoveable);
  }

  public void setPos(Vec2 pos) {
    // The 'pos' in this class is actually top left of the box not center
    Vec2 corrected = new Vec2(
      pos.x() - this.size.x() * 0.5f,
      pos.y() - this.size.y() * 0.5f
    );
    this.pos = corrected;
  }

  public Vec2 getPos() {
    // The 'pos' in this class is actually top left of the box not center
    Vec2 corrected = new Vec2(
      this.pos.x() + this.size.x() * 0.5f,
      this.pos.y() + this.size.y() * 0.5f
    );
    return corrected;
  }

  public float getWeight() {
    if (this.isUnmoveable) {
      return Float.POSITIVE_INFINITY;
    }

    return this.weight;
  }

  // Returns true if collision is fixed a.k.a there was collision
  // this also fixes other box
  public boolean checkCollisionAndFix(CollisionBox other) {
    if (!this.isCollided(other)) {
      return false;
    }

    float thisRatio;
    float otherRatio;

    if (this.isUnmoveable && !other.isUnmoveable) {
      thisRatio = 0.0f;
      otherRatio = 1.0f;
    } else if (!this.isUnmoveable && other.isUnmoveable) {
      thisRatio = 1.0f;
      otherRatio = 0.0f;
    } else if (this.isUnmoveable && other.isUnmoveable) {
      // Unmoveable object overlaps with unmoveable objects
      // yea.. cant fix either of them, lets do nothing
      return false;
    } else {
      float totalWeight = this.getWeight() + other.getWeight();
      thisRatio = other.getWeight() / totalWeight;
      otherRatio = this.getWeight() / totalWeight;
    }

    float overlapX;
    float overlapY;

    if (this.pos.x() < other.pos.x()) {
      overlapX = this.pos.x() + this.size.x() - other.pos.x();
    } else {
      overlapX = other.pos.x() + other.size.x() - this.pos.x();
    }

    if (this.pos.y() < other.pos.y()) {
      overlapY = this.pos.y() + this.size.y() - other.pos.y();
    } else {
      overlapY = other.pos.y() + other.size.y() - this.pos.y();
    }

    if (Math.abs(overlapX) < Math.abs(overlapY)) {
      if (this.pos.x() < other.pos.x()) {
        this.pos = this.pos.sub(new Vec2(overlapX, 0.0f).mul(thisRatio));
        other.pos = other.pos.add(new Vec2(overlapX, 0.0f).mul(otherRatio));
      } else {
        this.pos = this.pos.add(new Vec2(overlapX, 0.0f).mul(thisRatio));
        other.pos = other.pos.sub(new Vec2(overlapX, 0.0f).mul(otherRatio));
      }
    } else {
      if (this.pos.y() < other.pos.y()) {
        this.pos = this.pos.sub(new Vec2(0.0f, overlapY).mul(thisRatio));
        other.pos = other.pos.add(new Vec2(0.0f, overlapY).mul(otherRatio));
      } else {
        this.pos = this.pos.add(new Vec2(0.0f, overlapY).mul(thisRatio));
        other.pos = other.pos.sub(new Vec2(0.0f, overlapY).mul(otherRatio));
      }
    }

    return true;
  }

  public boolean isCollided(CollisionBox other) {
    FloatRectangle thisRect = new FloatRectangle(
      this.pos,
      this.pos.add(this.size)
    );

    FloatRectangle otherRect = new FloatRectangle(
      other.pos,
      other.pos.add(other.size)
    );

    return thisRect.isIntersects(otherRect);
  }

  public boolean isUnmoveable() {
    return this.isUnmoveable;
  }
  
  public FloatRectangle asRect() {
    return new FloatRectangle(
      this.pos,
      this.pos.add(this.size)
    );
  }
}
