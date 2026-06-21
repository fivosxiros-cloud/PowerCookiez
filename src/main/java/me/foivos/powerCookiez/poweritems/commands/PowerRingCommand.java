package me.foivos.powerCookiez.poweritems.commands;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import me.foivos.powerCookiez.poweritems.RingManager;
import me.foivos.powerCookiez.poweritems.rings.RingPower;

import org.bukkit.NamespacedKey;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class PowerRingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NotNull [] args) {

        if (!(sender instanceof Player p)) return true;

        ItemStack item = p.getInventory().getItemInMainHand();
        if (!item.hasItemMeta()) {
            p.sendMessage("§cHold a ring in your hand!");
            return true;
        }

        String ringName = item.getItemMeta().getPersistentDataContainer().get(
                new NamespacedKey(PowerCookiezMAIN.getInstance(), "ringName"),
                PersistentDataType.STRING
        );

        if (ringName == null) {
            p.sendMessage("§cThis is not a magical ring!");
            return true;
        }

        // Already active?
        RingPower active = RingManager.getActiveRing(p);
        if (active != null && active.getName().equals(ringName)) {
            p.sendMessage("§eYour ring is already active!");
            return true;
        }

        // Find the ring in registry
        for (RingPower r : RingManager.getRegisteredRings()) {
            if (r.getName().equals(ringName)) {

                // SET ACTIVE RING
                RingManager.setPlayerRing(p, r);

                p.sendMessage("§aYou activated the ring: §e" + r.getName());
                return true;
            }
        }

        p.sendMessage("§cRing not registered!");
        return true;
    }
}
