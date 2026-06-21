package me.foivos.powerCookiez.poweritems.gui;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import me.foivos.powerCookiez.poweritems.RingManager;
import me.foivos.powerCookiez.poweritems.rings.RingPower;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class KeybindTutorialGUI {

    public static void open(Player p) {

        Inventory gui = Bukkit.createInventory(null, 54, "§b§lKeybind Tutorial");

        // ============================================================
        // DECORATION
        // ============================================================
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta gm = glass.getItemMeta();
        gm.setDisplayName(" ");
        glass.setItemMeta(gm);

        for (int i = 0; i < 54; i++) gui.setItem(i, glass);

        // ============================================================
        // TITLE ITEMS
        // ============================================================
        gui.setItem(4, createItem(Material.BOOK, "§b§lKEYBIND TUTORIAL",
                "§7Δες όλα τα κουμπιά για Cookiez & Rings"));

        gui.setItem(20, createItem(Material.COOKIE, "§a§lCOOKIEZ KEYBINDS",
                "§7Όλα τα Gears ενεργοποιούνται με:",
                "§fDOUBLE SHIFT + KEY"));

        gui.setItem(24, createItem(Material.DRAGON_HEAD, "§d§lRINGS KEYBINDS",
                "§7Όλες οι abilities ενεργοποιούνται με:",
                "§fDOUBLE SHIFT + NUMBER"));

        // ============================================================
        // COOKIEZ KEYBINDS
        // ============================================================
        gui.setItem(29, createItem(Material.PAPER, "§aGear 1",
                "§fDOUBLE SHIFT + §eQ"));

        gui.setItem(30, createItem(Material.PAPER, "§aGear 2",
                "§fDOUBLE SHIFT + §eW"));

        gui.setItem(31, createItem(Material.PAPER, "§aGear 3",
                "§fDOUBLE SHIFT + §eA"));

        gui.setItem(32, createItem(Material.PAPER, "§aGear 4",
                "§fDOUBLE SHIFT + §eS"));

        gui.setItem(33, createItem(Material.PAPER, "§aGear 5",
                "§fDOUBLE SHIFT + §eZ"));

        gui.setItem(34, createItem(Material.PAPER, "§aGear 6",
                "§fDOUBLE SHIFT + §eX"));

        // ============================================================
        // RINGS KEYBINDS
        // ============================================================
        gui.setItem(38, createItem(Material.ENDER_EYE, "§dAbility A",
                "§fDOUBLE SHIFT + §e1"));

        gui.setItem(39, createItem(Material.ENDER_PEARL, "§dAbility B",
                "§fDOUBLE SHIFT + §e2"));

        gui.setItem(40, createItem(Material.BLAZE_POWDER, "§dAbility C",
                "§fDOUBLE SHIFT + §e3"));

        gui.setItem(41, createItem(Material.NETHER_STAR, "§dAbility D",
                "§fDOUBLE SHIFT + §e4"));

        // ============================================================
        // OPEN GUI
        // ============================================================
        p.openInventory(gui);
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.4f);
    }

    private static ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}
