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

public class AmberFlameRing implements RingPower {

    private final Map<UUID, Long> passiveCooldown = new HashMap<>();

    @Override
    public String getName() {
        return "AmberFlameRing";
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
        ItemStack item = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "Amber Flame ELE‑Ring");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "A legendary elemental ring of fire.",
                "",
                ChatColor.GOLD + "Category: " + ChatColor.YELLOW + "> ELEMENTAL <",
                ChatColor.GOLD + "Rarity: " + ChatColor.RED + "Mythic",
                "",
                ChatColor.GOLD + "PASSIVES:",
                ChatColor.GRAY + "🔥 Fire Immunity",
                ChatColor.GRAY + "🔥 Flame Pulse Aura (1.5 hearts every 5s)",
                "",
                ChatColor.GOLD + "ABILITIES:",
                ChatColor.GRAY + "A — Flame Burst",
                ChatColor.GRAY + "B — Fire Dash",
                ChatColor.GRAY + "C — Flame Chains",
                ChatColor.GRAY + "D — Inferno Field"
        ));

        meta.getPersistentDataContainer().set(
                new NamespacedKey(PowerCookiezMAIN.getInstance(), "ringName"),
                PersistentDataType.STRING,
                "AmberFlameRing"
        );

        meta.setCustomModelData(101);
        item.setItemMeta(meta);
        return item;
    }

    // ============================================================
    // PASSIVES (called every second)
    // ============================================================
    @Override
    public void applyPassives(Player player) {

        // Must be holding the ring
        if (!PowerCookiezMAIN.getInstance().getRingManager().isHoldingRing(player)) return;

        // FIRE IMMUNITY
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.FIRE_RESISTANCE,
                40, 0, true, false, false
        ));

        // FLAME PULSE AURA every 5 seconds
        long now = System.currentTimeMillis();
        long last = passiveCooldown.getOrDefault(player.getUniqueId(), 0L);

        if (now - last >= 5000) {
            passiveCooldown.put(player.getUniqueId(), now);

            Location loc = player.getLocation();
            World world = loc.getWorld();

            // Particles
            world.spawnParticle(Particle.FLAME, loc, 80, 1.5, 1, 1.5, 0.05);
            world.spawnParticle(Particle.LAVA, loc, 20, 1.2, 0.8, 1.2, 0.1);
            world.playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1f, 1.4f);

            // Damage entities in radius 5
            for (Entity e : world.getNearbyEntities(loc, 5, 5, 5)) {
                if (e instanceof LivingEntity le && le != player) {
                    le.damage(3.0, player); // 1.5 hearts
                    le.setFireTicks(60);
                }
            }
        }
    }

    // ============================================================
    // ABILITY A — Flame Burst (AoE explosion)
    // ============================================================
    @Override
    public void abilityA(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;
        if (!rm.isRingEnabled(player)) return;
        if (!RingManager.checkCooldown(player, "A", 7000)) return; // 7s cooldown

        Location loc = player.getLocation();
        World world = loc.getWorld();

        // Explosion
        world.spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
        world.spawnParticle(Particle.FLAME, loc, 200, 2, 1.5, 2, 0.05);
        world.spawnParticle(Particle.LAVA, loc, 40, 1.5, 1, 1.5, 0.1);
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.4f, 0.7f);

        // Damage
        for (Entity e : world.getNearbyEntities(loc, 6, 6, 6)) {
            if (e instanceof LivingEntity le && le != player) {
                le.damage(10.0, player); // 5 hearts
                le.setFireTicks(80);
            }
        }
    }

    // ============================================================
    // ABILITY B — Fire Dash
    // ============================================================
    @Override
    public void abilityB(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;
        if (!rm.isRingEnabled(player)) return;
        if (!RingManager.checkCooldown(player, "B", 6000)) return; // 6s cooldown

        World world = player.getWorld();
        Location loc = player.getLocation();

        Vector dir = loc.getDirection().normalize().multiply(1.4);
        dir.setY(0.3);

        player.setVelocity(dir);

        // Trail
        Bukkit.getScheduler().runTaskTimer(PowerCookiezMAIN.getInstance(), task -> {
            if (!player.isOnline()) { task.cancel(); return; }

            Location pLoc = player.getLocation();
            world.spawnParticle(Particle.FLAME, pLoc, 20, 0.3, 0.3, 0.3, 0.02);
            world.spawnParticle(Particle.SMOKE, pLoc, 10, 0.3, 0.3, 0.3, 0.01);

        }, 0L, 1L);

        // Damage on impact
        for (Entity e : world.getNearbyEntities(loc, 3, 3, 3)) {
            if (e instanceof LivingEntity le && le != player) {
                le.damage(6.0, player); // 3 hearts
                le.setFireTicks(60);
            }
        }
    }

    // ============================================================
    // ABILITY C — Flame Chains (pull target)
    // ============================================================
    @Override
    public void abilityC(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;
        if (!rm.isRingEnabled(player)) return;
        if (!RingManager.checkCooldown(player, "C", 8000)) return; // 8s cooldown

        World world = player.getWorld();
        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        Entity target = null;

        // Raycast 20 blocks
        for (Entity e : world.getNearbyEntities(eye, 20, 20, 20)) {
            if (e instanceof LivingEntity le && le != player) {
                Vector to = le.getLocation().toVector().subtract(eye.toVector());
                if (dir.dot(to.normalize()) > 0.97) {
                    target = le;
                    break;
                }
            }
        }

        if (target == null) {
            player.sendMessage(ChatColor.GRAY + "No target found.");
            return;
        }

        LivingEntity le = (LivingEntity) target;

        // Chain particles
        for (int i = 0; i < 20; i++) {
            Location point = eye.clone().add(dir.clone().multiply(i));
            world.spawnParticle(Particle.FLAME, point, 3, 0.1, 0.1, 0.1, 0.01);
            world.spawnParticle(Particle.SMOKE, point, 2, 0.1, 0.1, 0.1, 0.01);
        }

        // Pull target
        Vector pull = player.getLocation().toVector().subtract(le.getLocation().toVector()).normalize().multiply(0.8);
        pull.setY(0.3);
        le.setVelocity(pull);

        // Burn
        le.damage(4.0, player);
        le.setFireTicks(80);
    }

    // ============================================================
    // ABILITY D — Inferno Field (AoE fire zone)
    // ============================================================
    @Override
    public void abilityD(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;
        if (!rm.isRingEnabled(player)) return;
        if (!RingManager.checkCooldown(player, "D", 10000)) return; // 10s cooldown

        World world = player.getWorld();
        Location center = player.getLocation();

        player.sendMessage(ChatColor.GOLD + "🔥 Inferno Field unleashed!");

        // Field duration: 4 seconds
        Bukkit.getScheduler().runTaskTimer(PowerCookiezMAIN.getInstance(), task -> {

            if (!player.isOnline()) { task.cancel(); return; }

            // Particles
            world.spawnParticle(Particle.FLAME, center, 200, 4, 1, 4, 0.05);
            world.spawnParticle(Particle.LAVA, center, 40, 3, 1, 3, 0.1);
            world.playSound(center, Sound.BLOCK_FIRE_AMBIENT, 1f, 1.2f);

            // Damage entities
            for (Entity e : world.getNearbyEntities(center, 8, 8, 8)) {
                if (e instanceof LivingEntity le && le != player) {
                    le.damage(3.0, player);
                    le.setFireTicks(60);
                }
            }

        }, 0L, 20L); // 4 seconds
    }
}
