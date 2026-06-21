package me.foivos.powerCookiez.poweritems.rings;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WolfModelManager {

    private final Map<UUID, Wolf> wolves = new HashMap<>();
    private final PowerCookiezMAIN plugin = PowerCookiezMAIN.getInstance();

    public boolean isTransformed(Player p) {
        return wolves.containsKey(p.getUniqueId());
    }

    public void spawnWolf(Player p) {

        removeWolf(p);

        Wolf wolf = (Wolf) p.getWorld().spawnEntity(
                p.getLocation().clone().add(0, 1.4, 0),
                EntityType.WOLF
        );

        wolf.setAI(false);
        wolf.setGravity(true); //============================================================
        wolf.setInvulnerable(true);
        wolf.setSilent(true);
        wolf.setCollidable(false);
        wolf.setAdult();
        wolf.setCustomName("§fSpirit Wolf");
        wolf.setCustomNameVisible(true);

        wolves.put(p.getUniqueId(), wolf);

        // FOLLOW TASK
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isTransformed(p)) { cancel(); return; }

                Wolf w = wolves.get(p.getUniqueId());
                if (w == null || w.isDead()) { cancel(); return; }

                Location target = p.getLocation().clone().add(-1.2, 1.2, -1.2);
                Vector dir = target.toVector().subtract(w.getLocation().toVector()).multiply(0.25);

                w.setVelocity(dir);
            }
        }.runTaskTimer(plugin, 1L, 1L);

        // HOVER TASK
        new BukkitRunnable() {
            double t = 0;
            @Override
            public void run() {
                if (!isTransformed(p)) { cancel(); return; }

                Wolf w = wolves.get(p.getUniqueId());
                if (w == null || w.isDead()) { cancel(); return; }

                t += 0.15;
                w.teleport(w.getLocation().clone().add(0, Math.sin(t) * 0.05, 0));
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    public void removeWolf(Player p) {
        Wolf w = wolves.remove(p.getUniqueId());
        if (w != null && !w.isDead()) w.remove();
    }
}
