package me.foivos.powerCookiez.poweritems.rings.ani;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import me.foivos.powerCookiez.poweritems.rings.DragonModelManager;
import me.foivos.powerCookiez.poweritems.rings.RingPower;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import me.foivos.powerCookiez.poweritems.rings.RingCategory;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShadowyDragonRing implements RingPower {

    private final DragonModelManager modelManager;

    public ShadowyDragonRing() {
        this.modelManager = new DragonModelManager();
    }

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
        meta.setLore(java.util.Arrays.asList(
                "§7Transform into a massive",
                "§710-block Shadow Dragon.",
                "",
                "§eAbility A: §5Dragon Form",
                "§eAbility B: §5Shadow Dash",
                "§eAbility C: §5Void Breath",
                "§eAbility D: §5Corruption Burst"
        ));

        meta.getPersistentDataContainer().set(
                new NamespacedKey(PowerCookiezMAIN.getInstance(), "ringName"),
                PersistentDataType.STRING,
                getName()
        );

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void applyPassives(Player player) {
        // Shadow aura passive (optional)
    }

    @Override
    public void abilityA(Player player) {
        if (!modelManager.isTransformed(player)) {
            modelManager.spawnDragon(player);
        } else {
            modelManager.removeDragon(player);
        }
    }

    @Override
    public void abilityB(Player player) {
        modelManager.shadowDash(player);
    }

    @Override
    public void abilityC(Player player) {
        modelManager.startBreath(player);
    }

    @Override
    public void abilityD(Player player) {
        modelManager.corruptionBurst(player);
    }

    public DragonModelManager getModelManager() {
        return modelManager;
    }

}
