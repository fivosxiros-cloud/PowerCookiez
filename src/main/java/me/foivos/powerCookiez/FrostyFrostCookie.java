package me.foivos.powerCookiez;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.UUID;

public class FrostyFrostCookie implements CookiePower {

    // ============================================================
    // BASIC INFO
    // ============================================================
    @Override
    public String getName() {
        return "FrostyFrostCookie";
    }

    @Override
    public String getDescription() {
        return "Cold resistance, frost aura and ice-based abilities.";
    }

    @Override
    public Material getDisplayMaterial() {
        return Material.PACKED_ICE;
    }

    @Override
    public ItemStack getDisplayItem() {
        ItemStack item = new ItemStack(Material.COOKIE);
        ItemMeta meta = item.getItemMeta();

        meta.customName(Component.text("Frosty Frost Cookie").color(NamedTextColor.AQUA));
        meta.setLore(Arrays.asList(
                ChatColor.WHITE + "Right-click to consume",
                ChatColor.GRAY + "(Custom Power Cookie)",
                "",
                ChatColor.AQUA + "PASSIVE EFFECTS:",
                ChatColor.GRAY + "❄ Freeze Immunity",
                ChatColor.GRAY + "🔥 Fire Weakness",
                ChatColor.GRAY + "⛄ Resistance II",
                ChatColor.GRAY + "🐌 Slowness I"
        ));

        meta.getPersistentDataContainer().set(
                new NamespacedKey(PowerCookiezMAIN.getInstance(), "cookieName"),
                PersistentDataType.STRING,
                "FrostyFrostCookie"
        );

        meta.setCustomModelData(101); // texture ID
        item.setItemMeta(meta);
        return item;
    }

    // ============================================================
    // BASE ACTIVATION
    // ============================================================
    @Override
    public void activate(Player player) {
        player.sendMessage(ChatColor.AQUA + "❄ Frosty Frost powers awakened!");

        int gear = PowerCookiezMAIN.getInstance().getCookieManager().getGearLevel(player);

        if (gear >= 1) activateGear1(player);
        if (gear >= 2) activateGear2(player);
        if (gear >= 3) activateGear3(player);
    }

    // ============================================================
    // GEAR 1 — FROST DASH (3-hit combo)
    // ============================================================
    @Override
    public void activateGear1(Player player) {

        CookieManager cm = PowerCookiezMAIN.getInstance().getCookieManager();

        int gearId = 1;
        long now = System.currentTimeMillis();
        long comboWindowMs = 4000;
        long cooldownMs = 6000;

        UUID id = player.getUniqueId();

        long lastDash = cm.getComboWindow(player, gearId);
        int currentCount = cm.getComboCount(player, gearId);

        // Combo expired
        if (currentCount > 0 && now - lastDash > comboWindowMs) {

            if (!cm.isGearOnCooldown(player, gearId, cooldownMs)) {
                cm.setGearCooldown(player, gearId);
                player.sendMessage(ChatColor.RED + "❄ Ice Dash combo broken! Cooldown applied.");
            }

            cm.setComboCount(player, gearId, 0);
            cm.setComboWindow(player, gearId, 0L);
            return;
        }

        // Already did 3 dashes
        if (currentCount >= 3) {
            if (cm.isGearOnCooldown(player, gearId, cooldownMs)) {
                player.sendMessage(ChatColor.RED + "Ice Dash is on cooldown!");
                return;
            }

            cm.setGearCooldown(player, gearId);
            player.sendMessage(ChatColor.AQUA + "❄ Ice Dash combo finished!");
            cm.setComboCount(player, gearId, 0);
            cm.setComboWindow(player, gearId, 0L);
            return;
        }

        // Register dash
        cm.setComboCount(player, gearId, currentCount + 1);
        cm.setComboWindow(player, gearId, now);

        int dashNumber = currentCount + 1;

        // Movement
        Location start = player.getLocation();
        player.setVelocity(start.getDirection().multiply(2.5));

        // Ice trail
        for (int i = 0; i < 8; i++) {
            Location step = start.clone().add(start.getDirection().multiply(i));
            Block b = step.clone().add(0, -1, 0).getBlock();

            if (b.getType() == Material.AIR || b.getType() == Material.WATER) {
                b.setType(Material.FROSTED_ICE);

                Bukkit.getScheduler().runTaskLater(PowerCookiezMAIN.getInstance(), () -> {
                    if (b.getType() == Material.FROSTED_ICE) b.setType(Material.AIR);
                }, 20L * 3);
            }

            player.getWorld().spawnParticle(Particle.SNOWFLAKE, step, 10, 0.3, 0.3, 0.3, 0.01);
        }

        // Damage + freeze
        for (Entity e : player.getNearbyEntities(2, 2, 2)) {
            if (e instanceof LivingEntity le && e != player) {
                le.damage(4.0, player);
                le.setFreezeTicks(le.getFreezeTicks() + 80);
            }
        }

        // Sounds
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1.4f);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1f, 1.8f);

        player.sendMessage(ChatColor.AQUA + "❄ Ice Dash " + dashNumber + "/3");

        // If 3rd dash → cooldown
        if (dashNumber >= 3) {
            cm.setGearCooldown(player, gearId);
            cm.setComboCount(player, gearId, 0);
            cm.setComboWindow(player, gearId, 0L);
            player.sendMessage(ChatColor.AQUA + "❄ Ice Dash combo finished!");
        }
    }

    // ============================================================
    // GEAR 2 — ICE WALLS
    // ============================================================
    @Override
    public void activateGear2(Player player) {

        CookieManager cm = PowerCookiezMAIN.getInstance().getCookieManager();

        int gearId = 2;
        long cooldownMs = 12000;

        if (cm.isGearOnCooldown(player, gearId, cooldownMs)) {
            player.sendMessage(ChatColor.RED + "Gear 2 is on cooldown!");
            return;
        }
        cm.setGearCooldown(player, gearId);

        Location start = player.getLocation();
        Vector direction = start.getDirection().normalize();
        Vector right = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();

        int wallCount = 3;

        for (int i = 1; i <= wallCount; i++) {

            Location wallLoc = start.clone().add(direction.clone().multiply(i * 2));

            for (int x = -1; x <= 1; x++) {
                for (int y = 0; y <= 2; y++) {

                    Location blockLoc = wallLoc.clone()
                            .add(right.clone().multiply(x))
                            .add(0, y, 0);

                    Block b = blockLoc.getBlock();

                    if (b.getType() == Material.AIR || b.getType() == Material.WATER) {
                        b.setType(Material.ICE);

                        Bukkit.getScheduler().runTaskLater(PowerCookiezMAIN.getInstance(), () -> {
                            if (b.getType() == Material.ICE) b.setType(Material.AIR);
                        }, 20L * 5);

                        player.getWorld().spawnParticle(Particle.SNOWFLAKE, blockLoc, 10, 0.3, 0.3, 0.3, 0.01);
                    }
                }
            }
        }

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_PLACE, 1f, 1.2f);
    }

    // ============================================================
    // GEAR 3 — ABSOLUTE ZERO
    // ============================================================
    @Override
    public void activateGear3(Player player) {

        CookieManager cm = PowerCookiezMAIN.getInstance().getCookieManager();

        int gearId = 3;
        long cooldownMs = 18000;

        if (cm.isGearOnCooldown(player, gearId, cooldownMs)) {
            player.sendMessage(ChatColor.RED + "Gear 3 is on cooldown!");
            return;
        }
        cm.setGearCooldown(player, gearId);

        Location center = player.getLocation();
        World world = player.getWorld();

        // Particles
        world.spawnParticle(Particle.SNOWFLAKE, center, 600, 3, 3, 3, 0.1);
        world.spawnParticle(Particle.CLOUD, center, 300, 2.5, 2.5, 2.5, 0.05);
        world.spawnParticle(Particle.ITEM_SNOWBALL, center, 200, 2, 2, 2, 0.1);

        // Sounds
        world.playSound(center, Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 0.4f);
        world.playSound(center, Sound.BLOCK_GLASS_BREAK, 1f, 0.2f);
        world.playSound(center, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 0.3f);

        // Resistance buff
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.RESISTANCE,
                20 * 6,
                255,
                false,
                false,
                true
        ));

        double radius = 20.0;

        // Damage + freeze entities
        for (Entity e : world.getNearbyEntities(center, radius, radius, radius)) {

            if (e instanceof LivingEntity le && e != player) {
                le.setFreezeTicks(200);
                le.damage(6.0, player);

                world.spawnParticle(Particle.SNOWFLAKE, le.getLocation().add(0, 1, 0),
                        60, 0.6, 0.6, 0.6, 0.01);
            }

            if (e instanceof org.bukkit.entity.Projectile) {
                e.setVelocity(new Vector(0, 0, 0));
            }
        }

        // Freeze ground
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {

                Location blockLoc = center.clone().add(x, -1, z);
                Block b = blockLoc.getBlock();

                if (b.getType() == Material.AIR || b.getType() == Material.WATER || b.getType().isSolid()) {
                    b.setType(Material.FROSTED_ICE);

                    Bukkit.getScheduler().runTaskLater(PowerCookiezMAIN.getInstance(), () -> {
                        if (b.getType() == Material.FROSTED_ICE) b.setType(Material.AIR);
                    }, 20L * 6);
                }
            }
        }

        world.playSound(center, Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1f, 0.1f);
        world.playSound(center, Sound.BLOCK_GLASS_BREAK, 1f, 0.05f);
    }

    // ============================================================
    // MAX GEARS
    // ============================================================
    @Override
    public int getMaxGears() {
        return 3;
    }

    // ============================================================
    // GEAR DESCRIPTIONS
    // ============================================================
    @Override
    public String getGearDescription(int gear) {
        return switch (gear) {
            case 1 -> "❄ Frost Dash — 3-hit combo dash with freeze trail.";
            case 2 -> "🧊 Ice Walls — Creates 3 ice walls in front of you.";
            case 3 -> "🌨 Absolute Zero — Massive freeze explosion.";
            default -> "No ability.";
        };
    }

    // ============================================================
    // COOLDOWNS
    // ============================================================
    @Override
    public long getGearCooldownMs(int gear) {
        return switch (gear) {
            case 1 -> 6000;
            case 2 -> 12000;
            case 3 -> 18000;
            default -> 0;
        };
    }
}
