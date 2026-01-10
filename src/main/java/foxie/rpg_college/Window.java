package foxie.rpg_college;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.util.Optional;

import foxie.rpg_college.input.Keyboard;
import foxie.rpg_college.input.Mouse;

// Kelas yang memanejeman Frame yang didapat dari AWT
// beserta input/output nya
public class Window implements AutoCloseable {
  private Frame window;
  private Mouse mouse;
  private Keyboard keyboard;
  private BufferStrategy bufferStrategy;
  
  // Variabel-variabel ini berkaitan dengan fungsi fullscreen untuk game
  private static final GraphicsEnvironment GRAPHICS_ENVIRONMENT = GraphicsEnvironment.getLocalGraphicsEnvironment();
  private static final GraphicsDevice GRAPHICS_DEVICE = GRAPHICS_ENVIRONMENT.getDefaultScreenDevice();
  private static final boolean IS_FULLSCREEN_SUPPORTED = GRAPHICS_DEVICE.isFullScreenSupported();
  
  private int currentWidth;
  private int currentHeight;
  private boolean isClosed;
  private boolean isFullscreen = false;
  private FloatRectangle outputArea;
  
  // Isi-isi dari data bawah ini dishare dengan
  // listener yang berjalan di thread lain
  // aksees di sinkron dengan window nya
  // seperti
  //
  // synchronized (this) {
  //   ...
  // }
  private int sharedCurrentWidth;
  private int sharedCurrentHeight;
  private boolean sharedIsClosed;
  private FloatRectangle sharedOutputArea;
  
  private final Listener listener;
  private final float outputAspectRatio;
  private final Vec2 mouseRemappedArea;
  private final IVec2 size;
  private final IVec2 minSize;
  
  // size menyatakan window ukuran berapa saat dibuat
  // minSize menyatakan window minimal sebesar apa
  // mouseRemappedArea meyatakan mouse akan ditranslate kemana.
  // outputAspectRatio adalah rasio lebar dan tinggi dari output yang game ingin hasilkan
  public Window(IVec2 size, IVec2 minSize, Vec2 mouseRemappedArea, float outputAspectRatio) {
    this.outputAspectRatio = outputAspectRatio;
    this.listener = new Listener();
    this.mouseRemappedArea = mouseRemappedArea;
    this.size = size;
    this.minSize = minSize;
    this.initWindowStuffs(true);
  }
  
  // Mengisi variabel-variabel lainnya
  // dan membuat window, diperlukan untuk
  // fullscreen karena setUndecorated memerlukan window
  // yang belum pernah tampil. Diperlukan untuk fungsi
  // fullscreen untuk menghilangkan dekorasi window
  // dan tombol-tombol nya sepeerti close, minize, etc
  private void initWindowStuffs(boolean isDecorated) {
    this.window = new Frame();
    this.window.setSize(size.x(), size.y());
    this.window.setMinimumSize(new Dimension(minSize.x(), minSize.y()));
    this.window.setFocusable(true);
    this.window.setUndecorated(!isDecorated);
    this.window.setVisible(true);
    
    // buat input untuk window
    this.mouse = new Mouse(this.window, this.outputArea, this.mouseRemappedArea);
    this.keyboard = new Keyboard(this.window);
    
    // Buat buffer-buffer baru untuk window, sehingga screen-tearing tidak terjadi
    // sama sekali. Ini membuat sistem buffer yang sederhana dan palign banyak digunakan
    // yaitu double-buffering. Dimana satu buffer menyimpan keadaan window untuk di
    // tampil (front buffer). Dan satu lagi dinamai back buffer. akan di render dan
    // diedit oleh pengguna.
    //
    // Saat ready dapat memanggil .show() pada instance buffer strategy untuk secara atomik
    // dan tidak terbagi menampilkan hasil tanpa ada masalah user melihat buffer
    // setengah jadi.
    //
    // Untuk saat ini program tidak menggunakan buffer strategy nya
    // awalnya direncanakan untuk menggunakannya tetapi ada sediki komplikasi
    // dan saya tukar dengan double buffering manual di dalam kelas Game
    // jika satu hari dapat menyelesaikan komplikasinya akan berganti menggunakan
    // buffer strategy.
    //
    // Masalahnya adalah kalau jalan di web browser, programnya dapat jalan
    // di web browser tanpa diedit melalui CheerpJ (https://cheerpj.com/). Lengkap dengan UI dan semua
    // yang tersedia di Java. Tetapi saat saya coba pakai di web browser error
    // berkaitan dengan penggunaan kelas ini. Dan CheerpJ kinda... proprietary so
    // i cant figure whats wrong the error is thrown from CheerpJ side. Rencananya
    // ingin coba jalan di web hehe UwU. I dont know if i was using it wrongly somewhere
    // and coincidentally it worked well on desktop, or it was buggy on CheerpJ side
    //
    // Kalau dijalan secara normal berjalan dengan normal
    this.window.createBufferStrategy(2);
    this.bufferStrategy = Optional.ofNullable(this.window.getBufferStrategy()).get();
    
    // Trigger resized event to fills the sizing fields
    // -----------------------------------------------------
    // melakukan update sekali untuk mengupdate field-field
    // yang mungkin berganti value saat window dibuat ulang
    this.listener.componentResized(null);
    this.updateState();
    
    // Tambahkan listener untuk event-event pada window
    this.window.addWindowListener(this.listener);
    this.window.addComponentListener(this.listener);
  }
  
  public void toggleFullscreen() {
    if (!Window.IS_FULLSCREEN_SUPPORTED) {
      // Fullscreen tidak disupported, kacangkan/ignore saja
      return;
    }
    
    // Jika disupport set fullscreen
    this.setFullscreen(!this.isFullscreen);
  }
  
  public void setFullscreen(boolean val) {
    if (val && !this.isFullscreen) {
      // Tutup window nya, setUndecorated perlu window yang belum pernah tampil
      this.window.dispose();
      // Lalu buat lagi dengan value dekorasi yang baru
      this.initWindowStuffs(false);
      // Setelah buat window fullscreen
      Window.GRAPHICS_DEVICE.setFullScreenWindow(this.window);
    } else if (!val && this.isFullscreen) {
      // Tutup window nya, setUndecorated perlu window yang belum pernah tampil
      this.window.dispose();
      // Lalu buat lagi dengan value dekorasi yang baru
      this.initWindowStuffs(true);
      // Setelah buat window tidak fullscreen lagi
      Window.GRAPHICS_DEVICE.setFullScreenWindow(null);
    }
    this.isFullscreen = val;
  }
  
  // Mengupdate state dari window nya
  // sehingga sesuai dengan yang keadaan
  // sekarang.  Karena listener berjalan di thread
  // berbeda di Java AWT dan perlu sinkronisasi
  //
  // Java juga memiliki keyword synchronized yang
  // mengunci sebuah instance sehingga blok-blok
  // synchronized lain tidak bisa berjalan bersamaan
  //
  // Jika diletak di method kodenya sama dengan
  //
  // // Memakai keyword di blok
  // public void namaMethod() {
  //   synchronized (this) {
  //     ...
  //   }
  // }
  //
  // // Memakai keyword di method
  // public synchronized void namaMethod() {
  //   ...
  // }
  public synchronized void updateState() {
    this.mouse.updateState();
    this.keyboard.updateState();
    
    this.isClosed = this.sharedIsClosed;
    this.currentWidth = this.sharedCurrentWidth;
    this.currentHeight = this.sharedCurrentHeight;
    this.outputArea = this.sharedOutputArea;
  }
  
  public int getRenderWidth() {
    return (int) this.outputArea.getSize().x();
  }
  
  public int getRenderHeight() {
    return (int) this.outputArea.getSize().y();
  }
  
  public int getWindowWidth() {
    return this.currentWidth;
  }
  
  public int getWindowHeight() {
    return this.currentHeight;
  }
  
  public boolean isClosed() {
    return this.isClosed;
  }
  
  public FloatRectangle getOutputArea() {
    return this.outputArea;
  }
  
  public Frame getWindow() {
    return this.window;
  }
  
  public Keyboard getKeyboard() {
    return this.keyboard;
  }
  
  public Mouse getMouse() {
    return this.mouse;
  }
  
  public BufferStrategy getBufferStrategy() {
    return this.bufferStrategy;
  }
  
  @Override
  public void close() throws Exception {
    this.window.removeComponentListener(this.listener);
    this.window.removeWindowListener(this.listener);
    this.window.dispose();
  }
  
  // Fungsi ini menghitung area window terbesar dimana program
  // dapat menampilkan output selama aspect ratio dijaga. Metode
  // yang digunakan adalah letterboxing. Letter boxing menyisihkan
  // area kosong di kiri/kanan atau atas/bawah jika window sebenarnya
  // lebih lebar atau lebih tinggi dibanding output gamenya.
  //
  // Area kosong tersebut dapat di-isi dengan pixel warna hitam atau
  // warna lainnya
  //
  // Algoritmanya ini tebak-menebak saya bikin, kurang tau cara kerjanya.
  // saya tebak, edit, lihat, tebak, edit sedikit, etc sampai bisa
  // kurang tau kalau ini ada kesalahan *sad foxie noise*
  private FloatRectangle calcOutputArea() {
    float somethingWidth = this.outputAspectRatio;
    float somethingHeight = 1.0f;
    float somethingAspect = somethingWidth / somethingHeight;
    
    float actualWidth = (float) this.window.getWidth();
    float actualHeight = (float) this.window.getHeight();
    
    float neededWidthIfHeightIsScaledToFit = actualHeight * somethingAspect;
    
    float scale;
    if (neededWidthIfHeightIsScaledToFit > actualWidth) {
      scale = actualWidth / somethingWidth;
    } else {
      scale = actualHeight / somethingHeight;
    }
    
    float letterBoxedWidth = somethingWidth * scale;
    float letterBoxedHeight = somethingHeight * scale;
    float xOffset = Math.max((actualWidth - letterBoxedWidth) / 2.0f, 0.0f);
    float yOffset = Math.max((actualHeight - letterBoxedHeight) / 2.0f, 0.0f);
    
    return new FloatRectangle(
      new Vec2(xOffset, yOffset),
      new Vec2(xOffset + letterBoxedWidth, yOffset + letterBoxedHeight)
    );
  }
  
  private class Listener implements ComponentListener, WindowListener {
    @Override
    public void windowClosing(WindowEvent e) {
      // Java memiliki syntax ClassName.this untuk kelas
      // yang bukan statis, kelas yang bukan statis di Java
      // menyimpan reference ke kelas yang memilikinya.
      //
      // Dan cara mengaksesnya dari dalam menggunakan Window.this/
      // untuk kelas-kelas yang nested didalam
      synchronized (Window.this) {
        Window.this.sharedIsClosed = true;
      }
    }
    
    @Override
    public void componentResized(ComponentEvent e) {
      synchronized (Window.this) {
        // Hitung ulang output are dengan algoritma letter boxing
        // dan update mouse sehingga mouse tau area mana yang akan
        // dilihatnya
        FloatRectangle outputArea = Window.this.calcOutputArea();
        Window.this.sharedOutputArea = outputArea;
        Window.this.sharedCurrentWidth = Window.this.window.getWidth();
        Window.this.sharedCurrentHeight = Window.this.window.getHeight();
        Window.this.mouse.setWatchedArea(outputArea);
      }
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }
  }
}

