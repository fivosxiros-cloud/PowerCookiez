package me.foivos.powerCookiez.poweritems.rings;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class DragonFollowerTask extends BukkitRunnable {

    private final Player player;
    private final DragonModelManager manager;

    public DragonFollowerTask(Player player, DragonModelManager manager) {
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

        Location base = player.getLocation().clone().add(0, 2.2, 0);

        // CORE follows player
        ArmorStand core = parts.get(DragonPart.CORE);
        core.teleport(base);
    }
}
