package me.foivos.powerCookiez;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface CookiePower {

    // BASIC INFO
    String getName();
    String getDescription();
    Material getDisplayMaterial();
    ItemStack getDisplayItem();

    // BASE ACTIVATION
    void activate(Player player);

    // GEARS
    void activateGear1(Player player);
    void activateGear2(Player player);
    void activateGear3(Player player);

    default void activateGear4(Player player) {}
    default void activateGear5(Player player) {}
    default void activateGear6(Player player) {}

    int getMaxGears();
    String getGearDescription(int gear);
    long getGearCooldownMs(int gear);
}
