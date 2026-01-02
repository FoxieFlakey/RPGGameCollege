package foxie.rpg_college.input;

import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Vec2;

public class Mouse implements AutoCloseable {
  public enum Button {
    Left,
    Middle,
    Right
  }

  // Shared with listener
  private final Object lock = new Object();
  private boolean buttonStateNow[] = {false, false, false};
  private Vec2 positionNow = new Vec2(0.0f, 0.0f);

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
    this.watchedArea = watchedArea;
  }

  public void updateState() {
    synchronized (this.lock) {
      for (int i = 0; i < this.buttonState.length; i++) {
        boolean prev = this.buttonStatePrev[i];
        boolean now = this.buttonStateNow[i];

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

  private class Listener extends MouseAdapter {
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
      
      Vec2 raw = new Vec2((float) e.getX(), (float) e.getY());
      Vec2 offseted = raw.sub(this.owner.watchedArea.getTopLeftCorner());
      
      this.owner.positionNow = new Vec2(
        (offseted.x() / this.owner.watchedArea.getSize().x()) * this.owner.mapWatchedArea.x(),
        (offseted.y() / this.owner.watchedArea.getSize().y()) * this.owner.mapWatchedArea.y()
      );
    }

    @Override
    public void mouseExited(MouseEvent e) {
      synchronized (this.owner.lock) {
        if (!this.isInteresting(e)) {
          return;
        }
        
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
  }
}
