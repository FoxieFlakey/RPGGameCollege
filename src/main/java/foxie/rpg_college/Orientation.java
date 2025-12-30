package foxie.rpg_college;

public enum Orientation {
  Up,
  Down,
  Left,
  Right;

  // 315..45 is up
  // 45..135 is right
  // 135..225 is down
  // 225..315 is left
  //
  // Degree goes clockwise
  public static Orientation fromDegrees(float degree) {
    degree = Util.normalizeAngle(degree);

    if (degree >= 315.0f && degree < 45.0f) {
      return Orientation.Up;
    } else if (degree >= 45.0f && degree < 135.0f){
      return Orientation.Right;
    } else if (degree >= 135.0f && degree < 225.0f) {
      return Orientation.Down;
    } else if (degree >= 225.0f && degree < 315.0f) {
      return Orientation.Left;
    }

    throw new RuntimeException("unreachable");
  }
}
