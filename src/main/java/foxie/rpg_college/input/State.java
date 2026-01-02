package foxie.rpg_college.input;

public enum State {
  Clicked,
  Unclicked,
  Hold,
  Unhold;

  public boolean isNowPressed() {
    switch (this) {
      case Clicked:
        return true;
      case Hold:
        return true;
      default:
        return false;
    }
  }

  public boolean isNowReleased() {
    switch (this) {
      case Unclicked:
        return true;
      case Unhold:
        return true;
      default:
        return false;
    }
  }
}
