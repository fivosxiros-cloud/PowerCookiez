package me.foivos.powerCookiez.Cookiez;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CookieEatListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND
                || (e.getAction() != Action.RIGHT_CLICK_AIR
                && e.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return; // Not a right click, exit the method
        }

        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (item.getType() != Material.COOKIE
                || !item.hasItemMeta()) {
            return;
        }

        String cookieName = getCookieName(item);

        if (cookieName == null) return;

        CookieManager cm = PowerCookiezMAIN.getInstance().getCookieManager();

        // Global cookie-eating cooldown (5 sec)
        if (cm.isOnCooldown(p)) {
            p.sendMessage(Component.text("Your cookie power is on cooldown!", NamedTextColor.RED));
            return;
        }
        cm.setCooldown(p);

        // Eat cookie
        cm.onEat(p, cookieName);

        // Remove item
        item.setAmount(item.getAmount() - 1);
    }

    private String getCookieName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        // Preferred source: hidden plugin metadata that is stable and not player-visible.
        String cookieName = meta.getPersistentDataContainer().get(
                new NamespacedKey(PowerCookiezMAIN.getInstance(), "cookieName"),
                PersistentDataType.STRING
        );

        return cookieName != null && !cookieName.isBlank() ? cookieName : null;
    }
}
