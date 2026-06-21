package me.foivos.powerCookiez.poweritems.rings;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import org.bukkit.*;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class DragonModelManager {

    private final Map<UUID, EnderDragon> dragons = new HashMap<>();
    private final PowerCookiezMAIN plugin = PowerCookiezMAIN.getInstance();

    // ============================================================
    // CHECK IF PLAYER HAS DRAGON
    // ============================================================
    public boolean isTransformed(Player p) {
        return dragons.containsKey(p.getUniqueId());
    }

    // ============================================================
    // SPAWN DRAGON
    // ============================================================
    public void spawnDragon(Player p) {

        removeDragon(p);

        Location spawnLoc = p.getLocation().clone().add(0, 3, 0);

        EnderDragon dragon = (EnderDragon) p.getWorld().spawnEntity(spawnLoc, EntityType.ENDER_DRAGON);

        dragon.setAI(false);
        dragon.setGravity(false);
        dragon.setInvulnerable(true);
        dragon.setSilent(true);
        dragon.setCollidable(false);
        dragon.setCustomName("§5Shadow Dragon");
        dragon.setCustomNameVisible(true);

        dragons.put(p.getUniqueId(), dragon);

        startFollowTask(p);
        startHoverTask(p);
    }

    // ============================================================
    // FOLLOW TASK (smooth orbit follow)
    // ============================================================
    private void startFollowTask(Player p) {

        new BukkitRunnable() {
            double angle = 0;

            @Override
            public void run() {
                if (!isTransformed(p)) { cancel(); return; }

                EnderDragon d = dragons.get(p.getUniqueId());
                if (d == null || d.isDead()) { cancel(); return; }

                angle += 0.05;

                Location base = p.getLocation().clone().add(0, 3, 0);

                double radius = 4;
                double x = base.getX() + Math.cos(angle) * radius;
                double z = base.getZ() + Math.sin(angle) * radius;
                double y = base.getY();

                Location target = new Location(p.getWorld(), x, y, z, p.getLocation().getYaw(), 0);

                d.teleport(target);
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    // ============================================================
    // HOVER TASK (smooth up-down motion)
    // ============================================================
    private void startHoverTask(Player p) {

        new BukkitRunnable() {
            double t = 0;

            @Override
            public void run() {
                if (!isTransformed(p)) { cancel(); return; }

                EnderDragon d = dragons.get(p.getUniqueId());
                if (d == null || d.isDead()) { cancel(); return; }

                t += 0.15;

                d.teleport(d.getLocation().clone().add(0, Math.sin(t) * 0.08, 0));
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    // ============================================================
    // SHADOW DASH (forward burst)
    // ============================================================
    public void shadowDash(Player p) {
        if (!isTransformed(p)) return;

        EnderDragon d = dragons.get(p.getUniqueId());
        if (d == null) return;

        Vector dir = p.getLocation().getDirection().normalize().multiply(2.5);

        d.getWorld().playSound(d.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 1.2f);
        d.getWorld().spawnParticle(Particle.PORTAL, d.getLocation(), 50, 1, 1, 1, 0.1);

        d.setVelocity(dir);
    }

    // ============================================================
    // VOID BREATH (purple beam)
    // ============================================================
    public void startBreath(Player p) {
        if (!isTransformed(p)) return;

        EnderDragon d = dragons.get(p.getUniqueId());
        if (d == null) return;

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!isTransformed(p)) { cancel(); return; }
                if (ticks++ > 20) { cancel(); return; }

                Location loc = d.getLocation().clone().add(0, 1.5, 0);
                Vector dir = p.getLocation().getDirection().normalize();

                for (int i = 0; i < 10; i++) {
                    loc.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 1, 0, 0, 0, 0);
                    loc.add(dir.multiply(0.6));
                }

                loc.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1.4f);
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    // ============================================================
    // CORRUPTION BURST (AoE explosion)
    // ============================================================
    public void corruptionBurst(Player p) {
        if (!isTransformed(p)) return;

        EnderDragon d = dragons.get(p.getUniqueId());
        if (d == null) return;

        Location loc = d.getLocation();

        loc.getWorld().spawnParticle(Particle.END_ROD, loc, 80, 1.5, 1.5, 1.5, 0.1);
        loc.getWorld().spawnParticle(Particle.PORTAL, loc, 120, 2, 2, 2, 0.2);
        loc.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.6f);

        loc.getWorld().getNearbyEntities(loc, 4, 4, 4).forEach(entity -> {
            if (entity instanceof Player target && !target.equals(p)) {
                target.damage(6);
                target.setVelocity(target.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1.2));
            }
        });
    }

    // ============================================================
    // REMOVE DRAGON
    // ============================================================
    public void removeDragon(Player p) {
        EnderDragon d = dragons.remove(p.getUniqueId());
        if (d != null && !d.isDead()) d.remove();
    }
}
