package me.foivos.powerCookiez.Cookiez.Cookies;

import me.foivos.powerCookiez.Cookiez.CookieManager;
import me.foivos.powerCookiez.Cookiez.CookiePower;
import me.foivos.powerCookiez.PowerCookiezMAIN;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class SmokySmokeCookie implements CookiePower {

    // ============================================================
    // BASIC INFO
    // ============================================================
    @Override
    public String getName() {
        return "SmokySmokeCookie";
    }

    @Override
    public String getDescription() {
        return "Speed, smoke aura, teleportation, cages, levitation and smoke-based powers.";
    }

    @Override
    public Material getDisplayMaterial() {
        return Material.COAL;
    }

    @Override
    public ItemStack getDisplayItem() {
        ItemStack item = new ItemStack(Material.COOKIE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.DARK_GRAY + "Smoky Smoke Cookie");
        meta.setLore(Arrays.asList(
                ChatColor.WHITE + "Right-click to consume",
                ChatColor.GRAY + "(Custom Power Cookie)",
                "",
                ChatColor.DARK_GRAY + "PASSIVE EFFECTS:",
                ChatColor.GRAY + "💨 Speed II",
                ChatColor.GRAY + "🔥 Reduced Fire Damage",
                ChatColor.GRAY + "❄ Reduced Freeze Damage",
                ChatColor.GRAY + "☁ Smoke Choke Aura (damages enemies)",
                "",
                ChatColor.RED + "DISADVANTAGE:",
                ChatColor.GRAY + "🫁 Reduced Oxygen (extra damage underwater)"
        ));

        meta.getPersistentDataContainer().set(
                new NamespacedKey(PowerCookiezMAIN.getInstance(), "cookieName"),
                PersistentDataType.STRING,
                "SmokySmokeCookie"
        );

        meta.setCustomModelData(102);
        item.setItemMeta(meta);
        return item;
    }

    // ============================================================
    // BASE ACTIVATION
    // ============================================================
    @Override
    public void activate(Player player) {

        player.sendMessage(ChatColor.DARK_GRAY + "☁ Smoky Smoke powers awakened!");

        int gear = PowerCookiezMAIN.getInstance().getCookieManager().getGearLevel(player);

        if (gear >= 1) activateGear1(player);
        if (gear >= 2) activateGear2(player);
        if (gear >= 3) activateGear3(player);
        if (gear >= 4) activateGear4(player);
        if (gear >= 5) activateGear5(player);
        if (gear >= 6) activateGear6(player);
    }

    // ============================================================
    // GEAR 1 — Smoke Levitation + Darkness Burst
    // ============================================================
    @Override
    public void activateGear1(Player player) {

        CookieManager cm = PowerCookiezMAIN.getInstance().getCookieManager();
        int gearId = 1;
        long cooldownMs = 6000;

        if (cm.isGearOnCooldown(player, gearId, cooldownMs)) {
            player.sendMessage(ChatColor.RED + "Gear 1 is on cooldown!");
            return;
        }

        cm.setGearCooldown(player, gearId);

        player.sendMessage(ChatColor.DARK_GRAY + "☁ Smoke Flight activated!");

        // Levitation
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.LEVITATION,
                20 * 5,
                1,
                false, false, true
        ));

        // Darkness burst
        double radius = 10.0;

        for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
            if (e instanceof Player target && target != player) {

                target.addPotionEffect(new PotionEffect(
                        PotionEffectType.DARKNESS, 20 * 7, 0, false, false, true
                ));

                target.addPotionEffect(new PotionEffect(
                        PotionEffectType.NAUSEA, 20 * 7, 0, false, false, true
                ));

                target.getWorld().spawnParticle(
                        Particle.CAMPFIRE_COSY_SMOKE,
                        target.getLocation().add(0, 1, 0),
                        40, 0.5, 0.5, 0.5, 0.01
                );
            }
        }

        // Smoke burst
        player.getWorld().spawnParticle(
                Particle.CAMPFIRE_COSY_SMOKE,
                player.getLocation(),
                200, 1.5, 1.5, 1.5, 0.02
        );

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1f, 0.6f);
    }

    // ============================================================
    // GEAR 2 — Smoke Cage + DoT
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

        Entity target = player.getTargetEntity(10);

        if (!(target instanceof LivingEntity le)) {
            player.sendMessage(ChatColor.RED + "No target found!");
            return;
        }

        player.sendMessage(ChatColor.DARK_GRAY + "☁ Smoke Cage activated!");

        // Immobilize
        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 8, 255, false, false, true));
        le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 8, 0, false, false, true));
        le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 8, 0, false, false, true));

        // Damage over time
        Bukkit.getScheduler().runTaskTimer(PowerCookiezMAIN.getInstance(), task -> {

            if (!le.isValid() || le.isDead()) {
                task.cancel();
                return;
            }

            le.damage(2.0, player);

        }, 20L, 20L);

        // Smoke cage particles
        Bukkit.getScheduler().runTaskTimer(PowerCookiezMAIN.getInstance(), task -> {

            if (!le.isValid() || le.isDead()) {
                task.cancel();
                return;
            }

            for (double theta = 0; theta < Math.PI * 2; theta += Math.PI / 8) {
                for (double phi = 0; phi < Math.PI; phi += Math.PI / 8) {

                    double x = 1.5 * Math.sin(phi) * Math.cos(theta);
                    double y = 1.5 * Math.cos(phi);
                    double z = 1.5 * Math.sin(phi) * Math.sin(theta);

                    Location loc = le.getLocation().add(x, y + 1, z);

                    le.getWorld().spawnParticle(
                            Particle.CAMPFIRE_COSY_SMOKE,
                            loc,
                            2, 0.05, 0.05, 0.05, 0.01
                    );
                }
            }

        }, 0L, 5L);

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1f, 0.4f);
    }

    // ============================================================
    // GEAR 3 — Smoke Teleport + Invis + Speed + Heal
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

        player.sendMessage(ChatColor.DARK_GRAY + "☁ Smoke Teleport activated!");

        Location origin = player.getLocation();
        World world = player.getWorld();

        // Smoke burst at origin
        world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, origin, 200, 1.5, 1.5, 1.5, 0.02);
        world.playSound(origin, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 0.5f);

        Location safeLoc = null;

        for (int i = 0; i < 20; i++) {

            double dx = (Math.random() * 20) - 10;
            double dz = (Math.random() * 20) - 10;

            Location candidate = origin.clone().add(dx, 0, dz);

            int y = world.getHighestBlockYAt(candidate);
            candidate.setY(y + 1);

            if (candidate.getBlock().getType().isSolid()) continue;
            if (candidate.clone().add(0, 1, 0).getBlock().getType().isSolid()) continue;
            if (candidate.clone().add(0, -1, 0).getBlock().getType() == Material.LAVA) continue;

            safeLoc = candidate;
            break;
        }

        if (safeLoc == null) {
            player.sendMessage(ChatColor.RED + "Teleport failed — no safe location found!");
            return;
        }

        player.teleport(safeLoc);

        // Heal 3 hearts
        double newHealth = Math.min(player.getHealth() + 6.0, player.getMaxHealth());
        player.setHealth(newHealth);

        // Invisibility + Speed II for 7 seconds
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 7, 0, false, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 7, 1, false, false, true));

        // Smoke implosion at destination
        world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, safeLoc, 200, 1.5, 1.5, 1.5, 0.02);
        world.playSound(safeLoc, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 0.5f);
    }

    // ============================================================
    // GEAR 4 — Smoke Wall Blast
    // ============================================================
    @Override
    public void activateGear4(Player player) {

        CookieManager cm = PowerCookiezMAIN.getInstance().getCookieManager();
        int gearId = 4;
        long cooldownMs = 15000;

        if (cm.isGearOnCooldown(player, gearId, cooldownMs)) {
            player.sendMessage(ChatColor.RED + "Gear 4 is on cooldown!");
            return;
        }
        cm.setGearCooldown(player, gearId);

        player.sendMessage(ChatColor.DARK_GRAY + "☁ Smoke Wall Blast activated!");

        World world = player.getWorld();
        Location origin = player.getLocation();
        Vector dir = origin.getDirection().normalize();

        Location wallCenter = origin.clone().add(dir.clone().multiply(3));

        int width = 7;
        int height = 5;

        List<Location> wallBlocks = new ArrayList<>();
        Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();

        // Build wall (particles only)
        for (int x = -width/2; x <= width/2; x++) {
            for (int y = 0; y < height; y++) {

                Location loc = wallCenter.clone()
                        .add(right.clone().multiply(x))
                        .add(0, y, 0);

                wallBlocks.add(loc);

                world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 10, 0.25, 0.25, 0.25, 0.01);

                if (x == -width/2 || x == width/2 || y == 0 || y == height-1) {
                    world.spawnParticle(Particle.LARGE_SMOKE, loc, 4, 0.1, 0.1, 0.1, 0.01);
                }
            }
        }

        // Static damage for 1.5s
        BukkitRunnable staticTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Entity e : world.getNearbyEntities(wallCenter, 4, 4, 4)) {
                    if (e instanceof Player && e != player) {
                        ((Player) e).damage(8.0, player);
                    }
                }
            }
        };
        staticTask.runTaskTimer(PowerCookiezMAIN.getInstance(), 0L, 5L);

        // After 1.5s → move wall
        new BukkitRunnable() {
            @Override
            public void run() {
                staticTask.cancel();
                world.playSound(wallCenter, Sound.ENTITY_ENDER_DRAGON_FLAP, 1.2f, 1.8f);
                moveSmokeWall(player, wallBlocks, dir);
            }
        }.runTaskLater(PowerCookiezMAIN.getInstance(), 30L);
    }

    private void moveSmokeWall(Player player, List<Location> wallBlocks, Vector dir) {

        World world = player.getWorld();
        final int[] distance = {0};

        new BukkitRunnable() {
            @Override
            public void run() {

                distance[0]++;

                if (distance[0] >= 35) {

                    Location explosionLoc = wallBlocks.get(0);

                    for (Entity e : world.getNearbyEntities(explosionLoc, 4, 4, 4)) {
                        if (e instanceof Player && e != player) {
                            ((Player) e).damage(10.0, player);
                        }
                    }

                    world.spawnParticle(Particle.LARGE_SMOKE,
                            explosionLoc, 20, 1, 1, 1, 0.1);

                    world.playSound(explosionLoc,
                            Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.6f);

                    cancel();
                    return;
                }

                for (Location loc : wallBlocks) {

                    loc.add(dir.clone().multiply(0.4));

                    world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,
                            loc, 10, 0.25, 0.25, 0.25, 0.01);

                    world.spawnParticle(Particle.LARGE_SMOKE,
                            loc, 3, 0.1, 0.1, 0.1, 0.01);

                    for (Entity e : world.getNearbyEntities(loc, 1.2, 2, 1.2)) {
                        if (e instanceof Player && e != player) {
                            ((Player) e).damage(8.0, player);
                        }
                    }
                }
            }
        }.runTaskTimer(PowerCookiezMAIN.getInstance(), 0L, 2L);
    }

    // ============================================================
    // GEAR 5 — Smoke Hands (Grab + Drag + Damage)
    // ============================================================
    @Override
    public void activateGear5(Player player) {

        CookieManager cm = PowerCookiezMAIN.getInstance().getCookieManager();
        int gearId = 5;
        long cooldownMs = 20000;

        if (cm.isGearOnCooldown(player, gearId, cooldownMs)) {
            player.sendMessage(ChatColor.RED + "Gear 5 is on cooldown!");
            return;
        }
        cm.setGearCooldown(player, gearId);

        player.sendMessage(ChatColor.DARK_GRAY + "☁ Smoke Control activated! Look at enemies to grab them...");

        World world = player.getWorld();
        List<LivingEntity> controlled = new ArrayList<>();

        // Drag task
        BukkitRunnable dragTask = new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {

                ticks++;

                if (ticks >= 240) {
                    player.sendMessage(ChatColor.DARK_GRAY + "☁ Smoke Control faded...");
                    this.cancel();
                    return;
                }

                Entity target = player.getTargetEntity(15);

                if (target instanceof LivingEntity le) {
                    if (!controlled.contains(le) && controlled.size() < 3 && le != player) {

                        controlled.add(le);

                        world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,
                                le.getLocation().add(0, 1, 0),
                                40, 0.4, 0.4, 0.4, 0.01);

                        world.spawnParticle(Particle.LARGE_SMOKE,
                                le.getLocation().add(0, 1, 0),
                                20, 0.2, 0.2, 0.2, 0.01);

                        world.playSound(le.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1f, 0.7f);
                    }
                }

                for (Iterator<LivingEntity> it = controlled.iterator(); it.hasNext();) {
                    LivingEntity le = it.next();

                    if (le.isDead() || !le.isValid()) {
                        it.remove();
                        continue;
                    }

                    Location base = player.getLocation();
                    Vector dir = base.getDirection().normalize();

                    Location targetLoc = base.clone()
                            .add(dir.multiply(5))
                            .add(0, 1.5, 0);

                    le.teleport(targetLoc);

                    // Smoke trail
                    world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,
                            targetLoc, 10, 0.3, 0.3, 0.3, 0.01);

                    world.spawnParticle(Particle.LARGE_SMOKE,
                            targetLoc, 4, 0.1, 0.1, 0.1, 0.01);
                }
            }
        };

        dragTask.runTaskTimer(PowerCookiezMAIN.getInstance(), 0L, 3L);

        // DAMAGE TASK (every 2 seconds)
        BukkitRunnable damageTask = new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {

                ticks++;

                if (ticks >= 6) { // 6 * 2s = 12s
                    this.cancel();
                    return;
                }

                for (Iterator<LivingEntity> it = controlled.iterator(); it.hasNext();) {
                    LivingEntity le = it.next();

                    if (le.isDead() || !le.isValid()) {
                        it.remove();
                        continue;
                    }

                    // Damage 3 hearts
                    le.damage(6.0, player);

                    // Smoke burst
                    world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,
                            le.getLocation().add(0, 1, 0),
                            30, 0.3, 0.3, 0.3, 0.01);

                    world.spawnParticle(Particle.LARGE_SMOKE,
                            le.getLocation().add(0, 1, 0),
                            10, 0.1, 0.1, 0.1, 0.01);
                }
            }
        };

        damageTask.runTaskTimer(PowerCookiezMAIN.getInstance(), 40L, 40L); // every 2 seconds
    }

    // ============================================================
    // GEAR 6 — Smoke Black Hole
    // ============================================================
    @Override
    public void activateGear6(Player player) {

        CookieManager cm = PowerCookiezMAIN.getInstance().getCookieManager();
        int gearId = 6;
        long cooldownMs = 45000;

        if (cm.isGearOnCooldown(player, gearId, cooldownMs)) {
            player.sendMessage(ChatColor.RED + "Gear 6 is on cooldown!");
            return;
        }
        cm.setGearCooldown(player, gearId);

        World world = player.getWorld();
        Location center = player.getLocation().clone();

        player.sendMessage(ChatColor.DARK_GRAY + "☁ Smoke Black Hole unleashed!");

        // Initial smoke burst
        world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,
                center, 120, 1.5, 1.5, 1.5, 0.01);

        world.spawnParticle(Particle.LARGE_SMOKE,
                center, 80, 1.2, 1.2, 1.2, 0.01);

        world.playSound(center, Sound.ENTITY_WITHER_SPAWN, 1f, 0.6f);

        // BLACK HOLE TASK — Pull + Float + Smoke (4 seconds)
        BukkitRunnable blackHoleTask = new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {

                ticks++;

                if (ticks >= 80) { // 4 seconds
                    this.cancel();
                    doFinalImplosion(player, center);
                    return;
                }

                // Smoke swirl
                world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,
                        center, 40, 1.2, 1.2, 1.2, 0.01);

                world.spawnParticle(Particle.LARGE_SMOKE,
                        center, 20, 0.8, 0.8, 0.8, 0.01);

                // Pull entities
                for (Entity e : world.getNearbyEntities(center, 15, 15, 15)) {

                    if (!(e instanceof LivingEntity le)) continue;
                    if (le == player) continue;

                    if (le.isDead() || !le.isValid()) continue;

                    Location eloc = le.getLocation();
                    Vector pull = center.toVector().subtract(eloc.toVector());

                    pull.normalize().multiply(0.4);
                    pull.setY(0.25);

                    le.setVelocity(pull);

                    // Smoke around entity
                    world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,
                            eloc.add(0, 1, 0), 15, 0.3, 0.3, 0.3, 0.01);

                    world.spawnParticle(Particle.LARGE_SMOKE,
                            eloc, 6, 0.2, 0.2, 0.2, 0.01);

                    // Damage every 10 ticks
                    if (ticks % 10 == 0) {
                        le.damage(2.0, player);
                    }
                }
            }
        };

        blackHoleTask.runTaskTimer(PowerCookiezMAIN.getInstance(), 0L, 3L);
    }

    private void doFinalImplosion(Player player, Location center) {

        World world = center.getWorld();

        // Massive smoke burst
        world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,
                center, 200, 2.5, 2.5, 2.5, 0.01);

        world.spawnParticle(Particle.LARGE_SMOKE,
                center, 120, 2, 2, 2, 0.01);

        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.4f, 0.5f);

        // Final damage burst
        for (Entity e : world.getNearbyEntities(center, 6, 6, 6)) {

            if (!(e instanceof LivingEntity le)) continue;
            if (le == player) continue;

            if (le.isDead() || !le.isValid()) continue;

            le.damage(10.0, player);

            world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,
                    le.getLocation().add(0, 1, 0),
                    40, 0.4, 0.4, 0.4, 0.01);

            world.spawnParticle(Particle.LARGE_SMOKE,
                    le.getLocation(),
                    20, 0.2, 0.2, 0.2, 0.01);
        }

        player.sendMessage(ChatColor.DARK_GRAY + "☁ The Smoke Black Hole imploded!");
    }

    // ============================================================
    // MAX GEARS
    // ============================================================
    @Override
    public int getMaxGears() {
        return 6;
    }

    // ============================================================
    // GEAR DESCRIPTIONS
    // ============================================================
    @Override
    public String getGearDescription(int gear) {
        return switch (gear) {
            case 1 -> "☁ Smoke Levitation — Fly + darkness burst.";
            case 2 -> "🕳 Smoke Cage — Trap + poison + DoT.";
            case 3 -> "💨 Smoke Teleport — Blink + invisibility + heal.";
            case 4 -> "🌫 Smoke Wall Blast — Expanding smoke wall that damages enemies.";
            case 5 -> "✋ Smoke Hands — Grab, drag and crush enemies.";
            case 6 -> "⚫ Smoke Black Hole — Pull, float and implode enemies.";
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
            case 4 -> 15000;
            case 5 -> 20000;
            case 6 -> 45000;
            default -> 0;
        };
    }
}
