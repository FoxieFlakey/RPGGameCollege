package foxie.rpg_college.tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Optional;

import foxie.rpg_college.FloatRectangle;
import foxie.rpg_college.Game;
import foxie.rpg_college.IVec2;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.world.World;

public class PortalTile extends Tile {
  private static final Object EXTRA_DATA_KEY = new Object();
  private static final float COOLDOWN_TIME = 2.0f;
  private static final float TIME_BEFORE_TELEPORT = 1.0f;
  
  private final String targetWorldId;
  
  private static class PortalData {
    public final HashMap<World, Vec2> savedPositions = new HashMap<>();
    public float lastStepTime = 0.0f;
    public float timeToTeleport = 0.0f;
    public boolean isWaitingToTeleport = false;
  };
  
  public PortalTile(Game game, String targetWorld) {
    super(game);
    this.targetWorldId = targetWorld;
  }
  
  @Override
  public void steppedBy(Entity e, IVec2 coord) {
    float currentTime = e.getWorld().getGame().getGameTime();

    // Tiap entity memiliki kemampuan untuk menyimpan data tambahkan
    // yang disimpan oleh kelas-kelas lain. Kunci itu harus unique untuk
    // tiap kelas, untuk PortalTile, menggunakan object baru yang kosong
    // (literally its only new Object() -w-, on Java every object is unique)
    //
    // Setelah itu langsung di cast saja ke PortalData, karena hanya kelas ini
    // yang seharusnya menggunakan kuncinya. Kalau error maka itu memang
    // error karena seharusnya tidak terjadi dan itu berarti kode lain mengotak-
    // atik ;w; data yang bukan miliknya
    //
    // getExtraDataOrInsert memungkinkan pemanggil mengambil extra data nya atau
    // membuat baru lalu insert baru dalam satu langkah. Dalam progrramming fungsional
    // ini kurang lebih seperti lazily initialized data. Data hanya dibuat kalau tidak
    // ada dibanding dibuat duluan sebelum dipakai :3
    //
    // disini PortalData::new dapat digunakan untuk mendapatkan konstructor dari
    // objetknya sebagai object (function as object), sehingga method nya tau membuat
    // data kalau tidak ada.
    PortalData data = (PortalData) e.getExtraDataOrInsert(PortalTile.EXTRA_DATA_KEY, PortalData::new);
    if (currentTime - data.lastStepTime < PortalTile.COOLDOWN_TIME) {
      // In cooldown
      // -----------------------------------
      // Karena method steppedBy dipanggil
      // tiap kali walaupun sudah dipangil
      // jika entity diam ditempat. Maka diperlukan
      // cooldown agar entity tidak langsung teleport bolak
      // -balik antara dua portal -w-
      return;
    }
    
    if (!data.isWaitingToTeleport) {
      // Entity baru pertama injak
      // lalu mulai hitung mundur untuk
      // menunggu
      data.isWaitingToTeleport = true;
      data.timeToTeleport = currentTime + PortalTile.TIME_BEFORE_TELEPORT;
    } else if (data.isWaitingToTeleport && currentTime >= data.timeToTeleport) {
      // Setelah waktu habis, dan entity masih menginjak lakukan teleport
      // lalu reset beberapa variabel

      // Simpan lokasi entity saat ia teleport sehingga teleport balik
      // dapat ke posisi yang sama dengan masuk
      data.savedPositions.put(e.getWorld(), e.getPos());
      data.isWaitingToTeleport = false;
      data.lastStepTime = currentTime;
      
      // Mencari dunia target
      World targetWorld = this.getGame().getWorldManager().getWorld(this.targetWorldId).get();

      // Lalu mengambil koordinat tujuan
      // kelas Optional memiliki method orElseGet agar
      // kalau tidak ada lokasi yang disimpan kita dapat
      // mengasumsi teleport ke world spawn
      Vec2 targetTeleportCoord = Optional.ofNullable(data.savedPositions.get(targetWorld)).orElseGet(() -> targetWorld.getWorldSpawnPoint());
      
      // Setelah teleport entity ke dunia target
      targetWorld.addEntity(e);

      // Lalu set posisinya
      e.setPos(targetTeleportCoord);
    }
  }

  @Override
  public boolean isCollisionEnabled() {
    // Portal tidak bisa ditabrak karena
    // harus berdiri didalamnnya
    return false;
  }

  @Override
  public boolean canBeTicked() {
    // Portal tidak memeriksa apa-apa
    // jadi false
    return false;
  }

  @Override
  public void render(Graphics2D g, float deltaTime, IVec2 position) {
    // sama saja dengan WallTile#render tetapi warna hijau untuk
    // membedakan portal sama dinding
    FloatRectangle renderBox = TileHelper.calculateRenderBox(this, position);

    int x = (int) renderBox.getTopLeftCorner().x();
    int y = (int) renderBox.getTopLeftCorner().y();
    int width = (int) renderBox.getSize().x();
    int height = (int) renderBox.getSize().y();

    g.setColor(Color.GREEN);
    g.fillRect(x, y, width, height);
  }

  @Override
  public void tick(float deltaTime, IVec2 position) {
    // Portal tidak memeriksa apa-apa
    // jadi kosong
  }
}
