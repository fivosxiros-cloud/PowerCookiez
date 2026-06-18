package me.foivos.powerCookiez.poweritems.rings;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class DragonOrbitTask extends BukkitRunnable {

    private final Player player;
    private final DragonModelManager manager;
    private double angle = 0;

    public DragonOrbitTask(Player player, DragonModelManager manager) {
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

        angle += Math.toRadians(1.5); // slow orbit

        Location base = player.getLocation().clone().add(0, 1.5, 0);

        double radius = 1.2;

        // HEAD orbit
        ArmorStand head = parts.get(DragonPart.HEAD);
        head.teleport(base.clone().add(
                Math.cos(angle) * radius,
                0.8,
                Math.sin(angle) * radius - 1.2
        ));

        // CHEST
        ArmorStand chest = parts.get(DragonPart.CHEST);
        chest.teleport(base.clone().add(
                Math.cos(angle + 0.1) * radius,
                0.3,
                Math.sin(angle + 0.1) * radius - 0.5
        ));

        // BODY
        ArmorStand body = parts.get(DragonPart.BODY);
        body.teleport(base.clone().add(
                Math.cos(angle + 0.2) * radius,
                0.1,
                Math.sin(angle + 0.2) * radius + 0.8
        ));

        // TAIL
        ArmorStand tail = parts.get(DragonPart.TAIL);
        tail.teleport(base.clone().add(
                Math.cos(angle + 0.3) * radius,
                -0.1,
                Math.sin(angle + 0.3) * radius + 1.8
        ));

        // BREATH EMITTER
        ArmorStand emitter = parts.get(DragonPart.BREATH_EMITTER);
        emitter.teleport(base.clone().add(
                Math.cos(angle) * radius,
                0.6,
                Math.sin(angle) * radius - 1.6
        ));
    }
}
