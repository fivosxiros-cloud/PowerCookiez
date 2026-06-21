package me.foivos.powerCookiez.poweritems.gui;

import me.foivos.powerCookiez.poweritems.RingManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MyRingMenu {

    private final RingManager ringManager;

    public MyRingMenu(RingManager ringManager) {
        this.ringManager = ringManager;
    }

    public void open(Player player) {

        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Your Ring", NamedTextColor.DARK_AQUA));

        // Background
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta gm = glass.getItemMeta();
        gm.displayName(Component.text(" "));
        glass.setItemMeta(gm);

        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, glass);

        // Current Ring Display
        ItemStack ringItem = ringManager.getPlayerRingItem(player);
        if (ringItem == null || ringItem.getType() == Material.AIR) {
            ringItem = new ItemStack(Material.BARRIER);
            ItemMeta rm = ringItem.getItemMeta();
            rm.displayName(Component.text("No Ring Equipped", NamedTextColor.RED));
            ringItem.setItemMeta(rm);
        }
        inv.setItem(13, ringItem);

        // Toggle Button
        boolean enabled = RingManager.isRingEnabled(player);

        ItemStack toggle = new ItemStack(enabled ? Material.LIME_DYE : Material.RED_DYE);
        ItemMeta tm = toggle.getItemMeta();
        tm.displayName(enabled
                ? Component.text("Ring Enabled", NamedTextColor.GREEN)
                : Component.text("Ring Disabled", NamedTextColor.RED));

        tm.lore(Component.textOfChildren(
                Component.text("Click to " + (enabled ? "disable" : "enable") + " your ring.", NamedTextColor.GRAY))
                .children());

        toggle.setItemMeta(tm);

        inv.setItem(26, toggle);

        player.openInventory(inv);
    }
}
