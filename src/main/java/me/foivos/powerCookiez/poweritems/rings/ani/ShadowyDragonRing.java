package me.foivos.powerCookiez.poweritems.rings.ani;

import me.foivos.powerCookiez.poweritems.RingManager;
import me.foivos.powerCookiez.poweritems.rings.DragonModelManager;
import me.foivos.powerCookiez.poweritems.rings.RingCategory;
import me.foivos.powerCookiez.poweritems.rings.RingPower;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShadowyDragonRing implements RingPower {

    private final DragonModelManager modelManager = new DragonModelManager();

    @Override
    public String getName() {
        return "ShadowyDragonRing";
    }

    @Override
    public ItemStack getDisplayItem() {
        ItemStack item = new ItemStack(Material.DRAGON_HEAD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§5§lShadowy Dragon Ring");
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public RingCategory getCategory() {
        return RingCategory.ANI;
    }

    @Override
    public void applyPassives(Player p) {
        // no passives for now
    }

    // Ability A = DOUBLE SHIFT + 1 → summon / dismiss dragon
    @Override
    public void abilityA(Player p) {
        if (!modelManager.isTransformed(p)) {
            modelManager.spawnDragon(p);
        } else {
            modelManager.removeDragon(p);
        }
    }

    // Ability B = DOUBLE SHIFT + 2 → dash
    @Override
    public void abilityB(Player p) {
        modelManager.shadowDash(p);
    }

    // Ability C = DOUBLE SHIFT + 3 → breath
    @Override
    public void abilityC(Player p) {
        modelManager.startBreath(p);
    }

    // Ability D = DOUBLE SHIFT + 4 → corruption burst
    @Override
    public void abilityD(Player p) {
        modelManager.corruptionBurst(p);
    }

    public DragonModelManager getModelManager() {
        return modelManager;
    }
}
