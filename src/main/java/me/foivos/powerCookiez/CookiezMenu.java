package me.foivos.powerCookiez;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CookiezMenu implements Listener {

    private final PowerCookiezMAIN plugin;
    private final CookieManager cookieManager;

    public CookiezMenu(PowerCookiezMAIN plugin) {
        this.plugin = plugin;
        this.cookieManager = plugin.getCookieManager();
    }

    public void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.GOLD + "All Cookies");

        int slot = 0;

        for (CookiePower cookie : cookieManager.getAllCookieDisplays()) {

            ItemStack item = new ItemStack(cookie.getDisplayMaterial());
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(ChatColor.AQUA + cookie.getName());

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + cookie.getDescription());
            lore.add("");
            lore.add(ChatColor.YELLOW + "Click to consume this cookie!");

            meta.setLore(lore);
            item.setItemMeta(meta);

            inv.setItem(slot++, item);
        }

        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(ChatColor.GOLD + "All Cookies")) return;

        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String cookieName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        CookiePower cookie = cookieManager.getCookie(cookieName);
        if (cookie == null) return;

        // Cooldown check
        if (cookieManager.isOnCooldown(p)) {
            p.sendMessage(ChatColor.RED + "You must wait before eating another cookie!");
            return;
        }

        // Eat cookie
        cookieManager.setCooldown(p);
        cookieManager.onEat(p, cookieName);

        p.sendMessage(ChatColor.GREEN + "You consumed the " + cookieName + " cookie!");

        p.closeInventory();
    }
}
