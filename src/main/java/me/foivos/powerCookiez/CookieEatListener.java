package me.foivos.powerCookiez;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class CookieEatListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (!e.getAction().toString().contains("RIGHT_CLICK")) return;

        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (item == null || item.getType() != Material.COOKIE) return;
        if (!item.hasItemMeta()) return;

        String cookieName = item.getItemMeta().getPersistentDataContainer().get(
                new NamespacedKey(PowerCookiezMAIN.getInstance(), "cookieName"),
                PersistentDataType.STRING
        );

        if (cookieName == null) return;

        CookieManager cm = PowerCookiezMAIN.getInstance().getCookieManager();

        // Global cookie-eating cooldown (5 sec)
        if (cm.isOnCooldown(p)) {
            p.sendMessage(ChatColor.RED + "Your cookie power is on cooldown!");
            return;
        }
        cm.setCooldown(p);

        // Eat cookie
        cm.onEat(p, cookieName);

        // Remove item
        item.setAmount(item.getAmount() - 1);
    }
}
