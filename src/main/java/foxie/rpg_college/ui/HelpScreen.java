package foxie.rpg_college.ui;

import java.awt.Graphics2D;

import foxie.rpg_college.Game;
import foxie.rpg_college.input.Keyboard;
import foxie.rpg_college.input.State;
import foxie.rpg_college.input.Keyboard.Button;

// Kelas ini menampilkan layar pembantu ke pengguna
public class HelpScreen extends ScreenWithText {
  // Menyimpan layar sebelumnya sehingga
  // saat keluar dari help screen mereturn
  // ke screen sebelumnya
  private final Screen prevScreen;
  
  // Halaman yang sedang aktif, basis 0
  // seperti Java perlukan
  private int currentPage = 0;

  // Konstanta, dari teks untuk layar pembantu
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
  
  // HelpScreen memerlukan reference ke layar
  // sebelumnya agar dapat direturn sama dirender
  // layar sebelumnya. Karena helpscreen menimpa
  // output dari layar sebelumnya
  public HelpScreen(Game game, Screen prevScreen) {
    super(game);
    this.prevScreen = prevScreen;
  }

  @Override
  public String getText() {
    // Menampilkan teks yang sedang aktif
    // beserta teks nya
    return
      "Page " + (this.currentPage + 1) + "/" + HelpScreen.PAGES.length + ". Esc to quit.\n\n" +
      HelpScreen.PAGES[this.currentPage];
  }

  @Override
  public boolean handleInput() {
    // Menghandle input untuk help screennya

    Keyboard keyboard = this.getGame().getKeyboard();
    if (keyboard.getState(Button.Escape) == State.Clicked) {
      // Escape untuk keluar lalu set layar menjadi layar
      // sebelumnya lalu return false, agar input tidak
      // dilanjutkan ke kode berikutnya
      this.getGame().setScreen(this.prevScreen);
      return false;
    }
    
    if (keyboard.getState(Button.ArrowLeft) == State.Clicked) {
      // bergerak ke kiri, halaman -1 lalu mencek
      // agar halamat tetap berhenti di 0, menghindari
      // menghindari out of bound pada array
      this.currentPage -= 1;
      if (this.currentPage < 0) {
        this.currentPage = 0;
      }
    }
    
    if (keyboard.getState(Button.ArrowRight) == State.Clicked) {
      // bergerak ke kanan, halaman +1 lalu mencek
      // agar halamat tetap berhenti di index maximum,
      // menghindari out of bound pada array
      this.currentPage += 1;
      if (this.currentPage >= HelpScreen.PAGES.length) {
        this.currentPage = HelpScreen.PAGES.length - 1;
      }
    }
    
    // Return false karena semua input dicuri oleh
    // layar ini hehe UwU
    return false;
  }

  @Override
  public boolean canTickGame() {
    // Saat help screen muncul game dipause
    return false;
  }
  
  @Override
  public void tick(float deltaTime) {
    // Layar pembantu tidak melakukan apa-apa
  }
  
  @Override
  public void render(Graphics2D g, float deltaTime) {
    // Petama render layar sebelumnya
    this.prevScreen.render(g, deltaTime);

    // Lalu timpa dengan output layar ini
    super.render(g, deltaTime);
  }
}
