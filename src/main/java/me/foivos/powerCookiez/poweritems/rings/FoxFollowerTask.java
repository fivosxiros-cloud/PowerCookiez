package me.foivos.powerCookiez.poweritems.rings;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class FoxFollowerTask extends BukkitRunnable {

    private final Plugin plugin;
    private final FoxModelManager foxModelManager;
    private final UUID playerId;

    public FoxFollowerTask(Plugin plugin, FoxModelManager foxModelManager, Player player) {
        this.plugin = plugin;
        this.foxModelManager = foxModelManager;
        this.playerId = player.getUniqueId();
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null || !player.isOnline()) {
            cancel();
            return;
        }

        if (!foxModelManager.isFoxTransformed(player)) {
            cancel();
            return;
        }

        Fox fox = foxModelManager.getFox(player);
        if (fox == null || fox.isDead()) {
            cancel();
            return;
        }

        Location playerLoc = player.getLocation().clone();
        // Λίγο offset για να φαίνεται σωστά
        playerLoc.setY(playerLoc.getY() - 0.9);

        fox.teleport(playerLoc);
        fox.setRotation(playerLoc.getYaw(), playerLoc.getPitch());
    }
}
