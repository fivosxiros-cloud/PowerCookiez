package me.foivos.powerCookiez.Cookiez.MyCookie;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jspecify.annotations.NonNull;

public class MyCookieInventory implements InventoryHolder {

    private final Inventory inventory;

    public MyCookieInventory(PowerCookiezMAIN plugin, int size, Component title) {
        this.inventory = plugin.getServer().createInventory(this, size, title);
    }

    @Override
    public @NonNull Inventory getInventory() {
        return this.inventory;
    }
}