package foxie.rpg_college.entity;

public interface Attackable {
  // Whether attacker can attack or not
  boolean canAttack();
  
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
  boolean attack();
}



