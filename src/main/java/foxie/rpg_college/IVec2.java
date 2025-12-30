package foxie.rpg_college;

public record IVec2(int x, int y) {
  public IVec2 add(IVec2 rhs) {
    return new IVec2(this.x + rhs.x, this.y + rhs.y);
  }

  public IVec2 mul(int factor) {
    return new IVec2(
      this.x * factor,
      this.y * factor
    );
  }

  public IVec2 div(int factor) {
    return new IVec2(
      this.x / factor,
      this.y / factor
    );
  }

  public IVec2 sub(IVec2 rhs) {
    return new IVec2(this.x - rhs.x, this.y - rhs.y);
  }
}
