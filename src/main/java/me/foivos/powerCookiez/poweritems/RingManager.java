package me.foivos.powerCookiez.poweritems;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import me.foivos.powerCookiez.poweritems.rings.RingPower;
import me.foivos.powerCookiez.poweritems.rings.ani.FoxyFoxRing;
import me.foivos.powerCookiez.poweritems.rings.ani.ShadowyDragonRing;
import me.foivos.powerCookiez.poweritems.rings.ani.WolfyWolfRing;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class RingManager {

    // ACTIVE RINGS
    private static final Map<UUID, RingPower> activeRings = new HashMap<>();

    // REGISTERED RINGS
    private static final Map<String, RingPower> ringRegistry = new HashMap<>();

    // COOLDOWNS
    private static final Map<String, Map<UUID, Long>> abilityCooldowns = new HashMap<>();

    // ENABLE / DISABLE
    private static final Map<UUID, Boolean> ringEnabled = new HashMap<>();

    // TASK TRACKING
    private static final Map<UUID, List<Integer>> runningTasks = new HashMap<>();


    // ============================================================
    // ACTIVE RING GET/SET
    // ============================================================
    public static void setPlayerRing(Player player, RingPower ring) {

        // STOP OLD RING EFFECTS
        stopAllRingEffects(player);

        // SET NEW RING
        activeRings.put(player.getUniqueId(), ring);
    }

    public static RingPower getActiveRing(Player player) {
        return activeRings.get(player.getUniqueId());
    }


    // ============================================================
    // ENABLE / DISABLE
    // ============================================================
    public static boolean isRingEnabled(Player p) {
        return ringEnabled.getOrDefault(p.getUniqueId(), true);
    }

    public void toggleRing(Player p) {
        boolean current = isRingEnabled(p);
        ringEnabled.put(p.getUniqueId(), !current);

        // If disabling → stop everything
        if (!ringEnabled.get(p.getUniqueId())) {
            stopAllRingEffects(p);
        }
    }

    public static void disableRing(Player p) {
        if ( !isRingEnabled(p)) return;

        ringEnabled.put(p.getUniqueId(), false);

        stopAllRingEffects(p);
    }

    // ============================================================
    // REGISTER RINGS
    // ============================================================
    public static Collection<RingPower> getRegisteredRings() {
        return ringRegistry.values();
    }

    public static void registerRing(RingPower ring) {
        ringRegistry.put(ring.getName(), ring);

        abilityCooldowns.put(ring.getName() + "_A", new HashMap<>());
        abilityCooldowns.put(ring.getName() + "_B", new HashMap<>());
        abilityCooldowns.put(ring.getName() + "_C", new HashMap<>());
        abilityCooldowns.put(ring.getName() + "_D", new HashMap<>());
    }


    // ============================================================
    // PASSIVE LOOP
    // ============================================================
    public static void startPassiveLoop(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> {
                    for (Player p : Bukkit.getOnlinePlayers()) {

                        RingPower ring = getActiveRing(p);

                        if (ring != null &&
                                PowerCookiezMAIN.getInstance().getRingManager().isHoldingRing(p) &&
                                isRingEnabled(p)) {

                            ring.applyPassives(p);
                        }
                    }
                },
                20L, 20L
        );
    }


    // ============================================================
    // COOLDOWNS
    // ============================================================
    public static boolean checkCooldown(Player player, String ability, long cdMs) {

        RingPower ring = getActiveRing(player);
        if (ring == null) {
            player.sendMessage("§cYou have no active ring!");
            return false;
        }

        String key = ring.getName() + "_" + ability;
        Map<UUID, Long> map = abilityCooldowns.get(key);

        long now = System.currentTimeMillis();
        long last = map.getOrDefault(player.getUniqueId(), 0L);

        if (now - last < cdMs) {
            long left = (cdMs - (now - last)) / 1000;
            player.sendMessage("§cAbility on cooldown: " + left + "s");
            return false;
        }

        map.put(player.getUniqueId(), now);
        return true;
    }


    // ============================================================
    // DISPLAY ITEM FOR /myring
    // ============================================================
    public ItemStack getPlayerRingItem(Player p) {
        RingPower ring = getActiveRing(p);
        if (ring == null) return null;
        return ring.getDisplayItem();
    }


    // ============================================================
    // CHECK IF PLAYER IS HOLDING A RING
    // ============================================================
    public boolean isHoldingRing(Player p) {
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta()) return false;

        return item.getItemMeta().getPersistentDataContainer().has(
                new NamespacedKey(PowerCookiezMAIN.getInstance(), "ringName"),
                PersistentDataType.STRING
        );
    }

    public String getHeldRingName(Player p) {
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta()) return null;

        return item.getItemMeta().getPersistentDataContainer().get(
                new NamespacedKey(PowerCookiezMAIN.getInstance(), "ringName"),
                PersistentDataType.STRING
        );
    }

    public RingPower getHeldRing(Player p) {
        String name = getHeldRingName(p);
        if (name == null) return null;
        return ringRegistry.get(name);
    }


    // ============================================================
    // TASK TRACKING
    // ============================================================
    public static void registerTask(Player p, int taskId) {
        runningTasks.computeIfAbsent(p.getUniqueId(), k -> new ArrayList<>()).add(taskId);
    }


    // ============================================================
    // FULL RING CLEANUP
    // ============================================================
    public static void stopAllRingEffects(Player p) {

        UUID id = p.getUniqueId();

        // 1) Cancel all running tasks
        List<Integer> tasks = runningTasks.get(id);
        if (tasks != null) {
            for (int taskId : tasks) {
                Bukkit.getScheduler().cancelTask(taskId);
            }
            tasks.clear();
        }

        // 2) Remove models (Dragon, Fox, Wolf)
        RingPower ring = getActiveRing(p);

        if (ring instanceof ShadowyDragonRing dr) {
            dr.getModelManager().removeDragon(p);
        }
        if (ring instanceof FoxyFoxRing fx) {
            fx.getFoxModelManager().removeFox(p);
        }
        if (ring instanceof WolfyWolfRing wf) {
            wf.getWolfModelManager().removeWolf(p);
        }

        // 3) Remove invisibility
        p.removePotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY);

        // 4) Clear particles visually
        p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 20, 0.5, 0.5, 0.5, 0.01);
    }
}
