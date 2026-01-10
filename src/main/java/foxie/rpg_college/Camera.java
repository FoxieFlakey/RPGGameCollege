package foxie.rpg_college;

import java.awt.geom.AffineTransform;

// Camera essentially describes the area in the world which
// is visible to player and also handles clamping at the end of
// the entire world
//
// It also can transform coordinates in world into coordinates
// which actually can be passed to Java AWT's Graphics class for
// rendering things
//
// The center of camera would be middle of output screen
// ---------------------------------------------------------------
// Camera intinya menyimpan berbagai informasi penting yang 
// berupa area dimana dalam "dunia" yang nampak ke pengguna dan
// juga bertugas untuk mentranslate koordinat yang terletak space
// berbeda-beda ke space lain. Contohnya diberikan koordinat di
// dalam dunia lalu camera mengubahny menjadi koordinat di layar
// atau dari layar ke dunia dan operasi lain-lainnya.
//
// Posisi kamera terletak di tengah pada output. Camera juga dapat
// melakukan fungsi scrolling ke kiri/kanan seperti di banyak game
// 2D dan membatasi area yang nampak. Seperti dipojok dunia camera
// tidak akan bergerak lebih walaupun player dapat terus bergerak
public class Camera {
  // Variabel 'possiblePosition' adalah sebuah kotak yang menrepresentasi
  // posisi-posisi yang kamera dapat diletak
  private FloatRectangle possiblePosition;
  private Vec2 viewSize;
  private Vec2 outputSize;
  private Vec2 pos = new Vec2(0.0f, 0.0f);

  public Camera(FloatRectangle worldBound, Vec2 viewSize, Vec2 outputSize) {
    if (!worldBound.canFit(viewSize)) {
      // The world cannot fit the view. There would empty space
      // in final render which game don't know whta to do
      // ------------------------------------------------------
      // Pemanggil mencoba membuat kamera yang dapat melihat area
      // lebih besar dari dunianya sendiri, hal ini tidak diizinkan
      // karena ada area kosong pada layar output yang tidak disentuh
      // menyebabkan half yang tidak diinginkan
      throw new IllegalArgumentException("Attempt to create camera with it viewing area larger than the world");
    }
    
    this.viewSize = viewSize;
    this.outputSize = outputSize;
    this.setBound(worldBound);
  }

  // Method ini mereeturn koordinat dalam dunia yang
  // dimana bagian dunia yang terlihat pada output
  // didalam program, ini digunakan untuk mengoptimalkan
  // render background dari dunia sehingga hanya sebagian
  // yang tampil.
  public FloatRectangle getVisibleWorld() {
    return new FloatRectangle(
      this.pos.add(this.viewSize.mul(0.5f)),
      this.pos.sub(this.viewSize.mul(0.5f))
    );
  }

  // Given world coordinate translate it to
  // coordinate suitable for AWT graphics class
  // to use on game view canvas
  // ------------------------------------------
  // Fungsi ini mengubah koordinat di dunia ke
  // koordinat untuk dipakai di fungsi-fungsi dalam
  // AWT canvas. Sering digunakan untuk mentranslate
  // posisi titik dalam space dunia ke graphics
  // sehingga seolah-olah tampil dalam outputnya
  public Vec2 translateWorldToAWTGraphicsCoord(Vec2 coord) {
    return coord.sub(this.getVisibleWorld().getTopLeftCorner()).mul(this.getScale());
  }
  
  // Ini versi sama dengan yang parameternya hanya satu
  // tetapi ini fungsi tambahan kecil untuk menyederhanan
  // beberapa kode
  public FloatRectangle translateWorldToAWTGraphicsCoord(Vec2 pos, Vec2 size) {
    return new FloatRectangle(
      this.translateWorldToAWTGraphicsCoord(pos.sub(size.mul(0.5f))),
      this.translateWorldToAWTGraphicsCoord(pos.add(size.mul(0.5f)))
    );
  }
  
  // Fungsi ini adalah kebalikan dari fungsi translateWorldToAWTGraphicsCoord
  public Vec2 translateAWTGraphicsToWorldCoord(Vec2 coord) {
    return coord.div(this.getScale()).add(this.getVisibleWorld().getTopLeftCorner());
  }
  
  public Vec2 translateScreenToAWTGraphicsCoord(Vec2 coord) {
    return coord.mul(this.getScale());
  }
  
  // Fungsi ini membuat matriks AffineTransform yang digunakan
  // untuk mentranformasi gambar/titik/etc yang lebih complex
  // dibanding hanya skala dan translasi. Method ini digunakan
  // di berbagai projectile seperti arrow/fireball/beberapa entity
  // lain. Sehingga mereka dapat merotasi. Untuk kebanyakan
  // entity ini kurang dipakai.
  //
  // NOTE dan satu kesalahan yang saya temui
  // NOTE: Urutan transformasi dalam AffineTransform terbalik
  // walaupun di kode tampil scale lalu translate. Hasil yang
  // dilakukannya terbalik. Di translate dulu lalu di scale
  public AffineTransform getWorldToAWTGraphicsAffineTransform() {
    AffineTransform transform = new AffineTransform();
    transform.scale(this.getScale().x(), this.getScale().y());
    
    Vec2 visible = this.getVisibleWorld().getTopLeftCorner();
    transform.translate(-visible.x(), -visible.y());
    return transform;
  }
  
  // Fungsi ini mengubah koordinat di layar output ke
  // koordinat di dunia. Salah satu kegunaannya adalah
  // menentukan titik dalam dunia dimana user mengklik
  // di layar output
  public Vec2 translateScreenToWorldCoord(Vec2 coord) {
    return this.translateAWTGraphicsToWorldCoord(this.translateScreenToAWTGraphicsCoord(coord));
  }

  public void setPosition(Vec2 newPos) {
    this.pos = this.possiblePosition.clampCoordinate(newPos);
  }

  public Vec2 getPosition() {
    return this.pos;
  }

  // Fungsi ini mengset batas ukuran dunia baru. Digunakan
  // saat mengganti dunia yang tentunya ukurannya berbeda
  public void setBound(FloatRectangle worldBound) {
    Vec2 topLeft = worldBound.getTopLeftCorner();
    Vec2 bottomRight = worldBound.getBottomRightCorner();
    this.possiblePosition = new FloatRectangle(
      topLeft.add(this.viewSize.mul(0.5f)),
      bottomRight.sub(this.viewSize.mul(0.5f))
    );

    // Fix the position
    this.setPosition(this.pos);
  }
  
  // Menghitung skala agar dapat diketahui dikali
  // berapa sebuah koordinat. Fungsi ini tidak dapat
  // 100% mengkonversi titik dunia ke output. Ini
  // berguna jika pengguna mendapatkan koordinat
  // yang telah didalam 'viewSize' dan ingin mengubah
  // ke koordinat dalam output
  public Vec2 getScale() {
    return this.outputSize.div(this.viewSize);
  }
  
  public void setOutputSize(Vec2 size) {
    this.outputSize = size;
  }
}
