package me.foivos.powerCookiez;

import me.foivos.powerCookiez.Cookiez.MyCookie.MyCookieInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        var inventory = e.getInventory();

        if (inventory.getHolder(false) instanceof MyCookieInventory) {
            e.setCancelled(true);
        }
    }
}
