package foxie.rpg_college.entity;

// Interface attackable menyatakan kalau sebuah entity/karakter
// or object dapat menyerang
public interface Attackable {
  // Whether attacker can attack or not
  // -----------------------------------
  // Apakah object/entity/karakter dapat menyerang
  // fungsi ini dapat digunakan untuk mengetahui
  // jika penyerang masih dalam cooldown atau tidak
  // dapat menyerang.
  boolean canAttack();
  
  // Method yang dimana pemanggil tau entity spesifik
  // mana yang akan diserang. Entity specifik lebih
  // ke saran kalau entity/object/karakter ini dapat
  // menarget. Kalau tidak defaultnya adalah melanjutkan
  // ke Attackable#attack yang menyerang secara umum
  //
  // Salah satu kegunaan ini adalah contohnya jika
  // di override oleh Archer, archer dapat otomatis
  // menghitung arah mana menembak ke entity 'other'
  // dan juga mengkalkulasi waktu untuk panahnya untuk
  // sampai seperti memprediksi dimana entity nya akan
  // berada agar tidak melewati.
  //
  // Method return true jika berhasil menyerang (tidak
  // selalu mengurangi damage ataupun mengurangi HP
  // contohnya pada archer saat berhasil meneyerang
  // arrownya sedang lagi terbang ke target atau
  // kalau warrior pedangnya lagi diayun...)
  //
  // Walaupun return true, entitiy lain mungkin tidak
  // dapat menerima damage nya. Seperti jika panahnya
  // meleset, entity membatalkan attack entity ini (
  // melalui interface Defenseable), etc
  default boolean attackSpecific(LivingEntity other) {
    if (!this.canAttack()) {
      return false;
    }
    
    return this.attack();
  }
  
  // Called when asked to attack unspecified
  // targt, for example. Calling attack on archer
  // would make archer shoot at whatever direction
  // they're facing and possible there no hit.
  //
  // While the attackSpecific method would make archer
  // does trigonometry stuffs and stuffs and leading
  // etccccccc... to try target the specific living entity
  //
  // Return whether attack is performed or not
  // ------------------------------------------------------
  // Method ini meminta implementasi untuk meyerang sekarang
  // yang arahnya tidak tentu atau secara umum. Contohnya
  // pada archer, dan mage melempar bola api kearah yang dihadap
  // sedangkan warrior mengayun pedang kearah yang dihadapnya
  //
  // Return true jika attack "berhasil" dilakukan tetapi tidak
  // tentu apakah attacknya berhasil mendamage atau tidak. Kalau
  // returnnya false, attack gagal contohnya karena habis mananya
  // atau masih dalam cooldown, etc
  boolean attack();
}



