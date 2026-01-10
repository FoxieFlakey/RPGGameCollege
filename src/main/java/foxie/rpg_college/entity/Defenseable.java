package foxie.rpg_college.entity;

import foxie.rpg_college.entity.damage.DamageSource;

// Interface ini adalah untuk entity/karakter
// yang dapat bertahan dengan cara apapun
public interface Defenseable {
  // Mereturn true jika dapat mengblok
  // atau bertahan. Jika false, tidak
  // dapat. Contohnya return false karena
  // mungkin mana terlalu rendah atau ada
  // cooldown/limit waktu
  boolean canDefense();
  
  // Karakter/entity bertahan terhadap
  // sebuah sumber damage method ini
  // bebas mengeditnya seperti beberapa
  // karakter menerima damage lebih rendah
  // atau 100% memblok
  //
  // Setelah ini damagenya akan diapply.
  // Method ini hanya dipanggil jika
  // canDefense returnnya true
  void defend(DamageSource source);
}
