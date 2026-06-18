package me.foivos.powerCookiez.poweritems.gui;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import me.foivos.powerCookiez.poweritems.RingManager;
import me.foivos.powerCookiez.poweritems.rings.RingPower;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MyRingGUI {

    public static void open(Player player) {

        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.AQUA + "My Ring");

        RingPower ring = RingManager.getActiveRing(player);

        // === SLOT 13: ACTIVE RING ===
        if (ring != null) {
            inv.setItem(13, ring.getDisplayItem());
        } else {
            ItemStack none = new ItemStack(Material.BARRIER);
            ItemMeta meta = none.getItemMeta();
            meta.setDisplayName("§cNo active ring");
            none.setItemMeta(meta);
            inv.setItem(13, none);
        }

        // === SLOT 11: ENABLE ===
        ItemStack enable = new ItemStack(Material.LIME_DYE);
        ItemMeta em = enable.getItemMeta();
        em.setDisplayName("§aEnable Ring");
        enable.setItemMeta(em);
        inv.setItem(11, enable);

        // === SLOT 15: DISABLE ===
        ItemStack disable = new ItemStack(Material.RED_DYE);
        ItemMeta dm = disable.getItemMeta();
        dm.setDisplayName("§cDisable Ring");
        disable.setItemMeta(dm);
        inv.setItem(15, disable);

        player.openInventory(inv);
    }
}
