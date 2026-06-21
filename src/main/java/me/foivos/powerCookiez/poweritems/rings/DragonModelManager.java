package me.foivos.powerCookiez.poweritems.rings;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import me.foivos.powerCookiez.poweritems.RingManager;
import org.bukkit.*;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DragonModelManager {

    private final Map<UUID, EnderDragon> dragons = new HashMap<>();
    private final PowerCookiezMAIN plugin = PowerCookiezMAIN.getInstance();

    public boolean isTransformed(Player p) {
        return dragons.containsKey(p.getUniqueId());
    }

    public void spawnDragon(Player p) {

        removeDragon(p);

        p.sendMessage("§5§lYou summon your Shadow Dragon!");
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.6f);

        EnderDragon dragon = (EnderDragon) p.getWorld().spawnEntity(
                p.getLocation().clone().add(0, 5, 0),
                EntityType.ENDER_DRAGON
        );

        dragon.setAI(false);
        dragon.setInvulnerable(true);
        dragon.setRemoveWhenFarAway(false);
        dragon.setCustomName("§5Shadow Dragon");
        dragon.setCustomNameVisible(true);

        dragons.put(p.getUniqueId(), dragon);

        // FOLLOW TASK
        int id1 = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isTransformed(p)) {
                    cancel();
                    return;
                }

                EnderDragon d = dragons.get(p.getUniqueId());
                if (d == null || d.isDead()) {
                    cancel();
                    return;
                }

                Location target = p.getLocation().clone().add(0, 6, 0);
                Vector dir = target.toVector().subtract(d.getLocation().toVector()).multiply(0.1);

                d.setVelocity(dir);
            }
        }.runTaskTimer(plugin, 1L, 1L).getTaskId();
        RingManager.registerTask(p, id1);

        // HOVER TASK
        int id2 = new BukkitRunnable() {
            double t = 0;
            @Override
            public void run() {
                if (!isTransformed(p)) {
                    cancel();
                    return;
                }

                EnderDragon d = dragons.get(p.getUniqueId());
                if (d == null || d.isDead()) {
                    cancel();
                    return;
                }

                t += 0.1;
                d.teleport(d.getLocation().clone().add(0, Math.sin(t) * 0.3, 0));
            }
        }.runTaskTimer(plugin, 1L, 1L).getTaskId();
        RingManager.registerTask(p, id2);
    }

    public void startBreath(Player p) {
        if (!isTransformed(p)) return;

        EnderDragon d = dragons.get(p.getUniqueId());
        if (d == null || d.isDead()) return;

        int id = new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!isTransformed(p)) {
                    cancel();
                    return;
                }

                EnderDragon dragon = dragons.get(p.getUniqueId());
                if (dragon == null || dragon.isDead()) {
                    cancel();
                    return;
                }

                ticks++;
                if (ticks > 40) {
                    cancel();
                    return;
                }

                Location loc = dragon.getLocation().clone().add(0, -2, -4);
                Vector dir = p.getLocation().getDirection().normalize();

                loc.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 40, 0.4, 0.4, 0.4, 0.01);
                loc.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_SHOOT, 1f, 0.6f);

                for (Player target : loc.getWorld().getPlayers()) {
                    if (target != p && target.getLocation().distance(loc.add(dir)) < 3) {
                        target.damage(2.0, p);
                    }
                }
            }
        }.runTaskTimer(plugin, 1L, 1L).getTaskId();

        RingManager.registerTask(p, id);
    }

    public void shadowDash(Player p) {
        if (!isTransformed(p)) return;

        Vector dir = p.getLocation().getDirection().normalize().multiply(2.5);
        p.setVelocity(dir);
        p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 80, 1, 1, 1, 0.2);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 1.2f);
    }

    public void corruptionBurst(Player p) {
        if (!isTransformed(p)) return;

        Location loc = p.getLocation();
        loc.getWorld().spawnParticle(Particle.REVERSE_PORTAL, loc, 60, 1.5, 1.5, 1.5, 0.1);
        loc.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.5f);

        for (Player target : loc.getWorld().getPlayers()) {
            if (target != p && target.getLocation().distance(loc) < 6) {
                target.damage(4.0, p);
            }
        }
    }

    public void removeDragon(Player p) {
        EnderDragon d = dragons.remove(p.getUniqueId());
        if (d != null && !d.isDead()) {
            d.remove();
        }

        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1f, 0.8f);
    }
}
