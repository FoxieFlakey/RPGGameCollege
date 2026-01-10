package foxie.rpg_college;

// Record ini menrepresentasikan
// sebuah kotak yang dari titik
// pos1 dampai pos2. Ini digunakan
// untuk berbagai hal seperti
// "render box" yang adalah sebuah
// kotak yang telah melalui semua
// translations dan konversi sehingga
// angka-angka yang didalamnya
// dapat langsung dikasih ke java.awt.Graphics
// untuk dirender kelayar
public record FloatRectangle(
  Vec2 pos1,
  Vec2 pos2
) {
  // Karena pos1 dan pos2 mungkin terbalik, fungsi ini
  // menggunakan Float.max untuk mendapatkan titik yang
  // paling "besar" kearah kanan bawah
  public Vec2 getBottomRightCorner() {
    return new Vec2(
      Float.max(this.pos1.x(), this.pos2.x()),
      Float.max(this.pos1.y(), this.pos2.y())
    );
  }

  // Karena pos1 dan pos2 mungkin terbalik, fungsi ini
  // menggunakan Float.min untuk mendapatkan titik yang
  // paling "kecil" kearah kiri bawah
  public Vec2 getTopLeftCorner() {
    return new Vec2(
      Float.min(this.pos1.x(), this.pos2.x()),
      Float.min(this.pos1.y(), this.pos2.y())
    );
  }

  // Fungsi ini memeriksa apakah kotak ini menimpa kotak
  // lain. Sering digunakan pada sistem collision box
  // di program ini.
  public boolean isIntersects(FloatRectangle other) {
    Vec2 thisTopLeft = this.getTopLeftCorner();
    Vec2 thisBottomRight = this.getBottomRightCorner();
    Vec2 otherTopLeft = other.getTopLeftCorner();
    Vec2 otherBottomRight = other.getBottomRightCorner();

    boolean noOverlapX = thisBottomRight.x() <= otherTopLeft.x() || otherBottomRight.x() <= thisTopLeft.x();
    boolean noOverlapY = thisBottomRight.y() <= otherTopLeft.y() || otherBottomRight.y() <= thisTopLeft.y();

    return !(noOverlapX || noOverlapY);
  }

  // Fungsi ini memeriksa apakah kotak ini dapat muat di
  // dalam kotak lain yang memiliki ukuran 'size'. Hanya
  // return true, jika kotak bisa muat seluruhnya dalam
  // kotak berukuran 'size'
  //
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

  // Ukuran kotak
  //
  // Dapat dihitung dengan mengurangi titik kanan bawah
  // dan kiri atas.
  public Vec2 getSize() {
    return this.getBottomRightCorner().sub(this.getTopLeftCorner());
  }

  // Pembatas, jika diberikan koordinat "coord", Fungsi ini
  // mereturn koordinat baru yang harus terletak didalam kotanya
  // jika melebihi dibatasi ke X, atau Y terdekat
  public Vec2 clampCoordinate(Vec2 coord) {
    Vec2 topLeft = this.getTopLeftCorner();
    Vec2 bottomRight = this.getBottomRightCorner();

    return new Vec2(
      Util.clamp(coord.x(), topLeft.x(), bottomRight.x()),
      Util.clamp(coord.y(), topLeft.y(), bottomRight.y())
    );
  }
  
  // Menghitung koordinat titik tengah kotak, dengan manambahkan
  // atau offset dari titik kiri atas dengan setengah ukuran
  public Vec2 getCenter() {
    return this.getTopLeftCorner().add(this.getSize().mul(0.5f));
  }

  // Apakah titik 'pos' terletak didalam kotak?
  // logikanya sederhana saja, membandingkan apakah
  // titiknya di antara titik kiri atas dan bawah
  // kanan
  public boolean contains(Vec2 pos) {
    Vec2 topLeft = this.getTopLeftCorner();
    Vec2 bottomRight = this.getBottomRightCorner();

    return
      topLeft.x() <= pos.x() && pos.x() <= bottomRight.x() &&
      topLeft.y() <= pos.y() && pos.y() <= bottomRight.y();
  }
}

