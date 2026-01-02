package foxie.rpg_college.entity.controller;

import java.util.HashSet;
import java.util.Iterator;

import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.Entity;

public abstract class EntityController implements Controller {
  private final Entity owner;
  private final HashSet<ControlEventListener> listeners = new HashSet<>();
  
  public EntityController(Entity owner) {
    this.owner = owner;
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
  @Override
  public void dispatchOnPositionUpdated() {
    for (ControlEventListener listener : this.listeners) {
      listener.onPositionUpdated();
    }
  }
  
  @Override
  public void dispatchOnWorldChange() {
    for (ControlEventListener listener : this.listeners) {
      listener.onWorldChange();
    }
  }

  // Forwarding to listeners
  @Override
  public void dispatchOnEntityNoLongerControllable() {
    Iterator<ControlEventListener> iter = this.listeners.iterator();
    while (iter.hasNext()) {
      ControlEventListener listener = iter.next();
      iter.remove();
      listener.onEntityNoLongerControllable();
    }
  }
  
  public abstract float getMovementSpeed();
}
