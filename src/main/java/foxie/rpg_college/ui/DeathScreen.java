package foxie.rpg_college.ui;

import java.util.function.Function;

import foxie.rpg_college.Game;
import foxie.rpg_college.entity.ArcherCharacter;
import foxie.rpg_college.entity.CatEntity;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.entity.MageCharacter;
import foxie.rpg_college.entity.WarriorCharacter;
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
  
  private static String build() {
    StringBuilder builder = new StringBuilder();
    builder.append("YOU ARE DEAD!\n\n");
    for (Respawnable current : DeathScreen.RESPAWNABLES) {
      builder.append(". Press ");
      builder.append(current.buttonInString);
      builder.append(" to respawn as ");
      builder.append(current.description);
      builder.append("\n");
    }
    builder.append("\nGood luck on your next attempt!");
    return builder.toString();
  }
  private static final String TEXT = DeathScreen.build();
  
  public DeathScreen(Game game) {
    super(game);
  }

  @Override
  public String getText() {
    return DeathScreen.TEXT;
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
}
