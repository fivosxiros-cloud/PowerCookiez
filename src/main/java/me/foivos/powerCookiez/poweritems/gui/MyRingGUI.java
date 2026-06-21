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

import java.util.Arrays;

public class MyRingGUI {

    public static void open(Player p) {

        Inventory gui = Bukkit.createInventory(null, 27, "§d§lMy Ring");

        // Background
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta gm = glass.getItemMeta();
        gm.setDisplayName(" ");
        glass.setItemMeta(gm);

        for (int i = 0; i < 27; i++) gui.setItem(i, glass);

        // Active ring
        RingPower ring = RingManager.getActiveRing(p);

        ItemStack ringItem;
        if (ring != null) {
            ringItem = ring.getDisplayItem();
        } else {
            ringItem = new ItemStack(Material.BARRIER);
            ItemMeta meta = ringItem.getItemMeta();
            meta.setDisplayName("§cNo active ring");
            meta.setLore(Arrays.asList("§7Use /pwring while holding a ring"));
            ringItem.setItemMeta(meta);
        }

        gui.setItem(13, ringItem);

        // Enable / Disable button
        boolean enabled = RingManager.isRingEnabled(p);

        ItemStack toggle = new ItemStack(enabled ? Material.LIME_DYE : Material.RED_DYE);
        ItemMeta tm = toggle.getItemMeta();
        tm.setDisplayName(enabled ? "§aRing Enabled" : "§cRing Disabled");
        tm.setLore(Arrays.asList(
                "§7Click to toggle your ring",
                enabled ? "§cDisable ring" : "§aEnable ring"
        ));
        toggle.setItemMeta(tm);

        gui.setItem(11, toggle);

        // ⭐ KEYBINDS BUTTON ⭐
        ItemStack keybinds = new ItemStack(Material.BOOK);
        ItemMeta km = keybinds.getItemMeta();
        km.setDisplayName("§b§l📘 Keybinds Tutorial");
        km.setLore(Arrays.asList(
                "§7View all keybinds for:",
                "§f• Cookiez Gears",
                "§f• Ring Abilities",
                "",
                "§eClick to open"
        ));
        keybinds.setItemMeta(km);

        gui.setItem(15, keybinds);

        p.openInventory(gui);
    }
}
