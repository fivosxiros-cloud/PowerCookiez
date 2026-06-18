package me.foivos.powerCookiez.poweritems.rings.ele;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import me.foivos.powerCookiez.poweritems.RingManager;
import me.foivos.powerCookiez.poweritems.rings.RingCategory;
import me.foivos.powerCookiez.poweritems.rings.RingPower;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class StrikyWindsRing implements RingPower {

    private final Map<UUID, Long> passiveCooldown = new HashMap<>();

    @Override
    public String getName() {
        return "StrikyWindsRing";
    }

    @Override
    public RingCategory getCategory() {
        return RingCategory.ELE;
    }

    // ============================================================
    // DISPLAY ITEM
    // ============================================================
    @Override
    public ItemStack getDisplayItem() {
        ItemStack item = new ItemStack(Material.FEATHER);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.WHITE + "Striky Winds ELE‑Ring");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "A swift ring infused with the power of the wind.",
                "",
                ChatColor.GOLD + "Category: " + ChatColor.YELLOW + "> ELEMENTAL <",
                ChatColor.GOLD + "Rarity: " + ChatColor.RED + "Mythic",
                "",
                ChatColor.GOLD + "PASSIVES:",
                ChatColor.GRAY + "🌬 Wind Step (+20% speed)",
                ChatColor.GRAY + "🌬 Fall Immunity",
                "",
                ChatColor.GOLD + "ABILITIES:",
                ChatColor.GRAY + "A — Air Dash",
                ChatColor.GRAY + "B — Tornado Lift",
                ChatColor.GRAY + "C — Wind Cutter",
                ChatColor.GRAY + "D — Cyclone Burst"
        ));


        meta.getPersistentDataContainer().set(
                new NamespacedKey(PowerCookiezMAIN.getInstance(), "ringName"),
                PersistentDataType.STRING,
                "StrikyWindsRing"
        );

        meta.setCustomModelData(103);
        item.setItemMeta(meta);
        return item;
    }

    // ============================================================
    // PASSIVES
    // ============================================================
    @Override
    public void applyPassives(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;

        // Speed boost
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED,
                40, 1, true, false, false
        ));

        // Fall damage immunity
        player.setFallDistance(0);

        // Small wind pulse every 4 seconds
        long now = System.currentTimeMillis();
        long last = passiveCooldown.getOrDefault(player.getUniqueId(), 0L);

        if (now - last >= 4000) {
            passiveCooldown.put(player.getUniqueId(), now);

            World world = player.getWorld();
            Location loc = player.getLocation();

            world.spawnParticle(Particle.CLOUD, loc, 30, 0.8, 1, 0.8, 0.02);
            world.playSound(loc, Sound.ENTITY_PHANTOM_FLAP, 0.6f, 1.6f);
        }
    }

    // ============================================================
    // ABILITY A — Air Dash
    // ============================================================
    @Override
    public void abilityA(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;
        if (!rm.isRingEnabled(player)) return;
        if (!RingManager.checkCooldown(player, "A", 5000)) return; // 5s cooldown

        Vector dash = player.getLocation().getDirection().normalize().multiply(1.8);
        dash.setY(0.4);
        player.setVelocity(dash);

        World world = player.getWorld();

        // Trail
        Bukkit.getScheduler().runTaskTimer(PowerCookiezMAIN.getInstance(), task -> {
            if (!player.isOnline()) { task.cancel(); return; }

            world.spawnParticle(Particle.CLOUD, player.getLocation(), 20, 0.3, 0.3, 0.3, 0.01);
            world.spawnParticle(Particle.END_ROD, player.getLocation(), 5, 0.2, 0.2, 0.2, 0.01);

        }, 0L, 1L);
    }

    // ============================================================
    // ABILITY B — Tornado Lift
    // ============================================================
    @Override
    public void abilityB(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;
        if (!rm.isRingEnabled(player)) return;
        if (!RingManager.checkCooldown(player, "B", 8000)) return; // 8s cooldown

        World world = player.getWorld();
        Location loc = player.getLocation();

        world.playSound(loc, Sound.ENTITY_PHANTOM_FLAP, 1f, 1.2f);

        for (Entity e : world.getNearbyEntities(loc, 5, 5, 5)) {
            if (e instanceof LivingEntity le && le != player) {

                Vector lift = new Vector(0, 1.2, 0);
                le.setVelocity(lift);

                le.damage(2.0, player);

                world.spawnParticle(Particle.CLOUD, le.getLocation(), 40, 0.5, 1, 0.5, 0.02);
            }
        }
    }

    // ============================================================
    // ABILITY C — Wind Cutter (air blade projectile)
    // ============================================================
    @Override
    public void abilityC(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;
        if (!rm.isRingEnabled(player)) return;
        if (!RingManager.checkCooldown(player, "C", 6000)) return; // 6s cooldown

        World world = player.getWorld();
        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        ArmorStand blade = world.spawn(eye, ArmorStand.class, stand -> {
            stand.setInvisible(true);
            stand.setMarker(true);
            stand.setGravity(false);
        });

        Bukkit.getScheduler().runTaskTimer(PowerCookiezMAIN.getInstance(), task -> {

            if (!blade.isValid()) { task.cancel(); return; }

            blade.teleport(blade.getLocation().add(dir.clone().multiply(1)));

            world.spawnParticle(Particle.END_ROD, blade.getLocation(), 5, 0.1, 0.1, 0.1, 0.01);

            for (Entity e : world.getNearbyEntities(blade.getLocation(), 1, 1, 1)) {
                if (e instanceof LivingEntity le && le != player) {
                    le.damage(5.0, player);
                    le.setVelocity(dir.clone().multiply(0.8));
                    blade.remove();
                    task.cancel();
                    return;
                }
            }

        }, 0L, 1L);
    }

    // ============================================================
    // ABILITY D — Cyclone Burst
    // ============================================================
    @Override
    public void abilityD(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;
        if (!rm.isRingEnabled(player)) return;
        if (!RingManager.checkCooldown(player, "D", 10000)) return; // 10s cooldown

        World world = player.getWorld();
        Location loc = player.getLocation();

        world.playSound(loc, Sound.ENTITY_PHANTOM_FLAP, 1f, 0.6f);

        // Knockback burst
        for (Entity e : world.getNearbyEntities(loc, 6, 4, 6)) {
            if (e instanceof LivingEntity le && le != player) {

                Vector push = le.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1.6);
                push.setY(0.4);
                le.setVelocity(push);

                le.damage(3.0, player);

                world.spawnParticle(Particle.CLOUD, le.getLocation(), 40, 0.5, 1, 0.5, 0.02);
            }
        }

        // Visual cyclone
        world.spawnParticle(Particle.CLOUD, loc, 200, 3, 1, 3, 0.05);
        world.spawnParticle(Particle.END_ROD, loc, 40, 2, 1, 2, 0.02);
    }
}
