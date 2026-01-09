package foxie.rpg_college;

import java.net.URL;

// Kelas Util, kelas ini berisi beberapa fungsi/fungsi
// umum yang dapat digunakan diberbagai konteks
// seperti kotak perkakas contohnya yang berisi
// obeng, tang, dan lain-lain yang digunakan
// di berbagai bagain dari project
public class Util {
  // Mencari "resource" atau biasanya sebuah
  // data yang terletak di 'path'
  //
  // Program ini mengbundle data-data yang
  // dipakai nya kedalam satu jar yang lengkap
  // tinggal dijalankan.
  //
  // Java otomatis mencari data-data di jar
  //
  // Lebih detail: Java tidak specific cari
  // di jar program, tetapi memeriksa semua
  // jar yang ada di dalam ClassLoader yang
  // dimana method getResource dipanggil
  //
  // Kebetulan saja tidak ada jar-jar yang
  // Java load yang kebetulan sama nama dengan
  // yang ada diprogram saya :3
  //
  // URL yang direturn dapat dibuka dengan
  // method URL#openStream
  public static URL getResource(String path) {
    return Util.class.getResource(path);
  }

  // Fungsi ini mengambil waktu sekarang dalam detik
  // asal dari detiknya tidak penting. Fungsi System.nanoTime
  // tidak menentukan asalnya. Hanya dapat berguna
  // jika menghitung durasinya
  public static float getTime() {
    return ((float) System.nanoTime()) / 1_000_000_000.0f;
  }
  
  // Fungsi ini digunakan untuk membatasi 'value'
  // sehingga diantara min dan max, kalau terlalu
  // kecil dibatasi ke 'min' jika kebesaran dibatasi
  // ke 'max'
  public static float clamp(float value, float min, float max) {
    if (min < value && value < max) {
      return value;
    } else if (value <= min) {
      return min;
    } else if (value >= max) {
      return max;
    }
    
    // Java akan compile error jika ini tidak ada
    // karena baris ini dapat sampai jika dan HANYA
    // jika dua kondisi ini benar
    // 1. value adalah NaN
    // 2. min dan max dua-duanya adalah NaN
    //
    // Kalau NaN semua kondisi if diatas adalah false
    // maka tidak ada satu pun yang jalan. Kalau NaN
    // ada artinya ada kesalahan saat menulis rumus
    // matematika. Jadi lempar exception saja dibanding
    // sembunyikan karena NaN berarti Not-a-Number
    //
    // https://en.wikipedia.org/wiki/NaN#Comparison_with_NaN
    throw new RuntimeException("unexpected");
  }

  // Fungsi ini memeriksa apakah 'n'
  // adalah nol, fungsi ini perlu karena
  // float dan double di Java ataupun semua
  // bahasa pemograman yang ada double dan
  // float yang memiliki size limit, pembandingan
  // menggunakan == tidak disarankan karena
  // float dan double selalu keterbatasan
  // dalam presisi, 0.3 mungkin saja 0.29999999999...
  // tetapi dibulat ke 0.3. Dan contoh lain-lainnya
  //
  // Jika presisi seperti itu diperlukan Java
  // menyediakan java.math.BigDecimal yang memiliki
  // presisi tidak terbatas dan tidak terhalang
  // masalah diatas.
  //
  // Lebih baik menggunakan hanya operator pembandingan
  // yang bukan == ataupun !=
  //
  // Kode berikut dapat bekerja bara Math.signum
  // return angka baru berdasarkan tanda angka
  // jika 'n' negatif maka signum return -1.0,
  // kalau 'n' positif maka signum return 1.0
  //
  // Jika 'n' nol maka signum return 0.0. Dengan
  // kondisi seperti itu untuk memerika apakah
  // nol dapat menggunakan sign > -1.0f && sign < 1.0f
  // karena 0 diantara -1 dan 1
  public static boolean isZero(double n) {
    // Looks unnecessary but.. floats and doubles
    // well can't exactly represent. So lets use this
    // Float and double only work sanely if its not equality
    // comparisons
    double sign = Math.signum(n);
    return sign > -1.0f && sign < 1.0f;
  }

  // Fungsi ini mengambil 'degree' dalam derajat
  // (seluruh program ini selalu menggunakan derajat
  // untuk sudut)
  //
  // Dan mengubahnya menjadi bentuk "normal" dan
  // tidak ambigu diantara -10.0f derajat dan
  // 350.0f derajat. Ini juga meggunakan modulo
  // agar sudut seperti 720.0f adalah 0.0f derajat
  // putar dua kali 360 derajat sama saja dengan
  // tidak memutar sama sekali.
  //
  // Dalam modulo Java menghitung seolah-olah kedua
  // tanda sama, yaitu positif-positif. Setelah
  // dihitung Java mengambil tanda dari bilangan sebelah
  // kiri dan memasukkannya ke hasil jadi -370.0f berarti
  // -10.0f.
  //
  // Fungsi ini memilik kasus special untuk bilangan negatif
  // yang berarti berputar lawan arah jam. Jadi 360.0f + hasil
  // hasilnya bilangan positif yang menyatakan sudut
  // absolut jadi -10.0f berarti 350.0f
  //
  // Hasil yang direturn adalah [0.0f, 360.0f)
  //
  // dalam notasi interval "[" berarti inclusive
  // sedangkan ")" berarti exclusive.
  // Inclusive berarti 0.0f termasuk hasil tetapi
  // 360.0f tidak termasuk.
  public static float normalizeAngle(float degree) {
    degree %= 360.0f;

    if (degree < 0.0f) {
      // Salah satu kesalahan koding yang saya temui
      // di fungsi ini adalah menggunakan 360.0f - degree
      // itu menyebabkan hasil melebihi dari batas 360.0f
      // dikarenakan disini sudah diketahui
      // degree adalah negatif sehingga
      // -10.0f jika panggil fungsi ini maka
      // outputnya 370.0f bukan 350.0f
      degree = 360.0f + degree;
    }
    return degree;
  }
}
