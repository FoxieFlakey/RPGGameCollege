package foxie.rpg_college.entity;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Vec2;

public class CollisionBox {
  private Vec2 pos;
  private Vec2 size;

  public CollisionBox(Vec2 pos, Vec2 size) {
    this.size = size;
    this.setPos(pos);
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

  // Returns true if collision is fixed a.k.a there was collision
  // this only fixes current box's position. Not touching other
  public boolean checkCollisionAndFix(CollisionBox other) {
    if (!this.isCollided(other)) {
      return false;
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
        this.pos = this.pos.sub(new Vec2(overlapX, 0.0f));
      } else {
        this.pos = this.pos.add(new Vec2(overlapX, 0.0f));
      }
    } else {
      if (this.pos.y() < other.pos.y()) {
        this.pos = this.pos.sub(new Vec2(0.0f, overlapY));
      } else {
        this.pos = this.pos.add(new Vec2(0.0f, overlapY));
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
}
