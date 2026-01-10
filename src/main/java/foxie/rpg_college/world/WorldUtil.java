package foxie.rpg_college.world;

import java.awt.Graphics2D;
import java.awt.Image;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Vec2;

public class WorldUtil {
  // Fungsi pembantu untuk dunia merender background
  public static void renderBackground(World world, Graphics2D g, Image backgroundImage) {
    // pertama kita ambil ukuran gambarnya
    Vec2 backgroundImageSize = new Vec2(backgroundImage.getWidth(null), backgroundImage.getHeight(null));
    
    // Setelah itu menghitung bagian dimana yang terlihat di output
    FloatRectangle visible = world.getGame().getCamera().getVisibleWorld();
    
    // Setelah itu mengoffset koordinat awalnya dengan ukuran gambar karena
    // 0, 0 di dunia sebenarnya di tengah bukan di kiri atas
    Vec2 coord = visible.getTopLeftCorner().add(backgroundImageSize.mul(0.5f));
    
    // Setelah itu hitung ukuran yang terlihat lalu
    // menskalakannya sehingga memenuhi outputnya
    Vec2 visibleSize = visible.getSize();
    Vec2 visibleSizeOutput = world.getGame().getCamera().scaleSize(visible.getSize());

    // Image might be not ready, but lets ignore
    // Setelah itu gambar backgroundnya
    g.drawImage(
      backgroundImage,
      // Dest coords
      0, 0,
      (int) visibleSizeOutput.x(), (int) visibleSizeOutput.y(),
      // Source coords
      (int) coord.x(), (int) coord.y(),
      (int) (visibleSize.x() + coord.x()), (int) (visibleSize.y() + coord.y()),
      null
    );
  }
}
