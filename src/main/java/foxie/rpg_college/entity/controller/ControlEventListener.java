package foxie.rpg_college.entity.controller;

public interface ControlEventListener {
  void onPositionUpdated();
  void onWorldChange();
  
  // After this the listener is automatically removed
  void onEntityNoLongerControllable();
}
