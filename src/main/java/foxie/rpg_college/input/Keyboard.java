package foxie.rpg_college.input;

import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Optional;

// AutoCloseable adalah interface Java yang memungkinkan
// kelas untuk digunakan dalam try-use untuk otomatis
// membersihkan resource-resource yang digunakannya
public class Keyboard implements AutoCloseable {
  private final boolean buttonStateNow[] = new boolean[Button.values().length];
  
  private final boolean buttonStatePrev[] = new boolean[Button.values().length];
  private final State buttonState[] = new State[Button.values().length];

  private final Listener listener;
  private final Window window;

  public Keyboard(Window window) {
    this.window = window;
    this.listener = new Listener(this);
    window.addKeyListener(this.listener);
  }

  // Mengupdate this.buttonState dari keadaan
  // keyboard sekarang
  //
  // Saat keyboard baru dibuat dianggap sebelumnya buttonStatePrev
  // tidak ditekan. Lalu saat ada tombol ditekan listener memasukkan
  // true di buttonStateNow mengindikasi kalau tombol telah ditekan
  //
  // Beberapa saat kemudian program main memanggil fungsi ini untuk
  // memeriksa update baru di tombol contohnya sebelumnya tombol
  // belum ditekan lalu ditekan, artinya keadaan tombolnya adalah
  // State.Clicked artinya sudah diklik (klik adalah sebelumnya lepas
  // sekarang tekan)
  public void updateState() {
    // Synchronized ini diperlukan karena mengakses array yang mungkin
    // lagi dimodifikasi oleh listener
    synchronized (this.buttonStateNow) {
      int numKeys = Button.values().length;
      
      for (int i = 0; i < numKeys; i++) {
        // Mengambil keadaan tombol sebelum sama sekarang
        boolean prev = this.buttonStatePrev[i];
        boolean now = this.buttonStateNow[i];

        if (prev && now) {
          // Jika sebelum ditekan dan sesudah ditekan artinya ditahan
          this.buttonState[i] = State.Hold;
        } else if (prev && !now) {
          // Jika sebelum ditekan dan sesudah tidak ditekan artinya
          // tidak di click
          this.buttonState[i] = State.Unclicked;
        } else if (!prev && now) {
          // Jika sebelum tidak ditekan dan sesudah ditekan artinya diclick
          this.buttonState[i] = State.Clicked;
        } else if (!prev && !now) {
          // Jika sebelum tidak ditekan dan sesudah tidak ditekan
          // arti tidak ditahan
          this.buttonState[i] = State.Unhold;
        }
      }

      // Mengupdate keadaan tombol sehingga tombol sebelumnya
      // sama dengan sekarang agar pemanggilan berikutnya dapat
      // melihat update baru
      for (int i = 0; i < numKeys; i++) {
        this.buttonStatePrev[i] = this.buttonStateNow[i];
      }
    }
  }

  // Mengambil keadaan tombol, ini tidak memerlukan
  // synchronized karena 'buttonState' tidak diakses
  // oleh thread yang membaca events
  public State getState(Button button) {
    return this.buttonState[button.ordinal()];
  }

  // Menutup dan melakukan cleanup sehingga listener
  // tidak perlu lagi karena Keyboard sudah selesai
  // dipakai.
  @Override
  public void close() throws Exception {
    this.window.removeKeyListener(this.listener);
  }

  // Kelas listener yang mendengar aksi-aksi
  // yang terjadi berkaitan dengan tombol keyboard
  private class Listener extends KeyAdapter {
    private final Keyboard owner;

    public Listener(Keyboard owner) {
      this.owner = owner;
    }

    @Override
    public void keyReleased(KeyEvent e) {
      Optional<Button> button = fromVirtualKey(e.getKeyCode());
      if (button.isEmpty()) {
        // Tombol tidak dikenali lalu kacangkan/ignore saja
        return;
      }

      // Pertama menggunakan block synchornized kita mengambil
      // kunci ekslusif di array buttonStateNow sehingga game
      // tidak bisa mengakses isi saat tengah modifikasi
      //
      // Ini diperlukan karena di Java AWT, method-method listener
      // berjalan di thread berbeda dan jalannya concurrent atau
      // bersamaan denga main thread (thread yang memanggil fungsi
      // main dan akhirnya menjalankan kode game)
      synchronized (this.owner.buttonStateNow) {
        this.owner.buttonStateNow[button.get().ordinal()] = false;
      }
    }

    @Override
    public void keyPressed(KeyEvent e) {
      Optional<Button> button = fromVirtualKey(e.getKeyCode());
      if (button.isEmpty()) {
        return;
      }

      // Sama seperti untuk method keyReleased, hanya disimpan
      // true karena telah di tekan
      synchronized (this.owner.buttonStateNow) {
        this.owner.buttonStateNow[button.get().ordinal()] = true;
      }
    }

    // Method ini mengubah tombol dari KeyEvent.VK_* menjadi
    // salah satu varian dari Button. Untuk memudahkan penggunaan
    //
    // Note: ini kurang lengkap
    private static Optional<Button> fromVirtualKey(int a) {
      switch (a) {
        case KeyEvent.VK_0:
          return Optional.of(Button.Zero);
        case KeyEvent.VK_1:
          return Optional.of(Button.One);
        case KeyEvent.VK_2:
          return Optional.of(Button.Two);
        case KeyEvent.VK_3:
          return Optional.of(Button.Three);
        case KeyEvent.VK_4:
          return Optional.of(Button.Four);
        case KeyEvent.VK_5:
          return Optional.of(Button.Five);
        case KeyEvent.VK_6:
          return Optional.of(Button.Six);
        case KeyEvent.VK_7:
          return Optional.of(Button.Seven);
        case KeyEvent.VK_8:
          return Optional.of(Button.Eight);
        case KeyEvent.VK_9:
          return Optional.of(Button.Nine);
        case KeyEvent.VK_A:
          return Optional.of(Button.A);
        case KeyEvent.VK_B:
          return Optional.of(Button.B);
        case KeyEvent.VK_C:
          return Optional.of(Button.C);
        case KeyEvent.VK_D:
          return Optional.of(Button.D);
        case KeyEvent.VK_E:
          return Optional.of(Button.E);
        case KeyEvent.VK_F:
          return Optional.of(Button.F);
        case KeyEvent.VK_G:
          return Optional.of(Button.G);
        case KeyEvent.VK_H:
          return Optional.of(Button.H);
        case KeyEvent.VK_I:
          return Optional.of(Button.I);
        case KeyEvent.VK_J:
          return Optional.of(Button.J);
        case KeyEvent.VK_K:
          return Optional.of(Button.K);
        case KeyEvent.VK_L:
          return Optional.of(Button.L);
        case KeyEvent.VK_M:
          return Optional.of(Button.M);
        case KeyEvent.VK_N:
          return Optional.of(Button.N);
        case KeyEvent.VK_O:
          return Optional.of(Button.O);
        case KeyEvent.VK_P:
          return Optional.of(Button.P);
        case KeyEvent.VK_Q:
          return Optional.of(Button.Q);
        case KeyEvent.VK_R:
          return Optional.of(Button.R);
        case KeyEvent.VK_S:
          return Optional.of(Button.S);
        case KeyEvent.VK_T:
          return Optional.of(Button.T);
        case KeyEvent.VK_U:
          return Optional.of(Button.U);
        case KeyEvent.VK_V:
          return Optional.of(Button.V);
        case KeyEvent.VK_W:
          return Optional.of(Button.W);
        case KeyEvent.VK_X:
          return Optional.of(Button.X);
        case KeyEvent.VK_Y:
          return Optional.of(Button.Y);
        case KeyEvent.VK_Z:
          return Optional.of(Button.Z);
        case KeyEvent.VK_F1:
          return Optional.of(Button.F1);
        case KeyEvent.VK_F2:
          return Optional.of(Button.F2);
        case KeyEvent.VK_F3:
          return Optional.of(Button.F3);
        case KeyEvent.VK_F4:
          return Optional.of(Button.F4);
        case KeyEvent.VK_F5:
          return Optional.of(Button.F5);
        case KeyEvent.VK_F6:
          return Optional.of(Button.F6);
        case KeyEvent.VK_F7:
          return Optional.of(Button.F7);
        case KeyEvent.VK_F8:
          return Optional.of(Button.F8);
        case KeyEvent.VK_F9:
          return Optional.of(Button.F9);
        case KeyEvent.VK_F10:
          return Optional.of(Button.F10);
        case KeyEvent.VK_F11:
          return Optional.of(Button.F11);
        case KeyEvent.VK_F12:
          return Optional.of(Button.F12);
        case KeyEvent.VK_ESCAPE:
          return Optional.of(Button.Escape);
        case KeyEvent.VK_TAB:
          return Optional.of(Button.Tab);
        case KeyEvent.VK_CAPS_LOCK:
          return Optional.of(Button.CapsLock);
        case KeyEvent.VK_SHIFT:
          return Optional.of(Button.Shift);
        case KeyEvent.VK_CONTROL:
          return Optional.of(Button.Control);
        case KeyEvent.VK_CONTEXT_MENU:
          return Optional.of(Button.Context);
        case KeyEvent.VK_ALT:
          return Optional.of(Button.Alt);
        case KeyEvent.VK_WINDOWS:
          return Optional.of(Button.Windows);
        case KeyEvent.VK_SPACE:
          return Optional.of(Button.Space);
        case KeyEvent.VK_COMMA:
          return Optional.of(Button.Comma);
        case KeyEvent.VK_PERIOD:
          return Optional.of(Button.Period);
        case KeyEvent.VK_QUOTE:
          return Optional.of(Button.SingleQuote);
        case KeyEvent.VK_BACK_SLASH:
          return Optional.of(Button.Backslash);
        case KeyEvent.VK_SLASH:
          return Optional.of(Button.Slash);
        case KeyEvent.VK_SEMICOLON:
          return Optional.of(Button.Semicolon);
        case KeyEvent.VK_MINUS:
          return Optional.of(Button.Minus);
        case KeyEvent.VK_EQUALS:
          return Optional.of(Button.Equal);
        case KeyEvent.VK_BACK_SPACE:
          return Optional.of(Button.Backspace);
        case KeyEvent.VK_ENTER:
          return Optional.of(Button.Enter);
        case KeyEvent.VK_INSERT:
          return Optional.of(Button.Insert);
        case KeyEvent.VK_DELETE:
          return Optional.of(Button.Delete);
        case KeyEvent.VK_HOME:
          return Optional.of(Button.Home);
        case KeyEvent.VK_END:
          return Optional.of(Button.End);
        case KeyEvent.VK_PAGE_UP:
          return Optional.of(Button.PageDown);
        case KeyEvent.VK_UP:
          return Optional.of(Button.ArrowUp);
        case KeyEvent.VK_DOWN:
          return Optional.of(Button.ArrowDown);
        case KeyEvent.VK_LEFT:
          return Optional.of(Button.ArrowLeft);
        case KeyEvent.VK_RIGHT:
          return Optional.of(Button.ArrowRight);
        case KeyEvent.VK_BRACELEFT:
          return Optional.of(Button.OpenBracket);
        case KeyEvent.VK_BRACERIGHT:
          return Optional.of(Button.CloseBracket);
      }

      return Optional.empty();
    }
  }

  // Enum berisi semua tombol yang dikenali
  public enum Button {
    A,
    B,
    C,
    D,
    E,
    F,
    G,
    H,
    I,
    J,
    K,
    L,
    M,
    N,
    O,
    P,
    Q,
    R,
    S,
    T,
    U,
    V,
    W,
    X,
    Y,
    Z,
    Zero,
    One,
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,
    Shift,
    Control,
    Alt,
    Windows,
    Context,
    Enter,
    Backspace,
    CapsLock,
    Tab,
    Home,
    End,
    Insert,
    Delete,
    PageUp,
    PageDown,
    Comma,
    Period,
    Backslash,
    SingleQuote,
    Slash,
    Semicolon,
    Minus,
    Equal,
    BackQuote,
    Escape,
    F1,
    F2,
    F3,
    F4,
    F5,
    F6,
    F7,
    F8,
    F9,
    F10,
    F11,
    F12,
    ArrowUp,
    ArrowDown,
    ArrowLeft,
    ArrowRight,
    Space,
    OpenBracket,
    CloseBracket
  }
}
