package me.foivos.powerCookiez.poweritems.rings;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface RingPower {

    String getName();

    ItemStack getDisplayItem();

    RingCategory getCategory();

    // 2 passives
    void applyPassives(Player player);

    // 4 abilities
    void abilityA(Player player);
    void abilityB(Player player);
    void abilityC(Player player);
    void abilityD(Player player);
}
