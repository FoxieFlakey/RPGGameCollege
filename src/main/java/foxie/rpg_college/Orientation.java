package foxie.rpg_college;

// Enum di Java dapat dipikirkan
// sebagai list pilihan-pilihan
// yang berbeda
//
// Disini ini digunakan untuk
// menunjukkan arah dimana
public enum Orientation {
  Up,
  Down,
  Left,
  Right;

  // Mengubah orientasi menjadi
  // derajat
  public float toDegrees() {
    switch (this) {
      case Up:
        return 0.0f;
      case Down:
        return 180.0f;
      case Left:
        return 270.0f;
      case Right:
        return 90.0f;
    }
    
    throw new RuntimeException("unreachable");
  }

  // Fungsi ini mengubah sudut dalam derajat menjadi
  // salah satu opsi Orientation sesuai range dibawah ini
  //
  // 315..45 is up
  // 45..135 is right
  // 135..225 is down
  // 225..315 is left
  //
  // Degree goes clockwise
  // Add error belonging to this line (in git blame)
  // Dalam notasi range, a..b berarti "a" termasuk
  // dan "b" tidak termasuk, alternatively dalam notasi
  // interval itu sama saja dengan [a,b)
  public static Orientation fromDegrees(float degree) {
    degree = Util.normalizeAngle(degree);

    if (degree >= 315.0f && degree < 360.0f) {
      return Orientation.Up;
    } else if (degree >= 45.0f && degree < 135.0f){
      return Orientation.Right;
    } else if (degree >= 135.0f && degree < 225.0f) {
      return Orientation.Down;
    } else if (degree >= 225.0f && degree < 315.0f) {
      return Orientation.Left;
    } else if (degree >= 0.0f && degree < 45.0f) {
      return Orientation.Up;
    }

    throw new RuntimeException("unreachable degree " + degree);
  }
}
