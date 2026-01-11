package foxie.rpg_college.entity.controller;

import java.util.HashSet;
import java.util.Iterator;

import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.Entity;

// Kelas ini adalah implementasi dasar untuk entity
// untuk interface Controller. Untuk memudahkan menambahkan
// kontrol ke entity tanpat mengimplementasi ulang
// semuanya
public abstract class EntityController implements Controller {
  private final Entity owner;
  // Menyimpan list atau set dari objek-objek yang ingin
  // mendengar perubahan
  private final HashSet<ControlEventListener> listeners = new HashSet<>();
  
  public EntityController(Entity owner) {
    this.owner = owner;
  }
  
  @Override
  public boolean isActive() {
    // Jika ada yang mendengar di anggap
    // controller lagi aktif
    return !this.listeners.isEmpty();
  }
  
  @Override
  public Entity getEntity() {
    return owner;
  }

  @Override
  public void addListener(ControlEventListener listener) {
    this.listeners.add(listener);
  }

  @Override
  public void removeListener(ControlEventListener listener) {
    this.listeners.remove(listener);
  }

  @Override
  public void applyMovement(Vec2 multiplier) {
    // Melakukan pergerakan dengan
    // menambahkan hasil kali multiplier dengan kecepatan
    // pergerakan
    this.owner.setPos(this.owner.getPos().add(multiplier.mul(this.getMovementSpeed())));
  }

  @Override
  public void setRotation(float rotation) {
    this.owner.setRotation(rotation);
  }

  @Override
  public void setPos(Vec2 position) {
    this.owner.setPos(position);
  }

  // Forwarding to listeners
  // Memberitahukan event ke pendengar jika
  // posisi telah diupdate
  @Override
  public void dispatchOnPositionUpdated() {
    for (ControlEventListener listener : this.listeners) {
      listener.onPositionUpdated();
    }
  }
  
  // Memberitahukan event ke pendengar jika
  // dunia telah berganti
  @Override
  public void dispatchOnWorldChange() {
    for (ControlEventListener listener : this.listeners) {
      listener.onWorldChange();
    }
  }

  // Forwarding to listeners
  // Memberitahukan event ke pendengar jika
  // entity tidak dapat dikendalikan lagi
  @Override
  public void dispatchOnEntityNoLongerControllable() {
    Iterator<ControlEventListener> iter = this.listeners.iterator();
    while (iter.hasNext()) {
      ControlEventListener listener = iter.next();
      iter.remove();
      listener.onEntityNoLongerControllable();
    }
  }
  
  // Method abstrak untuk mendapatkan kecepatan
  // pergerakan
  public abstract float getMovementSpeed();
}
