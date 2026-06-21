package me.foivos.powerCookiez.poweritems.rings;

import me.foivos.powerCookiez.poweritems.RingManager;
import me.foivos.powerCookiez.poweritems.gui.KeybindTutorialGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MyRingMenuListener implements Listener {

    private final RingManager ringManager;

    public MyRingMenuListener(RingManager ringManager) {
        this.ringManager = ringManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!e.getView().getTitle().contains("My Ring")) return;

        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        Material type = e.getCurrentItem() != null ? e.getCurrentItem().getType() : null;

        if (type == Material.LIME_DYE || type == Material.RED_DYE) {
            ringManager.toggleRing(p);
            p.sendMessage(type == Material.LIME_DYE ?
                    ChatColor.RED + "Ring disabled!" :
                    ChatColor.GREEN + "Ring enabled!");
            p.closeInventory();
            return;
        }

        // ⭐ KEYBINDS BUTTON ⭐
        if (type == Material.BOOK) {
            p.closeInventory();
            KeybindTutorialGUI.open(p);
        }
    }
}
