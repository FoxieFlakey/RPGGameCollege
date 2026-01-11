package foxie.rpg_college.entity.controller;

import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.Entity;

// Interface Controller ini di-implementasi
// pada kelas Entity maupun kelas berbeda
// untuk mengendalikan sebuah entity. Agar
// terpisah kode untuk logika entity sendiri
// sama logik yang mengcontrol entity (pergerakan
// rotasi, etc)
//
// Kelas yang mengimplemtnasi Controller dapat di
// kasih ke kelas-kelas lain yang tugas nya adalah
// mengontrol contohnya InputToControllerBridge
// adlah kelas yang menghandle input dari user
// lalu megubahnya menjadi panggilan ke interface ini
// yang lalu entity bereaksi dengan bergerak
//
// Controller juga menyediakan getEntity untuk fungsi
// lain seperti mengattack bagi kelas yang
// mengimplementasi interface Attackable
//
// Controller lebih bertugas pada posisi dan rotasi ^w^
public interface Controller {
  Entity getEntity();

  // Apakah ada controller yang tersambung
  // ke controller ini
  boolean isActive();
  
  // Whether control should be disabled
  // used in cases like e.g. entity which
  // player controlling fainted for example
  // then it should not be able to do anything
  // -----------------------------------------
  // Apakah kendali perlu dimatikan digunakan
  // kondisi seperti entity sudah mati, entity
  // pingsan, dan lain lain. Jadi player tidak
  // dapat menggerkkan nya
  boolean shouldControlDisabled();
  
  // Entity implementing this must
  // fire correct event for the controller
  // to work properly
  // --------------------------------------------
  // Menambahkan pendengar kontrol seperti
  // berganti dunia, entity tidak bisa di kontrol,
  // etc. Minimal jika ada satu pendengar maka
  // Controller dianggap aktif digunakans
  void addListener(ControlEventListener listener);
  void removeListener(ControlEventListener listener);
  
  // The movement multiplier, it essentially
  // gives factor of movement on X and Y axis
  // the movement speed then will be multiplied
  // to that.
  // --------------------------------------------
  // Memberikan gerakan, dengan multiplier tertentu
  // 1.0 berarti bergerak pada kecepatan normal
  // kalau lebih tinggi berarti bergerak sedikit lebih
  // cepat dari normal, contohnya sedang berlari
  // atau crouching, etc
  void applyMovement(Vec2 multiplier);

  // Menset menhadap kemana
  void setRotation(float rotation);
  void setPos(Vec2 position);
  
  // Events to be dispatched
  // ---------------------------
  // event-event yang perlu dilakukan
  // saat posisi mengganti, dunia berganti
  // dan entity tidak bisa dikontrol
  void dispatchOnPositionUpdated();
  void dispatchOnWorldChange();
  void dispatchOnEntityNoLongerControllable();
}

