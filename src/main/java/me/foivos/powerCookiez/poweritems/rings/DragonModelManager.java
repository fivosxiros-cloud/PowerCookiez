package me.foivos.powerCookiez.poweritems.rings;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import me.foivos.powerCookiez.poweritems.RingManager;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class DragonModelManager {

    private final Map<UUID, Map<DragonPart, ArmorStand>> dragons = new HashMap<>();
    private final PowerCookiezMAIN plugin = PowerCookiezMAIN.getInstance();

    public boolean isTransformed(Player p) {
        return dragons.containsKey(p.getUniqueId());
    }

    public void spawnDragon(Player p) {

        p.sendMessage("§5§lYou become the Shadow Dragon...");
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.6f);

        p.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.INVISIBILITY,
                Integer.MAX_VALUE, 1, false, false, false
        ));

        Map<DragonPart, ArmorStand> parts = new HashMap<>();

        Location base = p.getLocation().clone().add(0, 2.2, 0);

        // === CORE ===
        ArmorStand core = spawnPart(base, null);
        parts.put(DragonPart.CORE, core);

        // === HEAD ===
        ArmorStand head = spawnPart(base.clone().add(0, 0.8, -1.2), Material.DRAGON_HEAD);
        parts.put(DragonPart.HEAD, head);

        // === CHEST ===
        ArmorStand chest = spawnPart(base.clone().add(0, 0.3, -0.5), Material.CRYING_OBSIDIAN);
        parts.put(DragonPart.CHEST, chest);

        // === BODY ===
        ArmorStand body = spawnPart(base.clone().add(0, 0.1, 0.8), Material.BLACK_CONCRETE);
        parts.put(DragonPart.BODY, body);

        // === WINGS ===
        ArmorStand wingL = spawnPart(base.clone().add(1.2, 0.4, 0.2), Material.BLACK_CONCRETE);
        ArmorStand wingR = spawnPart(base.clone().add(-1.2, 0.4, 0.2), Material.BLACK_CONCRETE);
        parts.put(DragonPart.WING_LEFT, wingL);
        parts.put(DragonPart.WING_RIGHT, wingR);

        // === TAIL ===
        ArmorStand tail = spawnPart(base.clone().add(0, -0.1, 1.8), Material.BLACK_CONCRETE);
        parts.put(DragonPart.TAIL, tail);

        // === BREATH EMITTER ===
        ArmorStand emitter = spawnPart(base.clone().add(0, 0.6, -1.6), Material.END_ROD);
        parts.put(DragonPart.BREATH_EMITTER, emitter);

        dragons.put(p.getUniqueId(), parts);

        // === START ANIMATIONS ===
        int id1 = new DragonFollowerTask(p, this).runTaskTimer(plugin, 1, 1).getTaskId();
        RingManager.registerTask(p, id1);

        int id2 = new DragonOrbitTask(p, this).runTaskTimer(plugin, 1, 1).getTaskId();
        RingManager.registerTask(p, id2);

        int id3 = new DragonHoverTask(p, this).runTaskTimer(plugin, 1, 1).getTaskId();
        RingManager.registerTask(p, id3);

        int id4 = new DragonWingAnimationTask(p, this).runTaskTimer(plugin, 1, 1).getTaskId();
        RingManager.registerTask(p, id4);
    }

    private ArmorStand spawnPart(Location loc, Material mat) {
        ArmorStand as = loc.getWorld().spawn(loc, ArmorStand.class, stand -> {
            stand.setInvisible(true);
            stand.setMarker(true);
            stand.setGravity(false);
            stand.setSmall(false);
            stand.setBasePlate(false);
            stand.setArms(false);
            stand.setInvulnerable(true);
        });

        if (mat != null) {
            as.getEquipment().setHelmet(new ItemStack(mat));
        }

        return as;
    }

    public void removeDragon(Player p) {
        if (!isTransformed(p)) return;

        Map<DragonPart, ArmorStand> parts = dragons.remove(p.getUniqueId());
        parts.values().forEach(ArmorStand::remove);

        p.removePotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY);

        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1f, 0.8f);
    }

    public Map<DragonPart, ArmorStand> getParts(Player p) {
        return dragons.get(p.getUniqueId());
    }

    // === ABILITY B ===
    public void shadowDash(Player p) {
        Vector dir = p.getLocation().getDirection().normalize().multiply(2.5);
        p.setVelocity(dir);
        p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 80, 1, 1, 1, 0.2);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 1.2f);
    }

    // === ABILITY C ===
    public void startBreath(Player p) {
        int id = new DragonBreathTask(p, this).runTaskTimer(plugin, 1, 1).getTaskId();
        RingManager.registerTask(p, id);
    }

    // === ABILITY D ===
    public void corruptionBurst(Player p) {
        p.getWorld().spawnParticle(Particle.REVERSE_PORTAL, p.getLocation(), 1);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.5f);
    }
}
