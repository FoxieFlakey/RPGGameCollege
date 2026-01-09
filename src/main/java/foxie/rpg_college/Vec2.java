package foxie.rpg_college;

// record adalah sebuah sugar syntax yang sama
// saja dengan kelas biasa tetapi disederhanakan
// untuk penggunakan dimana semua fieldnya final
// dan dapat diakses oleh luar. Record adalah
// kelas yang immutable, jadi setelah dibuat isinya
// tidak dapat diedit sama sekali. Jika perlu
// object baru perlu dibuat
//
// Record ini adalah vector 2 dimensi yang menentukan
// sebuah titik di koordinat, x and y. Arti dari x, y
// sangat bergantung dengan pengguna nya. Kelas ini
// hanya menyederhanan penggunakan vektor >w<
public record Vec2(
  float x,
  float y
) {
  public Vec2(float n) {
    this(n, n);
  }
  
  // Menambahakan vektor dan vektor kanan
  // seperti c = a + b dalam matematika
  public Vec2 add(Vec2 rhs) {
    return new Vec2(this.x + rhs.x, this.y + rhs.y);
  }
  
  // Mengalikan vektor dan vektor kanan
  // seperti c = a * b dalam matematika
  public Vec2 mul(Vec2 rhs) {
    return new Vec2(this.x * rhs.x, this.y * rhs.y);
  }

  // Sama seperti diatas tetapi masing-masing
  // x and y di kali dengan factor
  public Vec2 mul(float factor) {
    return new Vec2(
      this.x * factor,
      this.y * factor
    );
  }

  // Sama seperti dibawah tetapi masing-masing
  // x and y di bagi dengan factor
  public Vec2 div(float factor) {
    return new Vec2(
      this.x / factor,
      this.y / factor
    );
  }
  
  // Membagi vektor dan vektor kanan
  // seperti c = a / b dalam matematika
  public Vec2 div(Vec2 rhs) {
    return new Vec2(this.x / rhs.x, this.y / rhs.y);
  }

  // Mengurangi vektor dan vektor kanan
  // seperti c = a * b dalam matematika
  public Vec2 sub(Vec2 rhs) {
    return new Vec2(this.x - rhs.x, this.y - rhs.y);
  }

  // Method ini membulatkan vektor versi integernya
  public IVec2 round() {
    return new IVec2(
      Math.round(this.x),
      Math.round(this.y)
    );
  }

  // Magnitude adalah "panjang vektor" dalam
  // matematika umumnya diprogram ini digunakan
  // untuk menentukan jarak antara dua titik
  public float magnitude() {
    return (float) Math.sqrt(Math.pow(this.x(), 2) + Math.pow(this.y(), 2));
  }

  // Fungsi ini mengubah vektor menjadi
  // vektor yang panjangnya satu. Efek
  // yang sangat berguna adalah hasil
  // dari fungsi ini dapat digunakan
  // 1. ..untuk menghitung pergerakan
  //    karakter ke arah kursor atau
  //    koordinat tertentu. Dimana
  //    vektor ini dikalikan dengan
  //    kecepatan
  // 2. ..untuk menunjuk arah
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

  // Trigonometry yang kurang saya pahami
  // intinya fungsi ini menghitung sudut agar
  // jika diberikat vektor tentukan sudut
  // berapa derajat dari lurus atar.
  //
  // Illustrasi
  //        -Y     V
  //         |    /
  //         |T /
  //         |/
  // -X------0-------X
  //         |
  //         |
  //         Y
  //
  // Sudut yang direturn adalah di T, pada gambar diatas
  // dan V adalah letak vektor ini.
  //
  // Note:
  // seluruh kode di program ini sistem
  // koordinat agak berbeda -Y yang menunjuk
  // keatas dan +Y dibawah, ini dikarenakan
  // agar mudah mentranslate koordinat ke sistem
  // yang di pakai di Java AWT. 0, 0 actually
  // terletak di kiri atas jadi di gambar outputnya
  // secara logika dalam sistem koordinat di kuadran
  // kiri bawah
  public float calculateAngle() {
    Vec2 normalized = this.normalize();
    double angleRadians = Math.atan2(normalized.y(), normalized.x());
    double angleDegrees = Math.toDegrees(angleRadians);
    double shiftedAngle = angleDegrees + 90.0f;

    return Util.normalizeAngle((float) shiftedAngle);
  }

  // Fungsi ini mengambil sudut dan mengembalikan vektor arah
  // Fungsi ini juga adalah kebalikan dari calculateAngle
  //
  // Yang hilang hanya jarak/magnitude yang fungsi ini tidak
  // mungkin dapatkan
  //
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
