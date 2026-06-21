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

public class SplashyWaterRing implements RingPower {

    private final Map<UUID, Long> passiveCooldown = new HashMap<>();

    @Override
    public String getName() {
        return "SplashyWaterRing";
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
        ItemStack item = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.AQUA + "Splashy Water ELE‑Ring");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "A mystical ring infused with pure water energy.",
                "",
                ChatColor.GOLD + "Category: " + ChatColor.YELLOW + "> ELEMENTAL <",
                ChatColor.GOLD + "Rarity: " + ChatColor.RED + "Mythic",
                "",
                ChatColor.GOLD + "PASSIVES:",
                ChatColor.GRAY + "💧 Water Regeneration",
                ChatColor.GRAY + "💧 Drown Immunity",
                "",
                ChatColor.GOLD + "ABILITIES:",
                ChatColor.GRAY + "A — Water Jet Dash",
                ChatColor.GRAY + "B — Bubble Prison",
                ChatColor.GRAY + "C — Healing Splash",
                ChatColor.GRAY + "D — Tsunami Wave"
        ));


        meta.getPersistentDataContainer().set(
                new NamespacedKey(PowerCookiezMAIN.getInstance(), "ringName"),
                PersistentDataType.STRING,
                "SplashyWaterRing"
        );

        meta.setCustomModelData(102);
        item.setItemMeta(meta);
        return item;
    }

    // ============================================================
    // PASSIVES (called every second)
    // ============================================================
    @Override
    public void applyPassives(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;

        // Drown immunity
        player.setRemainingAir(player.getMaximumAir());

        // Water regeneration every 3 seconds
        long now = System.currentTimeMillis();
        long last = passiveCooldown.getOrDefault(player.getUniqueId(), 0L);

        if (now - last >= 3000) {
            passiveCooldown.put(player.getUniqueId(), now);

            // Heal +1 heart if in water or rain
            if (player.getLocation().getBlock().isLiquid() || player.getWorld().hasStorm()) {
                double newHealth = Math.min(player.getHealth() + 2.0, player.getMaxHealth());
                player.setHealth(newHealth);

                player.getWorld().spawnParticle(Particle.SPLASH, player.getLocation(), 20, 0.5, 1, 0.5, 0.1);
                player.playSound(player.getLocation(), Sound.ENTITY_DOLPHIN_SPLASH, 1f, 1.4f);
            }
        }
    }

    // ============================================================
    // ABILITY A — Water Jet Dash
    // ============================================================
    @Override
    public void abilityA(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;
        if (!rm.isRingEnabled(player)) return;
        if (RingManager.isCooldown(player, "A", 6000)) return; // 6s cooldown

        World world = player.getWorld();
        Location loc = player.getLocation();

        Vector dir = loc.getDirection().normalize().multiply(-1.2);
        dir.setY(0.4);

        player.setVelocity(dir);

        // Water trail
        Bukkit.getScheduler().runTaskTimer(PowerCookiezMAIN.getInstance(), task -> {
            if (!player.isOnline()) { task.cancel(); return; }

            Location pLoc = player.getLocation();
            world.spawnParticle(Particle.SPLASH, pLoc, 20, 0.3, 0.3, 0.3, 0.1);
            world.spawnParticle(Particle.CLOUD, pLoc, 10, 0.3, 0.3, 0.3, 0.01);

        }, 0L, 1L);
    }

    // ============================================================
    // ABILITY B — Bubble Prison
    // ============================================================
    @Override
    public void abilityB(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;
        if (!rm.isRingEnabled(player)) return;
        if (RingManager.isCooldown(player, "B", 8000)) return; // 8s cooldown

        World world = player.getWorld();
        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        Entity target = null;

        // Raycast 15 blocks
        for (Entity e : world.getNearbyEntities(eye, 15, 15, 15)) {
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

        // Bubble particles
        for (int i = 0; i < 20; i++) {
            Location point = eye.clone().add(dir.clone().multiply(i));
            world.spawnParticle(Particle.BUBBLE, point, 5, 0.1, 0.1, 0.1, 0.01);
        }

        // Trap target
        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 4, true, false, false));
        le.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 60, 250, true, false, false)); // can't jump
        le.getWorld().playSound(le.getLocation(), Sound.ENTITY_FISH_SWIM, 1f, 1.2f);

        // Visual bubble prison
        Bukkit.getScheduler().runTaskTimer(PowerCookiezMAIN.getInstance(), task -> {
            if (!le.isValid()) { task.cancel(); return; }

            world.spawnParticle(Particle.BUBBLE, le.getLocation().add(0,1,0), 30, 0.7, 1, 0.7, 0.1);

        }, 0L, 5L);
    }

    // ============================================================
    // ABILITY C — Healing Splash
    // ============================================================
    @Override
    public void abilityC(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;
        if (!rm.isRingEnabled(player)) return;
        if (RingManager.isCooldown(player, "C", 5000)) return; // 5s cooldown

        World world = player.getWorld();
        Location loc = player.getLocation();

        // Heal +2 hearts
        double newHealth = Math.min(player.getHealth() + 4.0, player.getMaxHealth());
        player.setHealth(newHealth);

        // Particles
        world.spawnParticle(Particle.SPLASH, loc, 40, 1, 1, 1, 0.1);
        world.spawnParticle(Particle.HEART, loc, 10, 0.5, 1, 0.5, 0.1);
        world.playSound(loc, Sound.ENTITY_DOLPHIN_SPLASH, 1f, 1.4f);
    }

    // ============================================================
    // ABILITY D — Tsunami Wave
    // ============================================================
    @Override
    public void abilityD(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;
        if (!rm.isRingEnabled(player)) return;
        if (RingManager.isCooldown(player, "D", 10000)) return; // 10s cooldown

        World world = player.getWorld();
        Location loc = player.getLocation();

        player.sendMessage(ChatColor.AQUA + "🌊 Tsunami unleashed!");

        // Push wave
        for (Entity e : world.getNearbyEntities(loc, 7, 4, 7)) {
            if (e instanceof LivingEntity le && le != player) {

                Vector push = le.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1.4);
                push.setY(0.5);
                le.setVelocity(push);

                le.damage(3.0, player);
            }
        }

        // Visual wave
        world.spawnParticle(Particle.SPLASH, loc, 200, 4, 1, 4, 0.1);
        world.playSound(loc, Sound.ENTITY_PLAYER_SPLASH, 1f, 0.8f);
    }
}

