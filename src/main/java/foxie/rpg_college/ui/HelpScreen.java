package foxie.rpg_college.ui;

import java.awt.Graphics2D;

import foxie.rpg_college.Game;
import foxie.rpg_college.input.Keyboard;
import foxie.rpg_college.input.State;
import foxie.rpg_college.input.Keyboard.Button;

public class HelpScreen extends ScreenWithText {
  private final Screen prevScreen;
  
  private int currentPage = 0;
  private static final String[] PAGES = {
      ////////////////////////////////////////
      """
      Controls (Help screen)
      Arrow Left => Go to previous page
      Arrow Right => Go to next page
      
      Controls (Game)
      W, A, S, D => movements
      Q => Attack at current direction
      """,
      ////////////////////////////////////////
      """
      Controls (Game)
      Right click => Aim weapon at the clicked
      spot
      Minus key => Reduce render scale
      Equals key => Increase render scale
      F11 => Toggling fullscreen mode
      
      """,
      ////////////////////////////////////////
      """
      How to play:
      Go to right and you'll find two portals
      green on leads to game mode where you
      have to survive longest with the turrets
      
      Right left side of you after starting
      you find a portal that leads to gamemode
      """,
      ////////////////////////////////////////
      ////////////////////////////////////////
      """
      where hostile creatures keep spawning
      more and more in wave and the objective
      is to survive as long as possible
      
      
      
      BOoo!
      """,
      ////////////////////////////////////////
    };
  
  public HelpScreen(Game game, Screen prevScreen) {
    super(game);
    this.prevScreen = prevScreen;
  }

  @Override
  public String getText() {
    return
      "Page " + (this.currentPage + 1) + "/" + HelpScreen.PAGES.length + ". Esc to quit.\n\n" +
      HelpScreen.PAGES[this.currentPage];
  }

  @Override
  public boolean handleInput() {
    Keyboard keyboard = this.getGame().getKeyboard();
    if (keyboard.getState(Button.Escape) == State.Clicked) {
      this.getGame().setScreen(this.prevScreen);
      return false;
    }
    
    if (keyboard.getState(Button.ArrowLeft) == State.Clicked) {
      this.currentPage -= 1;
      if (this.currentPage < 0) {
        this.currentPage = 0;
      }
    }
    
    if (keyboard.getState(Button.ArrowRight) == State.Clicked) {
      this.currentPage += 1;
      if (this.currentPage >= HelpScreen.PAGES.length) {
        this.currentPage = HelpScreen.PAGES.length - 1;
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
  
  @Override
  public void render(Graphics2D g, float deltaTime) {
    this.prevScreen.render(g, deltaTime);
    super.render(g, deltaTime);
  }
}
