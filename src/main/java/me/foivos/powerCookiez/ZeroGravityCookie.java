package me.foivos.powerCookiez;

import org.bukkit.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.Bukkit;

import java.util.*;

public class ZeroGravityCookie implements CookiePower {

    public static final Set<UUID> ZeroGravityNoFall = new HashSet<>();
    private final Map<UUID, Entity> grabbedTarget = new HashMap<>();
    private final Map<UUID, Long> grabExpire = new HashMap<>();
    private final Map<UUID, Long> lastDamage = new HashMap<>();

    // ============================================================
    // BASIC INFO
    // ============================================================
    @Override
    public String getName() {
        return "ZeroGravityCookie";
    }

    @Override
    public String getDescription() {
        return "Anti-gravity powers, shockwaves, grabs, zero-G jumps and aerial control.";
    }

    @Override
    public Material getDisplayMaterial() {
        return Material.FEATHER;
    }

    // ============================================================
    // DISPLAY ITEM
    // ============================================================
    @Override
    public ItemStack getDisplayItem() {
        ItemStack item = new ItemStack(Material.COOKIE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.AQUA + "ZeroGravity Cookie");
        meta.setLore(Arrays.asList(
                ChatColor.WHITE + "Right-click to consume",
                ChatColor.GRAY + "(Custom Power Cookie)",
                "",
                ChatColor.AQUA + "PASSIVE EFFECTS:",
                ChatColor.GRAY + "🛡 Fall Damage Immunity",
                ChatColor.GRAY + "💨 Permanent Speed I",
                "",
                ChatColor.RED + "DISADVANTAGE:",
                ChatColor.GRAY + "💧 Takes 1 Heart Damage per Second in Water"
        ));

        meta.getPersistentDataContainer().set(
                new NamespacedKey(PowerCookiezMAIN.getInstance(), "cookieName"),
                PersistentDataType.STRING,
                "ZeroGravityCookie"
        );

        meta.setCustomModelData(3);
        item.setItemMeta(meta);
        return item;
    }

    // ============================================================
    // BASE ACTIVATION (when eaten)
    // ============================================================
    @Override
    public void activate(Player player) {

        // Αν ο παίκτης έχει ήδη το cookie, δεν χρειάζεται να ξαναενεργοποιηθεί
        if (CookieManager.hasCookie(player, "ZeroGravityCookie")) {
            player.sendMessage("§b[ZeroGravity] §7Your cookie is already active!");
            return;
        }

        // Κάνουμε register ότι ο παίκτης έχει πλέον το ZeroGravity Cookie
        CookieManager.giveCookie(player, "ZeroGravityCookie");

        // Μικρό effect για feedback
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 1.6f);
        player.spawnParticle(Particle.CLOUD, player.getLocation().add(0, 1, 0), 20, 0.3, 0.5, 0.3, 0.01);

        // Μήνυμα ενεργοποίησης
        player.sendMessage("§b§lZeroGravity Activated! §7You feel weightless and agile!");

        // Προαιρετικό: δώσε ένα μικρό temporary boost όταν το τρώει
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.LEVITATION,
                20,  // 1 second
                0,
                true, false, false
        ));
    }

    // ============================================================
    // GEAR 1 — Gravity Push
    // ============================================================
    @Override
    public void activateGear1(Player player) {

        // Cooldown check
        CookieManager cm = PowerCookiezMAIN.getInstance().getCookieManager();
        int gearId = 1;
        long cooldownMs = 20000; // 20 seconds cooldown

        if (cm.isGearOnCooldown(player, gearId, cooldownMs)) {
            player.sendMessage(ChatColor.RED + "Gear 1 is on cooldown!");
            return;
        }
        cm.setGearCooldown(player, gearId);

        World world = player.getWorld();
        Location center = player.getLocation().clone();

        player.sendMessage(ChatColor.AQUA + "☄ Gravity Push unleashed!");

        // LAYER 1 — Core Flash
        world.spawnParticle(
                Particle.FLASH,
                center,
                1, 0, 0, 0, 0
        );

        // LAYER 2 — Shockwave Ring
        world.spawnParticle(
                Particle.SWEEP_ATTACK,
                center,
                40,
                2.5, 0.1, 2.5,
                0.01
        );

        // LAYER 3 — Anti‑Gravity Burst
        world.spawnParticle(
                Particle.END_ROD,
                center,
                200,
                3.0, 2.0, 3.0,
                0.05
        );

        // LAYER 4 — Gravity Cloud
        world.spawnParticle(
                Particle.CLOUD,
                center,
                120,
                2.5, 1.5, 2.5,
                0.08
        );

        // BONUS — Explosion emitter
        world.spawnParticle(
                Particle.EXPLOSION_EMITTER,
                center,
                1, 0, 0, 0, 0
        );

        // SOUNDS
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.6f, 0.6f);
        world.playSound(center, Sound.BLOCK_BEACON_POWER_SELECT, 1.4f, 1.8f);
        world.playSound(center, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.7f, 1.2f);

        // EFFECT ON ENTITIES
        for (Entity e : world.getNearbyEntities(center, 15, 15, 15)) {

            if (!(e instanceof LivingEntity le)) continue;
            if (le == player) continue;
            if (le.isDead() || !le.isValid()) continue;

            Vector dir = le.getLocation().toVector().subtract(center.toVector());
            dir.normalize().multiply(2.2);
            dir.setY(0.6);

            le.setVelocity(dir);

            // Damage (5 hearts)
            le.damage(10.0, player);

            // Weakness 15s
            le.addPotionEffect(new PotionEffect(
                    PotionEffectType.WEAKNESS,
                    300,
                    0,
                    true, false, false
            ));

            // Extra particles
            world.spawnParticle(Particle.END_ROD,
                    le.getLocation().add(0, 1, 0),
                    25, 0.3, 0.6, 0.3, 0.05);

            world.spawnParticle(Particle.CLOUD,
                    le.getLocation(),
                    15, 0.4, 0.4, 0.4, 0.02);
        }
    }

    // ============================================================
    // GEAR 2 — Gravy Graber
    // ============================================================
    @Override
    public void activateGear2(Player player) {
        CookieManager cm = PowerCookiezMAIN.getInstance().getCookieManager();
        int gearId = 2;
        long cooldownMs = 30000; // 30 seconds cooldown

        if (cm.isGearOnCooldown(player, gearId, cooldownMs)) {
            player.sendMessage(ChatColor.RED + "Gear 2 is on cooldown!");
            return;
        }

        // 1) RAYCAST — FIND TARGET
        Entity target = null;

        // First: try LivingEntities
        for (Entity e : player.getNearbyEntities(20, 20, 20)) {
            if (e instanceof LivingEntity le && le != player) {
                if (player.hasLineOfSight(le)) {
                    Vector dir = player.getLocation().getDirection();
                    Vector to = le.getLocation().toVector().subtract(player.getEyeLocation().toVector());
                    if (dir.normalize().dot(to.normalize()) > 0.97) {
                        target = le;
                        break;
                    }
                }
            }
        }

        // Second: try items on ground
        if (target == null) {
            for (Entity e : player.getNearbyEntities(20, 20, 20)) {
                if (e instanceof org.bukkit.entity.Item item) {
                    if (player.hasLineOfSight(item)) {
                        Vector dir = player.getLocation().getDirection();
                        Vector to = item.getLocation().toVector().subtract(player.getEyeLocation().toVector());
                        if (dir.normalize().dot(to.normalize()) > 0.97) {
                            target = item;
                            break;
                        }
                    }
                }
            }
        }

        if (target == null) {
            player.sendMessage(ChatColor.GRAY + "Δεν βρήκες κάτι να αρπάξεις...");
            return;
        }

        // 2) REGISTER GRAB
        grabbedTarget.put(player.getUniqueId(), target);
        grabExpire.put(player.getUniqueId(), System.currentTimeMillis() + 30000);

        player.sendMessage(ChatColor.AQUA + "☄ Gravy Graber engaged!");
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.8f);

        final Entity grabbed = target;

        // 3) START GRAB LOOP
        Bukkit.getScheduler().runTaskTimer(PowerCookiezMAIN.getInstance(), task -> {

            if (!grabbedTarget.containsKey(player.getUniqueId())) {
                task.cancel();
                return;
            }

            long expire = grabExpire.get(player.getUniqueId());
            if (System.currentTimeMillis() >= expire) {
                throwEntity(player, grabbed);
                task.cancel();
                cm.setGearCooldown(player, gearId);
                return;
            }

            if (!grabbed.isValid()) {
                grabbedTarget.remove(player.getUniqueId());
                task.cancel();
                return;
            }

            // HOLD ENTITY IN FRONT OF PLAYER
            Location eye = player.getEyeLocation();
            Vector forward = eye.getDirection().normalize();

            Location holdPos = eye.add(forward.multiply(3));
            holdPos.setY(holdPos.getY() + 0.5);

            grabbed.teleport(holdPos);

            // DAMAGE + NAUSEA
            if (grabbed instanceof LivingEntity le) {

                long now = System.currentTimeMillis();
                long last = lastDamage.getOrDefault(grabbed.getUniqueId(), 0L);

                if (now - last >= 1000) {
                    lastDamage.put(grabbed.getUniqueId(), now);
                    le.damage(2.0, player);
                    le.addPotionEffect(new PotionEffect(
                            PotionEffectType.NAUSEA,
                            200,
                            0,
                            true, false, false
                    ));
                }
            }

            // PARTICLE BEAM
            drawParticleBeam(player.getEyeLocation(), grabbed.getLocation().add(0, 0.5, 0));

        }, 0L, 1L);
    }

    // THROW FUNCTION
    private void throwEntity(Player player, Entity grabbed) {

        if (grabbed == null || !grabbed.isValid()) return;

        Vector dir = player.getLocation().getDirection().normalize().multiply(2.5);
        dir.setY(0.4);

        grabbed.setVelocity(dir);

        if (grabbed instanceof LivingEntity le) {
            le.damage(8.0, player);
        }

        player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, grabbed.getLocation(), 1);
        player.getWorld().playSound(grabbed.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);

        grabbedTarget.remove(player.getUniqueId());
    }

    // PARTICLE BEAM
    private void drawParticleBeam(Location from, Location to) {

        World world = from.getWorld();
        Vector diff = to.toVector().subtract(from.toVector());
        double length = diff.length();
        Vector step = diff.normalize().multiply(0.2);

        Vector current = from.toVector().clone();

        for (double d = 0; d < length; d += 0.2) {
            world.spawnParticle(
                    Particle.END_ROD,
                    current.getX(), current.getY(), current.getZ(),
                    1, 0, 0, 0, 0
            );
            current.add(step);
        }
    }

    // ============================================================
    // GEAR 3 — Zero‑G Jump
    // ============================================================
    @Override
    public void activateGear3(Player player) {

        CookieManager cm = PowerCookiezMAIN.getInstance().getCookieManager();
        int gearId = 3;
        long cooldownMs = 30000;

        if (cm.isGearOnCooldown(player, gearId, cooldownMs)) {
            player.sendMessage(ChatColor.RED + "Gear 3 is on cooldown!");
            return;
        }

        World world = player.getWorld();
        Location center = player.getLocation();

        player.sendMessage(ChatColor.AQUA + "☄ Zero‑G Jump initiated!");
        player.playSound(center, Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.8f);

        // 1) FIND ENTITIES
        List<Entity> affected = new ArrayList<>();

        for (Entity e : world.getNearbyEntities(center, 20, 20, 20)) {
            if (e == player) continue;
            if (!e.isValid()) continue;
            affected.add(e);
        }

        // 2) INITIAL EXPLOSION + DAMAGE + LEVITATION
        for (Entity e : affected) {

            world.spawnParticle(Particle.EXPLOSION_EMITTER, e.getLocation(), 1);
            world.spawnParticle(Particle.CLOUD, e.getLocation(), 40, 0.6, 0.6, 0.6, 0.05);
            world.spawnParticle(Particle.END_ROD, e.getLocation(), 40, 0.4, 0.8, 0.4, 0.02);

            world.playSound(e.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.7f);

            if (e instanceof LivingEntity le) {
                le.damage(10.0, player);

                le.addPotionEffect(new PotionEffect(
                        PotionEffectType.LEVITATION,
                        200,
                        0,
                        true, false, false
                ));
            }

            if (e instanceof org.bukkit.entity.Item item) {
                item.setVelocity(new Vector(0, 0.4, 0));
            }
        }

        // 3) FLOAT ITEMS
        Bukkit.getScheduler().runTaskTimer(PowerCookiezMAIN.getInstance(), task -> {

            for (Entity e : affected) {
                if (!(e instanceof org.bukkit.entity.Item)) continue;
                if (!e.isValid()) continue;

                Vector v = e.getVelocity();
                e.setVelocity(new Vector(0, Math.min(v.getY() + 0.02, 0.35), 0));

                world.spawnParticle(Particle.END_ROD, e.getLocation(), 3, 0.1, 0.2, 0.1, 0.01);
            }

        }, 0L, 1L);

        Bukkit.getScheduler().runTaskLater(PowerCookiezMAIN.getInstance(), () -> {}, 200L);

        // 4) BUFFS TO PLAYER
        double newHealth = Math.min(player.getMaxHealth(), player.getHealth() + 10.0);
        player.setHealth(newHealth);

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED, 300, 1, true, false, false
        ));

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.JUMP_BOOST, 300, 1, true, false, false
        ));

        UUID uid = player.getUniqueId();
        ZeroGravityNoFall.add(uid);
        Bukkit.getScheduler().runTaskLater(PowerCookiezMAIN.getInstance(), () -> {
            ZeroGravityNoFall.remove(uid);
        }, 300L);

        // 5) ZERO‑G JUMP
        Bukkit.getScheduler().runTaskLater(PowerCookiezMAIN.getInstance(), () -> {

            Vector dir = player.getLocation().getDirection().normalize();
            Vector jump = dir.multiply(1.3);
            jump.setY(1.0);

            player.setVelocity(jump);

            world.spawnParticle(Particle.CLOUD, player.getLocation(), 40, 0.5, 0.5, 0.5, 0.05);
            world.spawnParticle(Particle.END_ROD, player.getLocation(), 60, 0.4, 0.8, 0.4, 0.02);
            world.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 1.4f);

        }, 10L);

        // 6) LANDING EXPLOSION
        Bukkit.getScheduler().runTaskLater(PowerCookiezMAIN.getInstance(), () -> {

            Location land = player.getLocation();

            world.spawnParticle(Particle.EXPLOSION_EMITTER, land, 1);
            world.spawnParticle(Particle.CLOUD, land, 80, 1.2, 1.2, 1.2, 0.1);
            world.spawnParticle(Particle.SWEEP_ATTACK, land, 40, 2.5, 0.1, 2.5, 0.01);
            world.playSound(land, Sound.ENTITY_GENERIC_EXPLODE, 1.4f, 0.7f);

            for (Entity e : world.getNearbyEntities(land, 7, 7, 7)) {
                if (e instanceof LivingEntity le && le != player) {
                    le.damage(10.0, player);
                    Vector knock = le.getLocation().toVector().subtract(land.toVector()).normalize().multiply(1.2);
                    knock.setY(0.4);
                    le.setVelocity(knock);
                }
            }

            cm.setGearCooldown(player, gearId);

        }, 30L);
    }
    // ============================================================
    // GEAR 4 - G‑LASER DESTROYER
    // ============================================================
    @Override
    public void activateGear4(Player player) {

        CookieManager cm = PowerCookiezMAIN.getInstance().getCookieManager();
        int gearId = 4;
        long cooldownMs = 30000; // 30 seconds cooldown

        if (cm.isGearOnCooldown(player, gearId, cooldownMs)) {
            player.sendMessage(ChatColor.RED + "Gear 4 is on cooldown!");
            return;
        }
        cm.setGearCooldown(player, gearId);

        player.sendMessage(ChatColor.AQUA + "☄ G‑LASER DESTROYER activated!");

        World world = player.getWorld();

        // ============================================================
        // 1) SPAWN ROTATING CRYING OBSIDIAN ORBS
        // ============================================================
        ArmorStand leftOrb = world.spawn(player.getLocation(), ArmorStand.class, as -> {
            as.setInvisible(true);
            as.setMarker(true);
            as.setGravity(false);
            as.setSmall(true);
            as.setInvulnerable(true);
            as.setBasePlate(false);
            as.getEquipment().setHelmet(new ItemStack(Material.CRYING_OBSIDIAN));
        });

        ArmorStand rightOrb = world.spawn(player.getLocation(), ArmorStand.class, as -> {
            as.setInvisible(true);
            as.setMarker(true);
            as.setGravity(false);
            as.setSmall(true);
            as.setInvulnerable(true);
            as.setBasePlate(false);
            as.getEquipment().setHelmet(new ItemStack(Material.CRYING_OBSIDIAN));
        });

        // ============================================================
        // 2) LASER TASK (15 seconds)
        // ============================================================
        final int[] ticks = {0};

        Bukkit.getScheduler().runTaskTimer(PowerCookiezMAIN.getInstance(), task -> {

            ticks[0]++;

            // Stop after 15 seconds
            if (ticks[0] >= 20 * 15) {
                leftOrb.remove();
                rightOrb.remove();
                task.cancel();
                return;
            }

            // If player invalid → cleanup
            if (!player.isOnline() || player.isDead()) {
                leftOrb.remove();
                rightOrb.remove();
                task.cancel();
                return;
            }

            Location eye = player.getEyeLocation();
            Vector dir = eye.getDirection().normalize();

            // ============================================================
            // 2A) ROTATE ORBS AROUND PLAYER HEAD
            // ============================================================
            double angle = ticks[0] * 0.25;

            double radius = 0.6;
            double yOffset = 1.5;

            Location base = player.getLocation().add(0, yOffset, 0);

            Location leftPos = base.clone().add(
                    Math.cos(angle) * radius,
                    0,
                    Math.sin(angle) * radius
            );

            Location rightPos = base.clone().add(
                    Math.cos(angle + Math.PI) * radius,
                    0,
                    Math.sin(angle + Math.PI) * radius
            );

            leftOrb.teleport(leftPos);
            rightOrb.teleport(rightPos);

            // ============================================================
            // 2B) RAYTRACE FOR LASERS
            // ============================================================
            int maxDistance = 25;

            RayTraceResult result = world.rayTrace(eye, dir, maxDistance,
                    FluidCollisionMode.NEVER, true, 0.1,
                    e -> e != player);

            Location hitLoc = (result != null && result.getHitPosition() != null)
                    ? result.getHitPosition().toLocation(world)
                    : eye.clone().add(dir.clone().multiply(maxDistance));

            // ============================================================
            // 2C) DRAW LASERS FROM BOTH ORBS → hitLoc
            // ============================================================
            drawLaser(world, leftPos, hitLoc);
            drawLaser(world, rightPos, hitLoc);

            // ============================================================
            // 2D) DAMAGE ENTITIES
            // ============================================================
            for (Entity e : world.getNearbyEntities(hitLoc, 1.2, 1.2, 1.2)) {
                if (e instanceof LivingEntity le && le != player) {
                    le.damage(6.0, player); // 3 hearts
                    le.setFireTicks(40);
                }
            }

            // ============================================================
            // 2E) BLOCK DAMAGE (Option A)
            // ============================================================
            Block b = hitLoc.getBlock();

            if (b.getType() == Material.SNOW || b.getType() == Material.SNOW_BLOCK) {
                b.setType(Material.AIR);
            }

            if (b.getType() == Material.ICE || b.getType() == Material.FROSTED_ICE) {
                b.setType(Material.WATER);
            }

            if (b.getType().isFlammable()) {
                b.setType(Material.FIRE);
            }

            // Impact particles
            world.spawnParticle(Particle.FLAME, hitLoc, 10, 0.2, 0.2, 0.2, 0.01);
            world.spawnParticle(Particle.LARGE_SMOKE, hitLoc, 6, 0.2, 0.2, 0.2, 0.01);
            world.spawnParticle(Particle.ELECTRIC_SPARK, hitLoc, 8, 0.2, 0.2, 0.2, 0.01);

        }, 0L, 1L);
    }

    // ============================================================
    // LASER DRAWING FUNCTION (FULL FIXED)
    // ============================================================
    private void drawLaser(World world, Location from, Location to) {

        Vector diff = to.toVector().subtract(from.toVector());
        double length = diff.length();
        Vector step = diff.normalize().multiply(0.3);

        Location point = from.clone();

        for (double d = 0; d < length; d += 0.3) {

            // White core / glow
            world.spawnParticle(Particle.END_ROD, point, 1, 0, 0, 0, 0);

            // Red-ish flame shell
            world.spawnParticle(Particle.FLAME, point, 1, 0, 0, 0, 0.001);

            point.add(step);
        }
    }
    // ============================================================
    // Gear 5 - Fih???
    // ============================================================
    @Override
    public void activateGear5(Player player) {}

    @Override
    public void activateGear6(Player player) {}

    // ============================================================
    // MAX GEARS
    // ============================================================
    @Override
    public int getMaxGears() {
        return 6;
    }

    // ============================================================
    // DESCRIPTIONS
    // ============================================================
    @Override
    public String getGearDescription(int gear) {
        switch (gear) {

            case 1:
                return ChatColor.AQUA + "Gear 1: Gravity Push\n"
                        + ChatColor.GRAY + "➤ Releases a powerful anti‑gravity shockwave\n"
                        + "  that blasts nearby entities away.";

            case 2:
                return ChatColor.AQUA + "Gear 2: Gravy Graber\n"
                        + ChatColor.GRAY + "➤ Use gravitational force to grab a target\n"
                        + "  holding it mid‑air, damaging it over time,\n"
                        + "  and launching it away with a force blast.";

            case 3:
                return ChatColor.AQUA + "Gear 3: Zero‑G Jump\n"
                        + ChatColor.GRAY + "➤ Massive anti‑gravity burst, levitation,\n"
                        + "  buffs and a powerful landing explosion.";

            case 4:
            case 5:
            case 6:
            default:
                return ChatColor.GRAY + "No ability for this gear.";
        }
    }

    // ============================================================
    // COOLDOWNS
    // ============================================================
    @Override
    public long getGearCooldownMs(int gear) {
        switch (gear) {
            case 1: return 20000; // 20s
            case 2: return 30000; // 30s
            case 3: return 30000; // 30s
            case 4: return 40000; // placeholder
            case 5: return 45000; // placeholder
            case 6: return 60000; // placeholder
            default: return 0;
        }
    }
}
