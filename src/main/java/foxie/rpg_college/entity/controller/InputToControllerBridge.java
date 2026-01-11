package foxie.rpg_college.entity.controller;

import java.util.Optional;

import foxie.rpg_college.Camera;
import foxie.rpg_college.Orientation;
import foxie.rpg_college.Vec2;
import foxie.rpg_college.entity.Attackable;
import foxie.rpg_college.entity.Entity;
import foxie.rpg_college.entity.LivingEntity;
import foxie.rpg_college.input.Keyboard;
import foxie.rpg_college.input.Keyboard.Button;
import foxie.rpg_college.world.World;
import foxie.rpg_college.input.Mouse;
import foxie.rpg_college.input.State;

// Kelas ini salah satu kelas yang menyambungkan controller
// ke input. Design dari input ke pergerakan seperti ini
//
// 1. Pertama input sampai di kelas ini, lalu mentranslate
//    request menjadi panggilan ke kelas kontroller.
// 2. Request sampai ke kelas kontroller lalu kelas kontroller
//    yang mengimplementasi Controller, mentraslate menjadi 
//    pergerakan yang terjadi :3
// 3. Pergerakan terjadi ^w^
//
// NOTE: Untuk attack dan menyerang seharusnya panggil langsung
// ke interface Attackeable
// 
// Sistem ini mengizinkan berbagai jenis input untuk ini untuk
// keyboard dan mouse. Dengan mudah game dapat membuat kelas lain
// untuk touchcreen input contohnya atau gamepad dan lain-lain
//
// Kelas entity tidak peduli apa yang jadi input asal dia mendapatkan
// panggilan dengan parameter yang tepat >w<
//
// Bahkan dengan implementasi yang benar, bisa disambung AI model
// untuk entahlah AI belajar main game saya :3
public class InputToControllerBridge implements AutoCloseable {
  private Optional<Controller> controller;
  private World currentWorld = null;
  
  private final Camera camera;
  private final ControlEventListener listener;
  
  public InputToControllerBridge(Entity entity, Vec2 viewSize, Vec2 outputSize) {
    if (!entity.canBeControlled()) {
      throw new IllegalArgumentException("Attempt to create InputToControllerBridge with non controllable entity");
    }
    
    this.controller = Optional.of(entity.getController().get());
    this.currentWorld = entity.getWorld();
    this.camera = new Camera(entity.getWorld().getRenderBound(), viewSize, outputSize);
    
    // Supress ini diperlukan karena
    // close resource dilakukan sama yang membuat kelas ini
    // dan ini tidak perlu close
    @SuppressWarnings("resource")
    InputToControllerBridge self = this;
    
    // Membuat kelas listener, untuk mendengar update
    // pada entity yang dikendalikan
    this.listener = new ControlEventListener() {
      @Override
      public void onPositionUpdated() {
        // Entity sudah berpindah tempat, jadi update
        // posisi kamera
        Controller controller = self.controller.get();
        self.camera.setPosition(controller.getEntity().getPos());
      }
      
      @Override
      public void onEntityNoLongerControllable() {
        self.controller = Optional.empty();
      }
      
      @Override
      public void onWorldChange() {
        // Entity sudah berpindah dunia, jadi update
        // dunia kalau tujuannya ada dunia. Kalau
        // tidak ada (hasilnya null) maka tidak melakukan
        // apa-apa
        Controller controller = self.controller.get();
        if (controller.getEntity().getWorld() != null) {
          self.camera.setBound(controller.getEntity().getWorld().getRenderBound());
          self.camera.setPosition(controller.getEntity().getPos());
          self.currentWorld = controller.getEntity().getWorld();
        }
      }
    };
    
    // Lalu tambahkan pendengar untuk mendengar event
    entity.getController().get().addListener(this.listener);
  }
  
  // Kelas Game memanggil ini agar kendali entity baru UwU
  public void setNewEntityToControl(Entity entity) {
    if (!entity.canBeControlled()) {
      throw new IllegalArgumentException("Attempt to controll non-controllable entity");
    }
    
    if (this.controller.isPresent()) {
      this.controller.get().removeListener(this.listener);
    }
    
    // Ambil controller untuk entity dan tambahkan
    // pendengarnya
    Controller newController = entity.getController().get();
    newController.addListener(this.listener);
    this.controller = Optional.of(newController);
    
    // Lalu panggil event-event untuk ganti dunia
    // sama ganti posisi karena entity baru mungkin
    // berbeda dunia sama lokasi
    this.listener.onWorldChange();
    this.listener.onPositionUpdated();
  }
  
  public World getWorld() {
    return this.currentWorld;
  }
  
  public Optional<Entity> getEntity() {
    return this.controller.map(Controller::getEntity);
  }
  
  // Menutup objek ini dan melepaskan resource yang
  // digunakan, karena ingin ditutup maka coba
  // remove pendengar pada kontroller nya. Karena
  // tidak perlu dengar lagi :3
  @Override
  public void close() throws Exception {
    if (this.controller.isPresent()) {
      this.controller.get().removeListener(this.listener);
    }
  }
  
  // Coba mengambil entity yang dikendalikan sebagai
  // LivingEntity kalau ada
  public Optional<LivingEntity> getLivingEntity() {
    Optional<Entity> maybeEntity = this.getEntity();
    if (maybeEntity.isPresent()) {
      Entity entity = maybeEntity.get();
      if (entity instanceof LivingEntity) {
        return Optional.of((LivingEntity) entity);
      }
    }
    
    return Optional.empty();
  }
  
  public Camera getCamera() {
    return this.camera;
  }
  
  // Handle input untuk di kirim ke kontroller
  // seperti maju, mundur, kiri, kanan, etc
  public void handleInput(float deltaTime) {
    if (this.controller.isEmpty()) {
      return;
    }
    
    Controller controller = this.controller.get();
    
    Keyboard keyboard = this.getWorld().getGame().getKeyboard();
    Mouse mouse = this.getWorld().getGame().getMouse();
    
    if (controller.shouldControlDisabled()) {
      // Control is disabled temporarily
      // ----------------------------------
      // Controller meminta kendali untuk di
      // matikan sementara
      return;
    }
    
    if (this.getEntity().isPresent()) {
      // Jika ada entity, jalan kode berikut
      // yang memerlukan adanya entity
      Entity entity = this.getEntity().get();
      
      if (keyboard.getState(Button.Q).isNowPressed()) {
        // Player meminta untuk menyerang, periksa apakah
        // entity dapat menyerang, jika iya panggil method
        // Attackable#attack()
        if (entity instanceof Attackable) {
          Attackable attacker = (Attackable) entity;
          if (attacker.canAttack()) {
            attacker.attack();
          }
        }
      }
      
      if (keyboard.getState(Button.P) == State.Clicked) {
        // Memprint koordinat dimana entity berada
        System.out.println("Coord: " + entity.getPos().x() + ", " + entity.getPos().y());
      }
    }

    Vec2 moveMultiplier = new Vec2(0.0f, 0.0f);
    if (keyboard.getState(Button.W).isNowPressed()) {
      moveMultiplier = moveMultiplier.add(new Vec2(0.0f, -deltaTime));
    }
    
    if (keyboard.getState(Button.A).isNowPressed()) {
      moveMultiplier = moveMultiplier.add(new Vec2(-deltaTime, 0.0f));
    }

    if (keyboard.getState(Button.S).isNowPressed()) {
      moveMultiplier = moveMultiplier.add(new Vec2(0.0f, deltaTime));
    }

    if (keyboard.getState(Button.D).isNowPressed()) {
      moveMultiplier = moveMultiplier.add(new Vec2(deltaTime, 0.0f));
    }
    
    // Pergerakan dari input harus di normalkan dulu
    // sehingga panjang vektor tetap 1.0f, Tanpa
    // baris ini, pergerakan entity yang di kontrol
    // bergerak pada kecepatan ~140% dari kecepatan normal
    // dikarenakan sifat pythogoras. 140% berasal dari
    // sqrt(1^2 + 1^2) = ~1.4142...
    //
    // Ini adalah kesalahan sering dilakukan oleh
    // penulis game yang baru atau kurang mengenali
    //
    // Normalize mengubah multiplier menjadi panjang 1.0f
    moveMultiplier = moveMultiplier.normalize();
    
    if (keyboard.getState(Button.Shift).isNowPressed()) {
      moveMultiplier = moveMultiplier.mul(3.0f);
    }
    
    // Mengapply pergerakan dengan multiplier yang ditetapkan
    controller.applyMovement(moveMultiplier);
    
    if (moveMultiplier.x() > 0.0f) {
      controller.setRotation(Orientation.Right.toDegrees());
    } else if (moveMultiplier.x() < 0.0f) {
      controller.setRotation(Orientation.Left.toDegrees());
    }
    
    if (moveMultiplier.y() < 0.0f) {
      controller.setRotation(Orientation.Up.toDegrees());
    } else if (moveMultiplier.y() > 0.0f) {
      controller.setRotation(Orientation.Down.toDegrees());
    }
    
    if (this.getEntity().isPresent()) {
      // Jika ada entity update entity hadap kemana
      Entity entity = this.getEntity().get();
      if (mouse.getButtonState(Mouse.Button.Right).isNowPressed()) {
        // Pertama ubah posisi entity sekarang di layar
        Vec2 playerScreenCoord = entity.getPos();
        // Lalu dapatkan posisi mouse saat diklik pada layar
        Vec2 lookToScreenCoord = this.camera.translateScreenToWorldCoord(mouse.getMousePosition()).sub(playerScreenCoord);

        // Menggunakan method Vec2#calculateAngle() hitung sudut
        // agar entity dapat limit
        //
        // Untuk melihat kode ini dalam aksi, bisa tekan F3 saat
        // game tidak dipause, lalu lihat garis hijau yang muncul dari
        // entity menunjuk ke arah dimana kursor berada
        controller.setRotation(lookToScreenCoord.calculateAngle());
      }
    }
  }
}
