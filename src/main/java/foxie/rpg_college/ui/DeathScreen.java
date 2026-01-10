package foxie.rpg_college.ui;

import java.util.Optional;
import java.util.function.Function;

import foxie.rpg_college.Game;
import foxie.rpg_college.entity.ArcherCharacter;
import foxie.rpg_college.entity.CatEntity;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.entity.MageCharacter;
import foxie.rpg_college.entity.ProjectileEntity;
import foxie.rpg_college.entity.WarriorCharacter;
import foxie.rpg_college.entity.damage.DamageSource;
import foxie.rpg_college.entity.damage.EntityDamageSource;
import foxie.rpg_college.entity.damage.ProjectileDamageSource;
import foxie.rpg_college.entity.damage.TileDamageSource;
import foxie.rpg_college.input.Keyboard;
import foxie.rpg_college.input.State;
import foxie.rpg_college.input.Keyboard.Button;

public class DeathScreen extends ScreenWithText {
  private static record Respawnable(
    String description,
    String buttonInString,
    Button button,
    Function<Game, Entity> constructor
  ) {
  };
  
  private static final Respawnable[] RESPAWNABLES = {
    new Respawnable(
      "Cat with sword",
      "1",
      Button.One,
      CatEntity::new
    ),
    new Respawnable(
      "Archer",
      "2",
      Button.Two,
      ArcherCharacter::new
    ),
    new Respawnable(
      "Mage",
      "3",
      Button.Three,
      MageCharacter::new
    ),
    new Respawnable(
      "Warrior",
      "4",
      Button.Four,
      WarriorCharacter::new
    )
  };
  
  private final String text;
  
  private final Optional<DamageSource> deathReason;
  
  public DeathScreen(Game game, Optional<DamageSource> deathReasonOptional) {
    super(game);
    this.deathReason = deathReasonOptional;
    
    StringBuilder builder = new StringBuilder();
    builder.append("YOU ARE DEAD!\n");
    if (this.deathReason.isPresent()) {
      builder.append("Reason: ");
      DamageSource deathReason = this.deathReason.get();
      if (deathReason instanceof EntityDamageSource) {
        EntityDamageSource killer = (EntityDamageSource) deathReason;
        if (killer instanceof ProjectileDamageSource) {
          ProjectileEntity projectile = ((ProjectileDamageSource) killer).getProjectile();
          builder.append("Shot to death by ");
          builder.append(killer.getSource().getName());
          builder.append("\nUsing ");
          builder.append(projectile.getName());
        } else {
          builder.append("Killed by ");
          builder.append(killer.getSource().getName());
        }
      } else if (deathReason instanceof TileDamageSource) {
        TileDamageSource killer = (TileDamageSource) deathReason;
        builder.append("Standed on ");
        builder.append(killer.getSource().getName(killer.getCoord()));
        builder.append(" for too long");
      } else {
        builder.append("Unknown killer: ");
        builder.append(deathReason.getName());
      }
      builder.append(" (Damage: ");
      builder.append(deathReason.getDamagePoint());
      builder.append(")\n");
    }
    
    builder.append("\n");
    for (Respawnable current : DeathScreen.RESPAWNABLES) {
      builder.append(". Press ");
      builder.append(current.buttonInString);
      builder.append(" to respawn as ");
      builder.append(current.description);
      builder.append("\n");
    }
    builder.append("\nGood luck on your next attempt!");
    this.text = builder.toString();
  }

  @Override
  public String getText() {
    return this.text;
  }

  @Override
  public boolean handleInput() {
    Keyboard keyboard = this.getGame().getKeyboard();
    
    for (Respawnable current : DeathScreen.RESPAWNABLES) {
      if (keyboard.getState(current.button) == State.Clicked) {
        this.getGame().respawn(current.constructor.apply(this.getGame()));
        break;
      }
    }
    
    return false;
  }

  @Override
  public boolean canTickGame() {
    return false;
  }
  
  @Override
  public void tick(float deltaTime) {
  }
}
