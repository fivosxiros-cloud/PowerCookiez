package me.foivos.powerCookiez.Cookiez.AllCookiez;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jspecify.annotations.NonNull;

public class CookiezInventory implements InventoryHolder {

    private final Inventory inventory;

    public CookiezInventory(PowerCookiezMAIN plugin, int size, Component title) {
        this.inventory = plugin.getServer().createInventory(this, size, title);
    }

    @Override
    public @NonNull Inventory getInventory() {
        return this.inventory;
    }
}