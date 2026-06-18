package me.foivos.powerCookiez.poweritems.rings;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.Map;

public class DragonWingAnimationTask extends BukkitRunnable {

    private final Player player;
    private final DragonModelManager manager;
    private double flap = 0;

    public DragonWingAnimationTask(Player player, DragonModelManager manager) {
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

        flap += 0.25;
        double angle = Math.sin(flap) * 0.6;

        ArmorStand wingL = parts.get(DragonPart.WING_LEFT);
        ArmorStand wingR = parts.get(DragonPart.WING_RIGHT);

        wingL.setHeadPose(new EulerAngle(0, angle, 0));
        wingR.setHeadPose(new EulerAngle(0, -angle, 0));
    }
}
