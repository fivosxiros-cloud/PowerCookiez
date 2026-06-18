package me.foivos.powerCookiez.poweritems.rings;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;

public class DragonBreathTask extends BukkitRunnable {

    private final Player player;
    private final DragonModelManager manager;
    private int ticks = 0;

    public DragonBreathTask(Player player, DragonModelManager manager) {
        this.player = player;
        this.manager = manager;
    }

    @Override
    public void run() {

        if (!manager.isTransformed(player)) {
            cancel();
            return;
        }

        ticks++;
        if (ticks > 40) { // 2 seconds
            cancel();
            return;
        }

        Map<DragonPart, ArmorStand> parts = manager.getParts(player);
        ArmorStand emitter = parts.get(DragonPart.BREATH_EMITTER);

        Location loc = emitter.getLocation().clone();
        Vector dir = player.getLocation().getDirection().normalize();

        // Breath particles
        loc.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 20, 0.2, 0.2, 0.2, 0.01);
        loc.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_SHOOT, 0.7f, 0.6f);

        // Damage hitbox
        for (Entity e : loc.getWorld().getNearbyEntities(loc, 2, 2, 2)) {
            if (e instanceof Player && e != player) {
                Player target = (Player) e;
                target.damage(2.0, player);
                target.addPotionEffect(new org.bukkit.potion.PotionEffect(
                        org.bukkit.potion.PotionEffectType.WITHER, 40, 1
                ));
                target.setVelocity(dir.multiply(0.4));
            }
        }
    }
}
