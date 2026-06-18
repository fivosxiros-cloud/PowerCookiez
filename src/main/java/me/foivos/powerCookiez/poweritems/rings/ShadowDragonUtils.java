package me.foivos.powerCookiez.poweritems.rings;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ShadowDragonUtils {

    public static void corruptionExplosion(Player p) {
        Location loc = p.getLocation();

        loc.getWorld().spawnParticle(Particle.REVERSE_PORTAL, loc, 1);
        loc.getWorld().spawnParticle(Particle.PORTAL, loc, 80, 1, 1, 1, 0.2);
        loc.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 40, 1, 1, 1, 0.1);

        loc.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.5f);
        loc.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_SCREAM, 1f, 0.8f);
    }

    public static void corruptionDamage(Player p, double radius) {
        Location loc = p.getLocation();

        for (Entity e : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
            if (e instanceof Player && e != p) {
                Player target = (Player) e;
                target.damage(4.0, p);
                target.setVelocity(target.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1.2));
                target.addPotionEffect(new org.bukkit.potion.PotionEffect(
                        org.bukkit.potion.PotionEffectType.WITHER, 60, 1
                ));
            }
        }
    }

    public static void shadowTrail(Player p) {
        Location loc = p.getLocation();
        loc.getWorld().spawnParticle(Particle.PORTAL, loc, 40, 0.5, 0.5, 0.5, 0.1);
    }
}
