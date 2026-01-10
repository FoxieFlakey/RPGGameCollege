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

public class Window implements AutoCloseable {
  private Frame window;
  private Mouse mouse;
  private Keyboard keyboard;
  private BufferStrategy bufferStrategy;
  
  private static final GraphicsEnvironment GRAPHICS_ENVIRONMENT = GraphicsEnvironment.getLocalGraphicsEnvironment();
  private static final GraphicsDevice GRAPHICS_DEVICE = GRAPHICS_ENVIRONMENT.getDefaultScreenDevice();
  private static final boolean IS_FULLSCREEN_SUPPORTED = GRAPHICS_DEVICE.isFullScreenSupported();
  
  private int currentWidth;
  private int currentHeight;
  private boolean isClosed;
  private boolean isFullscreen = false;
  private FloatRectangle outputArea;
  
  private int sharedCurrentWidth;
  private int sharedCurrentHeight;
  private boolean sharedIsClosed;
  private FloatRectangle sharedOutputArea;
  
  private final Listener listener;
  private final float outputAspectRatio;
  private final Vec2 mouseRemappedArea;
  private final IVec2 size;
  private final IVec2 minSize;
  
  public Window(IVec2 size, IVec2 minSize, Vec2 mouseRemappedArea, float outputAspectRatio) {
    this.outputAspectRatio = outputAspectRatio;
    this.listener = new Listener();
    this.mouseRemappedArea = mouseRemappedArea;
    this.size = size;
    this.minSize = minSize;
    this.initWindowStuffs(true);
  }
  
  private void initWindowStuffs(boolean isDecorated) {
    this.window = new Frame();
    this.window.setSize(size.x(), size.y());
    this.window.setMinimumSize(new Dimension(minSize.x(), minSize.y()));
    this.window.setFocusable(true);
    this.window.setUndecorated(!isDecorated);
    this.window.setVisible(true);
    
    this.mouse = new Mouse(this.window, this.outputArea, this.mouseRemappedArea);
    this.keyboard = new Keyboard(this.window);
    
    this.window.createBufferStrategy(2);
    this.bufferStrategy = Optional.ofNullable(this.window.getBufferStrategy()).get();
    
    // Trigger resized event to fills the sizing fields
    this.listener.componentResized(null);
    this.updateState();
    
    this.window.addWindowListener(this.listener);
    this.window.addComponentListener(this.listener);
  }
  
  public void toggleFullscreen() {
    if (!Window.IS_FULLSCREEN_SUPPORTED) {
      return;
    }
    
    this.setFullscreen(!this.isFullscreen);
  }
  
  public void setFullscreen(boolean val) {
    if (val && !this.isFullscreen) {
      this.window.dispose();
      this.initWindowStuffs(false);
      Window.GRAPHICS_DEVICE.setFullScreenWindow(this.window);
    } else if (!val && this.isFullscreen) {
      this.window.dispose();
      this.initWindowStuffs(true);
      Window.GRAPHICS_DEVICE.setFullScreenWindow(null);
    }
    this.isFullscreen = val;
  }
  
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
      synchronized (Window.this) {
        Window.this.sharedIsClosed = true;
      }
    }
    
    @Override
    public void componentResized(ComponentEvent e) {
      synchronized (Window.this) {
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

