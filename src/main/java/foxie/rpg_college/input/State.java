package foxie.rpg_college.input;

// enum yang menyatakan kondisi sebuah button
// seperti keyboard atatu mouse
public enum State {
  // Sebelumnya tombol belum diklik tetapi sekarang diklik
  Clicked,
  // Sebelumnya tombol diklik tetapi sekarang tidak diklik
  Unclicked,
  // Sebelumnya tombol diklik dan sekarang juga masih diklik
  Hold,
  // Sebelumnya tombol tidak diklik dan sekarang juga masih tidak diklik
  Unhold;

  // Apakah state nya berakhir dengan
  // tombol ditekan
  public boolean isNowPressed() {
    switch (this) {
      case Clicked:
        return true;
      case Hold:
        return true;
      default:
        return false;
    }
  }

  // Apakah state nya berakhir dengan
  // tombol tidak ditekan
  public boolean isNowReleased() {
    switch (this) {
      case Unclicked:
        return true;
      case Unhold:
        return true;
      default:
        return false;
    }
  }
}
