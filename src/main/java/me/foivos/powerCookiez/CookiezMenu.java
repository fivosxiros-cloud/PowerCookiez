package me.foivos.powerCookiez;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import me.foivos.powerCookiez.CookieManager;
import me.foivos.powerCookiez.poweritems.gui.KeybindTutorialGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class CookiezMenu implements Listener {

    private final PowerCookiezMAIN plugin;
    private final CookieManager cookieManager;

    public CookiezMenu(PowerCookiezMAIN plugin) {
        this.plugin = plugin;
        this.cookieManager = plugin.getCookieManager();
    }

    public void open(Player p) {

        Inventory gui = Bukkit.createInventory(null, 54, "§6§lCookiez");

        // Background
        ItemStack glass = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta gm = glass.getItemMeta();
        gm.setDisplayName(" ");
        glass.setItemMeta(gm);

        for (int i = 0; i < 54; i++) gui.setItem(i, glass);

        // Title item
        ItemStack title = new ItemStack(Material.COOKIE);
        ItemMeta tm = title.getItemMeta();
        tm.setDisplayName("§6§lYour Cookiez");
        tm.setLore(Arrays.asList(
                "§7Click a cookie to eat it.",
                "§7Each cookie has 6 gears.",
                "",
                "§eUse DOUBLE SHIFT + KEY to activate gears."
        ));
        title.setItemMeta(tm);
        gui.setItem(4, title);

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
        gui.setItem(49, keybinds);

        // Place cookies
        int slot = 10;
        for (var cookie : cookieManager.getAllCookies()) {

            ItemStack item = cookie.getDisplayItem();
            ItemMeta meta = item.getItemMeta();

            meta.setLore(Arrays.asList(
                    "§7" + cookie.getDescription(),
                    "",
                    "§eClick to eat this cookie"
            ));

            item.setItemMeta(meta);

            gui.setItem(slot, item);

            slot++;
            if (slot == 17) slot = 19;
            if (slot == 26) slot = 28;
        }

        p.openInventory(gui);
    }
}
