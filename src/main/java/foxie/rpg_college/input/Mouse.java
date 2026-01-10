package foxie.rpg_college.input;

import java.awt.Window;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Vec2;

// Sama seperti Keyboard, Mouse melacak posisi
// cursor sama juga kondisi tombolnya
public class Mouse implements AutoCloseable {
  public enum Button {
    Left,
    Middle,
    Right
  }

  // berikut adalah data-data yang dibagi dengan listener
  // beserta "lock object" yang digunakan untuk mensinkron
  // modifikasi dan akses ke data berikut sehingga tidak
  // dapat diakses bersamaan dengan modifikasi.
  // NOTE: diperlukan karena listener berjalan di thread
  // berbeda pada Java AWT
  // ------------------------------------------------------
  // Shared with listener
  private final Object lock = new Object();
  private boolean buttonStateNow[] = {false, false, false};
  private boolean buttonStateClickedNow[] = {false, false, false};
  private Vec2 positionNow = new Vec2(0.0f, 0.0f);

  // Data bawah ini melacak keadaan mouse sejak terakhir di
  // update
  private boolean buttonStatePrev[] = {false, false, false};
  private State buttonState[] = {State.Unhold, State.Unhold, State.Unhold};
  private Vec2 positionSaved = new Vec2(0.0f, 0.0f);

  private final Listener listener = new Listener(this);
  private final Window window;

  private FloatRectangle watchedArea;
  private Vec2 mapWatchedArea;

  public Mouse(Window window, FloatRectangle watchedArea, Vec2 mapToArea) {
    this.window = window;
    this.watchedArea = watchedArea;
    this.mapWatchedArea = mapToArea;
    window.addMouseListener(this.listener);
    window.addMouseMotionListener(this.listener);
  }
  
  public void setWatchedArea(FloatRectangle watchedArea) {
    synchronized (this.lock) {
      this.watchedArea = watchedArea;
    }
  }

  public void updateState() {
    // Mengambil kunci di lock object
    // agar listener tidak memodifikasi
    // saat diakses
    synchronized (this.lock) {
      // lalu logikanya sama dengan keyboard
      // hanya tambahkan pemeriksaan this.buttonStateClickedNow
      for (int i = 0; i < this.buttonState.length; i++) {
        boolean prev = this.buttonStatePrev[i];
        boolean now = this.buttonStateNow[i];
        
        // Periksa apakah tombol mouse diclick
        // hasil langsung dari event MouseListener#mouseClicked
        if (this.buttonStateClickedNow[i]) {
          this.buttonState[i] = State.Clicked;
          continue;
        }

        if (prev && now) {
          this.buttonState[i] = State.Hold;
        } else if (!prev && now) {
          this.buttonState[i] = State.Clicked;
        } else if (prev && !now) {
          this.buttonState[i] = State.Unclicked;
        } else if (!prev && !now) {
          this.buttonState[i] = State.Unhold;
        }
      }

      for (int i = 0; i < this.buttonStatePrev.length; i++) {
        this.buttonStatePrev[i] = this.buttonStateNow[i];
      }
      
      for (int i = 0; i < this.buttonStateClickedNow.length; i++) {
        this.buttonStateClickedNow[i] = false;
      }

      this.positionSaved = this.positionNow;
    }
  }

  public State getButtonState(Button button) {
    return this.buttonState[button.ordinal()];
  }

  public Vec2 getMousePosition() {
    return this.positionSaved;
  }

  @Override
  public void close() throws Exception {
    this.window.removeMouseMotionListener(this.listener);
    this.window.removeMouseListener(this.listener);
  }

  private class Listener implements MouseListener, MouseMotionListener {
    private final Mouse owner;

    public Listener(Mouse owner) {
      this.owner = owner;
    }
    
    boolean isInteresting(MouseEvent e) {
      return this.owner.watchedArea.contains(new Vec2((float) e.getX(), (float) e.getY()));
    }

    void updatePosition(MouseEvent e) {
      if (!this.isInteresting(e)) {
        return;
      }
      
      // Mengambil koordinat utuhnya
      Vec2 raw = new Vec2((float) e.getX(), (float) e.getY());
      
      // Lalu mengoffset koordinat sehingga koordinat relatif
      // dengan area yang di lihat oleh kelas ini
      Vec2 offseted = raw.sub(this.owner.watchedArea.getTopLeftCorner());
      // Setelah itu hitung berapa banyak mouse bergerak dalam area
      // watchedArea
      Vec2 percentMoved = offseted.div(this.owner.watchedArea.getSize());
      
      // Dan akhirnya megubah persen tersebut sehingga menjadi koordinat
      // dalam mapWatchedArea
      // Hal ini adalah salah satu komponen yang penting untuk program agar
      // ukuran window nya dapat resize sedangkan game hanya memikirkan area
      // yang dia ingin lihat
      this.owner.positionNow = percentMoved.mul(this.owner.mapWatchedArea);
    }

    // Mouse nya keluar dari window reset semuanya
    @Override
    public void mouseExited(MouseEvent e) {
      synchronized (this.owner.lock) {
        this.owner.buttonStateNow[0] = false;
        this.owner.buttonStateNow[1] = false;
        this.owner.buttonStateNow[2] = false;
        this.updatePosition(e);
      }
    }

    void updateButton(MouseEvent e, boolean val) {
      switch (e.getButton()) {
        case MouseEvent.BUTTON1:
          this.owner.buttonStateNow[0] = val;
          break;
        case MouseEvent.BUTTON2:
          this.owner.buttonStateNow[1] = val;
          break;
        case MouseEvent.BUTTON3:
          this.owner.buttonStateNow[2] = val;
          break;
      }
    }
    
    void updateButtonClicked(MouseEvent e, boolean val) {
      switch (e.getButton()) {
        case MouseEvent.BUTTON1:
          this.owner.buttonStateClickedNow[0] = val;
          break;
        case MouseEvent.BUTTON2:
          this.owner.buttonStateClickedNow[1] = val;
          break;
        case MouseEvent.BUTTON3:
          this.owner.buttonStateClickedNow[2] = val;
          break;
      }
    }
    
    // Sisa dari method-methodnya hanya mengambil kuncil
    // lalu memanggil method yang sesuai dengan argument
    // yang sesuai. dan juga memeriksa apakah eventnya
    // menarik atau tidak (terletak dalam window yang mau diperiksa)
    //
    // mousePressed untuk menekan tombol,
    // mouseReleased untuk melepaskan tombol,
    // mouseDrag untuk mouse yang bergerak tetapi juga menahan tombol
    // mouseMoved untuk mouse yang bergerak tetapi tidak menahan tombol
    // dan mouseEntered ignore saja, karena tidak penting
    @Override
    public void mousePressed(MouseEvent e) {
      synchronized (this.owner.lock) {
        if (!this.isInteresting(e)) {
          return;
        }
        
        this.updateButton(e, true);
        this.updatePosition(e);
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      synchronized (this.owner.lock) {
        if (!this.isInteresting(e)) {
          return;
        }
        
        this.updateButton(e, false);
        this.updateButtonClicked(e, false);
        this.updatePosition(e);
      }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      synchronized (this.owner.lock) {
        if (!this.isInteresting(e)) {
          return;
        }
        this.updatePosition(e);
      }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      synchronized (this.owner.lock) {
        if (!this.isInteresting(e)) {
          return;
        }
        this.updatePosition(e);
      }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
      synchronized (this.owner.lock) {
        if (!this.isInteresting(e)) {
          return;
        }
        
        this.updatePosition(e);
        this.updateButtonClicked(e, true);
      }
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {      
    }
  }
}
