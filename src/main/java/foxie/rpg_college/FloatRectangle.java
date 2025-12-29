package foxie.rpg_college;

public record FloatRectangle(
  Vec2 pos1,
  Vec2 pos2
) {
  public Vec2 getBottomRightCorner() {
    return new Vec2(
      Float.max(this.pos1.x(), this.pos2.x()),
      Float.max(this.pos1.y(), this.pos2.y())
    );
  }

  public Vec2 getTopLeftCorner() {
    return new Vec2(
      Float.min(this.pos1.x(), this.pos2.x()),
      Float.min(this.pos1.y(), this.pos2.y())
    );
  }

  public boolean isIntersects(FloatRectangle other) {
    Vec2 thisTopLeft = this.getTopLeftCorner();
    Vec2 thisBottomRight = this.getBottomRightCorner();
    Vec2 otherTopLeft = other.getTopLeftCorner();
    Vec2 otherBottomRight = other.getBottomRightCorner();

    boolean noOverlapX = thisBottomRight.x() <= otherTopLeft.x() || otherBottomRight.x() <= thisTopLeft.x();
    boolean noOverlapY = thisBottomRight.y() <= otherTopLeft.y() || otherBottomRight.y() <= thisTopLeft.y();

    return !(noOverlapX || noOverlapY);
  }

  // Check if this rectangle can fit another rectangle
  // sized width and height
  public boolean canFit(Vec2 size) {
    Vec2 topLeft = this.getTopLeftCorner();
    Vec2 bottomRight = this.getBottomRightCorner();

    float thisWidth = bottomRight.x() - topLeft.x();
    float thisHeight = bottomRight.y() - topLeft.y();

    if (size.x() <= thisWidth && size.y() <= thisHeight) {
      return true;
    }

    return false;
  }

  public Vec2 getSize() {
    return this.getBottomRightCorner().sub(this.getTopLeftCorner());
  }

  public Vec2 clampCoordinate(Vec2 coord) {
    Vec2 topLeft = this.getTopLeftCorner();
    Vec2 bottomRight = this.getBottomRightCorner();

    return new Vec2(
      Math.clamp(coord.x(), topLeft.x(), bottomRight.x()),
      Math.clamp(coord.y(), topLeft.y(), bottomRight.y())
    );
  }

  public boolean contains(Vec2 pos) {
    Vec2 topLeft = this.getTopLeftCorner();
    Vec2 bottomRight = this.getBottomRightCorner();

    return
      topLeft.x() <= pos.x() && pos.x() <= bottomRight.x() &&
      topLeft.y() <= pos.y() && pos.y() <= bottomRight.y();
  }
}

