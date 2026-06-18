package me.foivos.powerCookiez.poweritems.rings;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class WolfFollowerTask extends BukkitRunnable {

    private final Plugin plugin;
    private final WolfModelManager wolfModelManager;
    private final UUID playerId;

    public WolfFollowerTask(Plugin plugin, WolfModelManager wolfModelManager, Player player) {
        this.plugin = plugin;
        this.wolfModelManager = wolfModelManager;
        this.playerId = player.getUniqueId();
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null || !player.isOnline()) {
            cancel();
            return;
        }

        if (!wolfModelManager.isWolf(player)) {
            cancel();
            return;
        }

        Wolf wolf = wolfModelManager.getWolf(player);
        if (wolf == null || wolf.isDead()) {
            cancel();
            return;
        }

        Location loc = player.getLocation().clone();
        loc.setY(loc.getY() - 0.9);

        wolf.teleport(loc);
        wolf.setRotation(loc.getYaw(), loc.getPitch());
    }
}
