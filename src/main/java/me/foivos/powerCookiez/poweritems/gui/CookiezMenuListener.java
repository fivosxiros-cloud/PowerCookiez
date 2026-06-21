package me.foivos.powerCookiez.poweritems.gui;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import me.foivos.powerCookiez.CookiePower;
import me.foivos.powerCookiez.poweritems.gui.KeybindTutorialGUI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CookiezMenuListener implements Listener {

    private final PowerCookiezMAIN plugin;

    public CookiezMenuListener(PowerCookiezMAIN plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!e.getView().getTitle().contains("Cookiez")) return;

        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null) return;

        Material type = e.getCurrentItem().getType();

        // ⭐ KEYBINDS BUTTON ⭐
        if (type == Material.BOOK) {
            p.closeInventory();
            KeybindTutorialGUI.open(p);
            return;
        }

        // ⭐ COOKIE CLICK ⭐
        CookiePower cookie = plugin.getCookieManager().getCookieByItem(e.getCurrentItem());
        if (cookie == null) return;

        // Eat cookie
        plugin.getCookieManager().eatCookie(p, cookie.getName());

        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 1f, 1f);
        p.closeInventory();
    }
}
