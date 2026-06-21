package me.foivos.powerCookiez.poweritems.rings.ani;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import me.foivos.powerCookiez.poweritems.rings.DragonModelManager;
import me.foivos.powerCookiez.poweritems.rings.RingCategory;
import me.foivos.powerCookiez.poweritems.rings.RingPower;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class ShadowyDragonRing implements RingPower {

    private final DragonModelManager modelManager = new DragonModelManager();

    @Override
    public String getName() {
        return "ShadowyDragonRing";
    }

    @Override
    public RingCategory getCategory() {
        return RingCategory.ANI;
    }

    @Override
    public ItemStack getDisplayItem() {

        ItemStack item = new ItemStack(Material.DRAGON_HEAD);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§5§lShadowy Dragon Ring");
        meta.setLore(Arrays.asList(
                "§7Summon a shadow dragon spirit.",
                "§7Dash, breath and corrupt your enemies.",
                "",
                "§eAbility A: §fSummon / Dismiss Dragon",
                "§eAbility B: §fShadow Dash",
                "§eAbility C: §fVoid Breath",
                "§eAbility D: §fCorruption Burst"
        ));

        // ⭐ REQUIRED FOR /pwring
        meta.getPersistentDataContainer().set(
                new NamespacedKey(PowerCookiezMAIN.getInstance(), "ringName"),
                PersistentDataType.STRING,
                getName()
        );

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void applyPassives(Player p) {
        // No passives for now
    }

    // ============================================================
    // ABILITIES
    // ============================================================

    // Ability A → Summon / Dismiss Dragon
    @Override
    public void abilityA(Player p) {
        if (!modelManager.isTransformed(p)) {
            modelManager.spawnDragon(p);
        } else {
            modelManager.removeDragon(p);
        }
    }

    // Ability B → Shadow Dash
    @Override
    public void abilityB(Player p) {
        modelManager.shadowDash(p);
    }

    // Ability C → Void Breath
    @Override
    public void abilityC(Player p) {
        modelManager.startBreath(p);
    }

    // Ability D → Corruption Burst
    @Override
    public void abilityD(Player p) {
        modelManager.corruptionBurst(p);
    }

    public DragonModelManager getModelManager() {
        return modelManager;
    }
}
