package foxie.rpg_college.entity.controller;

// Interface ini menjelaskan event-event yang
// controller dapat dengar yang mungkin penting
// seperti perubahan lokasi, ganti dunia atau
// juga entity tidak dapat di kendalikan lagi
//
// Ini digunakan untuk kelas InputToControllerBridge
// untuk mengupdate posisi kamera sama ukuran dunia
//
// Sedangkan onEntityNoLongerControllable untuk 
// berhenti mengontrol entity :3
public interface ControlEventListener {
  void onPositionUpdated();
  void onWorldChange();
  
  // After this the listener is automatically removed
  void onEntityNoLongerControllable();
}
