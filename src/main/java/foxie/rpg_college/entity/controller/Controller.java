package foxie.rpg_college.entity.controller;

import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.Entity;

public interface Controller {
  Entity getEntity();
  boolean isActive();
  
  // Whether control should be disabled
  // used in cases like e.g. entity which
  // player controlling fainted for example
  // then it should not be able to do anything
  boolean shouldControlDisabled();
  
  // Entity implementing this must
  // fire correct event for the controller
  // to work properly
  void addListener(ControlEventListener listener);
  void removeListener(ControlEventListener listener);
  
  // The movement multiplier, it essentially
  // gives factor of movement on X and Y axis
  // the movement speed then will be multiplied
  // to that.
  void applyMovement(Vec2 multiplier);
  void setRotation(float rotation);
  void setPos(Vec2 position);
  
  // Events to be dispatched
  void dispatchOnPositionUpdated();
  void dispatchOnWorldChange();
  void dispatchOnEntityNoLongerControllable();
}

