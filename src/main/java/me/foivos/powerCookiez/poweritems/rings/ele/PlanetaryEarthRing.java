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

public class PlanetaryEarthRing implements RingPower {

    private final Map<UUID, Long> passiveCooldown = new HashMap<>();

    @Override
    public String getName() {
        return "PlanetaryEarthRing";
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
        ItemStack item = new ItemStack(Material.MOSS_BLOCK);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.DARK_GREEN + "Planetary Earth ELE‑Ring");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "A mighty ring forged from the core of the planet.",
                "",
                ChatColor.GOLD + "Category: " + ChatColor.YELLOW + "> ELEMENTAL <",
                ChatColor.GOLD + "Rarity: " + ChatColor.RED + "Mythic",
                "",
                ChatColor.GOLD + "PASSIVES:",
                ChatColor.GRAY + "🪨 Stone Skin (+2 Armor)",
                ChatColor.GRAY + "🪨 Earth Stability (No Knockback)",
                "",
                ChatColor.GOLD + "ABILITIES:",
                ChatColor.GRAY + "A — Earth Slam",
                ChatColor.GRAY + "B — Rock Shield",
                ChatColor.GRAY + "C — Boulder Throw",
                ChatColor.GRAY + "D — Earthquake"
        ));


        meta.getPersistentDataContainer().set(
                new NamespacedKey(PowerCookiezMAIN.getInstance(), "ringName"),
                PersistentDataType.STRING,
                "PlanetaryEarthRing"
        );

        meta.setCustomModelData(104);
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

        // Stone Skin — +2 armor
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.RESISTANCE,
                40, 0, true, false, false
        ));

        // Earth Stability — no knockback
        player.setVelocity(player.getVelocity().multiply(1)); // prevents knockback

        // Earth pulse every 4 seconds
        long now = System.currentTimeMillis();
        long last = passiveCooldown.getOrDefault(player.getUniqueId(), 0L);

        if (now - last >= 4000) {
            passiveCooldown.put(player.getUniqueId(), now);

            World world = player.getWorld();
            Location loc = player.getLocation();

            world.spawnParticle(Particle.BLOCK, loc, 40, 1, 0.5, 1, 0.1, Material.DIRT.createBlockData());
            world.playSound(loc, Sound.BLOCK_STONE_BREAK, 0.7f, 0.8f);
        }
    }

    // ============================================================
    // ABILITY A — Earth Slam
    // ============================================================
    @Override
    public void abilityA(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;
        if (!rm.isRingEnabled(player)) return;
        if (RingManager.isCooldown(player, "A", 6000)) return; // 6s cooldown

        World world = player.getWorld();
        Location loc = player.getLocation();

        world.spawnParticle(Particle.BLOCK, loc, 100, 2, 1, 2, 0.1, Material.STONE.createBlockData());
        world.playSound(loc, Sound.BLOCK_ANVIL_LAND, 1f, 0.7f);

        for (Entity e : world.getNearbyEntities(loc, 5, 4, 5)) {
            if (e instanceof LivingEntity le && le != player) {

                le.damage(6.0, player);
                le.setVelocity(new Vector(0, 0.6, 0));

                world.spawnParticle(Particle.BLOCK, le.getLocation(), 40, 0.5, 1, 0.5, 0.1, Material.DIRT.createBlockData());
            }
        }
    }

    // ============================================================
    // ABILITY B — Rock Shield
    // ============================================================
    @Override
    public void abilityB(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;
        if (!rm.isRingEnabled(player)) return;
        if (RingManager.isCooldown(player, "B", 7000)) return; // 7s cooldown

        World world = player.getWorld();
        Location loc = player.getLocation();

        // Absorption hearts
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.ABSORPTION,
                100, 2, true, false, false
        ));

        world.spawnParticle(Particle.BLOCK, loc, 80, 1.5, 1, 1.5, 0.1, Material.STONE.createBlockData());
        world.playSound(loc, Sound.BLOCK_STONE_PLACE, 1f, 0.8f);
    }

    // ============================================================
    // ABILITY C — Boulder Throw
    // ============================================================
    @Override
    public void abilityC(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;
        if (!rm.isRingEnabled(player)) return;
        if (RingManager.isCooldown(player, "C", 8000)) return; // 8s cooldown

        World world = player.getWorld();
        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        ArmorStand rock = world.spawn(eye, ArmorStand.class, stand -> {
            stand.setInvisible(true);
            stand.setMarker(true);
            stand.setGravity(false);
        });

        Bukkit.getScheduler().runTaskTimer(PowerCookiezMAIN.getInstance(), task -> {

            if (!rock.isValid()) { task.cancel(); return; }

            rock.teleport(rock.getLocation().add(dir.clone().multiply(1)));

            world.spawnParticle(Particle.BLOCK, rock.getLocation(), 20, 0.3, 0.3, 0.3, 0.1, Material.STONE.createBlockData());

            for (Entity e : world.getNearbyEntities(rock.getLocation(), 1.2, 1.2, 1.2)) {
                if (e instanceof LivingEntity le && le != player) {

                    le.damage(8.0, player);
                    le.setVelocity(dir.clone().multiply(1.2));

                    rock.remove();
                    task.cancel();
                    return;
                }
            }

        }, 0L, 1L);
    }

    // ============================================================
    // ABILITY D — Earthquake
    // ============================================================
    @Override
    public void abilityD(Player player) {

        RingManager rm = PowerCookiezMAIN.getInstance().getRingManager();
        if (!rm.isHoldingRing(player)) return;
        if (!rm.isRingEnabled(player)) return;
        if (RingManager.isCooldown(player, "D", 10000)) return; // 10s cooldown

        World world = player.getWorld();
        Location center = player.getLocation();

        player.sendMessage(ChatColor.DARK_GREEN + "🌍 Earthquake unleashed!");

        // 3-second earthquake
        Bukkit.getScheduler().runTaskTimer(PowerCookiezMAIN.getInstance(), task -> {

            if (!player.isOnline()) { task.cancel(); return; }

            world.spawnParticle(Particle.BLOCK, center, 200, 4, 1, 4, 0.1, Material.DIRT.createBlockData());
            world.playSound(center, Sound.BLOCK_STONE_BREAK, 1f, 0.6f);

            for (Entity e : world.getNearbyEntities(center, 7, 5, 7)) {
                if (e instanceof LivingEntity le && le != player) {

                    le.damage(4.0, player);

                    Vector shake = le.getLocation().toVector().subtract(center.toVector()).normalize().multiply(0.6);
                    shake.setY(0.4);
                    le.setVelocity(shake);
                }
            }

        }, 0L, 10L); // 3 seconds
    }
}
