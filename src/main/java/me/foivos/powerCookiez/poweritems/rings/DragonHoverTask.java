package me.foivos.powerCookiez.poweritems.rings;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class DragonHoverTask extends BukkitRunnable {

    private final Player player;
    private final DragonModelManager manager;
    private double t = 0;

    public DragonHoverTask(Player player, DragonModelManager manager) {
        this.player = player;
        this.manager = manager;
    }

    @Override
    public void run() {
        if (!manager.isTransformed(player)) {
            cancel();
            return;
        }

        Map<DragonPart, ArmorStand> parts = manager.getParts(player);
        if (parts == null) {
            cancel();
            return;
        }

        t += 0.1;
        double offset = Math.sin(t) * 0.15;

        for (ArmorStand as : parts.values()) {
            Location loc = as.getLocation().clone();
            loc.add(0, offset, 0);
            as.teleport(loc);
        }
    }
}
