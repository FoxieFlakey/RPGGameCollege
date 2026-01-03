package foxie.rpg_college;

// Camera essentially describes the area in the world which
// is visible to player and also handles clamping at the end of
// the entire world
//
// It also can transform coordinates in world into coordinates
// which actually can be passed to Java AWT's Graphics class for
// rendering things
//
// The center of camera would be middle of output screen
public class Camera {
  private FloatRectangle possiblePosition;
  private Vec2 viewSize;
  private Vec2 outputSize;
  private Vec2 pos = new Vec2(0.0f, 0.0f);

  public Camera(FloatRectangle worldBound, Vec2 viewSize, Vec2 outputSize) {
    if (!worldBound.canFit(viewSize)) {
      // The world cannot fit the view. There would empty space
      // in final render which game don't know whta to do
      throw new IllegalArgumentException("Attempt to create camera with it viewing area larger than the world");
    }
    
    this.viewSize = viewSize;
    this.outputSize = outputSize;
    this.setBound(worldBound);
  }

  public FloatRectangle getVisibleWorld() {
    return new FloatRectangle(
      this.pos.add(this.viewSize.mul(0.5f)),
      this.pos.sub(this.viewSize.mul(0.5f))
    );
  }

  // Given world coordinate translate it to
  // coordinate suitable for AWT graphics class
  // to use on game view canvas
  public Vec2 translateWorldToAWTGraphicsCoord(Vec2 coord) {
    return coord.sub(this.getVisibleWorld().getTopLeftCorner()).mul(this.getScale());
  }
  
  public Vec2 translateAWTGraphicsToWorldCoord(Vec2 coord) {
    return coord.div(this.getScale()).add(this.getVisibleWorld().getTopLeftCorner());
  }
  
  public Vec2 translateScreenToAWTGraphicsCoord(Vec2 coord) {
    return coord.mul(this.getScale());
  }
  
  public Vec2 translateScreenToWorldCoord(Vec2 coord) {
    return this.translateAWTGraphicsToWorldCoord(this.translateScreenToAWTGraphicsCoord(coord));
  }

  public void setPosition(Vec2 newPos) {
    this.pos = this.possiblePosition.clampCoordinate(newPos);
  }

  public Vec2 getPosition() {
    return this.pos;
  }

  public void setBound(FloatRectangle worldBound) {
    Vec2 topLeft = worldBound.getTopLeftCorner();
    Vec2 bottomRight = worldBound.getBottomRightCorner();
    this.possiblePosition = new FloatRectangle(
      topLeft.add(this.viewSize.mul(0.5f)),
      bottomRight.sub(this.viewSize.mul(0.5f))
    );

    // Fix the position
    this.setPosition(this.pos);
  }
  
  public Vec2 getScale() {
    return this.outputSize.div(this.viewSize);
  }
  
  public void setOutputSize(Vec2 size) {
    this.outputSize = size;
  }
}
