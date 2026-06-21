package me.foivos.powerCookiez.Cookiez.MyCookie;

import me.foivos.powerCookiez.Cookiez.AllCookiez.CookiezInventory;
import me.foivos.powerCookiez.Cookiez.CookieManager;
import me.foivos.powerCookiez.PowerCookiezMAIN;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MyCookieMenu implements Listener {

    private final PowerCookiezMAIN plugin;
    private final CookieManager cookieManager;

    public MyCookieMenu(PowerCookiezMAIN plugin) {
        this.plugin = plugin;
        this.cookieManager = plugin.getCookieManager();
    }

    public void open(Player player) {

        Inventory inv = new MyCookieInventory(plugin,
                27,
                Component.text("Your Cookie", NamedTextColor.AQUA)).getInventory();

        // ===========================
        // TOGGLE BUTTON (slot 26)
        // ===========================
        ItemStack toggle = new ItemStack(
                cookieManager.isCookieEnabled(player) ? Material.BARRIER : Material.LIME_DYE
        );

        ItemMeta toggleMeta = toggle.getItemMeta();
        toggleMeta.setDisplayName(cookieManager.isCookieEnabled(player)
                ? ChatColor.RED + "Disable Cookie"
                : ChatColor.GREEN + "Enable Cookie"
        );
        toggle.setItemMeta(toggleMeta);

        inv.setItem(26, toggle);

        // ===========================
        // COOKIE INFO (slot 13)
        // ===========================
        String cookieName = cookieManager.getLastCookieEaten(player);
        if (cookieName == null) cookieName = "None";

        int gear = cookieManager.getGearLevel(player);

        ItemStack cookieItem = new ItemStack(Material.COOKIE);
        ItemMeta cookieMeta = cookieItem.getItemMeta();
        cookieMeta.setDisplayName(ChatColor.GOLD + "Cookie: " + ChatColor.AQUA + cookieName);
        cookieMeta.setLore(List.of(
                ChatColor.YELLOW + "Gear Level: " + ChatColor.AQUA + gear,
                "",
                ChatColor.GRAY + "Gear 1: Frost Dash " + (gear >= 1 ? ChatColor.GREEN + "UNLOCKED" : ChatColor.RED + "LOCKED"),
                ChatColor.GRAY + "Gear 2: Ice Walls " + (gear >= 2 ? ChatColor.GREEN + "UNLOCKED" : ChatColor.RED + "LOCKED"),
                ChatColor.GRAY + "Gear 3: Absolute Zero " + (gear >= 3 ? ChatColor.GREEN + "UNLOCKED" : ChatColor.RED + "LOCKED")
        ));
        cookieItem.setItemMeta(cookieMeta);

        inv.setItem(13, cookieItem);

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        var inventory = e.getClickedInventory();
        if (inventory == null || !(inventory.getHolder(false) instanceof MyCookieInventory)) return;

        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();

        // Toggle button
        if (e.getSlot() == 26) {
            boolean enabled = cookieManager.isCookieEnabled(p);
            cookieManager.setCookieEnabled(p, !enabled);

            p.sendMessage(enabled
                    ? Component.text("Cookie disabled!", NamedTextColor.RED)
                    : Component.text("Cookie enabled!", NamedTextColor.GREEN)
            );

            open(p); // refresh GUI
        }
    }
}
