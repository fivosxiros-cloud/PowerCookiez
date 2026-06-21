package me.foivos.powerCookiez.Cookiez.AllCookiez;

import me.foivos.powerCookiez.Cookiez.CookieManager;
import me.foivos.powerCookiez.Cookiez.CookiePower;
import me.foivos.powerCookiez.PowerCookiezMAIN;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CookiezMenu implements Listener {

    private final PowerCookiezMAIN plugin;
    private final CookieManager cookieManager;

    public CookiezMenu(PowerCookiezMAIN plugin) {
        this.plugin = plugin;
        this.cookieManager = plugin.getCookieManager();
    }

    public void open(Player p) {

        Inventory inv = new CookiezInventory(plugin, 54, Component.text("All Cookies", NamedTextColor.GOLD))
                .getInventory();

        int slot = 0;

        for (CookiePower cookie : cookieManager.getAllCookieDisplays()) {

            ItemStack item = new ItemStack(cookie.getDisplayMaterial());
            ItemMeta meta = item.getItemMeta();

            meta.displayName(Component.text(cookie.getName(), NamedTextColor.AQUA));

            meta.lore(Component.textOfChildren(
                    Component.text(cookie.getDescription(), NamedTextColor.GRAY),
                    Component.text(""),
                    Component.text("Click to consume this cookie!", NamedTextColor.YELLOW)
            ).children());

            // Store stable id for click handling; display name is only for UI.
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "cookieName"),
                    PersistentDataType.STRING,
                    cookie.getName()
            );
            item.setItemMeta(meta);

            inv.setItem(slot++, item);
        }

        p.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        var inventory = e.getClickedInventory();
        if (inventory == null || !(inventory.getHolder(false) instanceof CookiezInventory)) return;

        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String cookieName = clicked.getItemMeta().getPersistentDataContainer().get(
                new NamespacedKey(plugin, "cookieName"),
                PersistentDataType.STRING
        );

        if (cookieName == null || cookieName.isBlank()) {
            return;
        }

        // Cooldown check
        if (cookieManager.isOnCooldown(p)) {
            p.sendMessage(Component.text("You must wait before eating another cookie!", NamedTextColor.RED));
            return;
        }

        // Eat cookie
        cookieManager.setCooldown(p);
        cookieManager.onEat(p, cookieName);

        p.sendMessage(Component.text("You consumed the " + cookieName + " cookie!", NamedTextColor.GREEN));

        p.closeInventory();
    }
}
