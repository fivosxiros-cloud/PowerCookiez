package me.foivos.powerCookiez;

import me.foivos.powerCookiez.poweritems.RingManager;
import me.foivos.powerCookiez.poweritems.rings.RingPower;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityTriggerListener implements Listener {

    private final PowerCookiezMAIN plugin;

    public AbilityTriggerListener(PowerCookiezMAIN plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, Integer> shiftCount = new HashMap<>();
    private final Map<UUID, Long> lastShift = new HashMap<>();
    private final Map<UUID, Long> shiftHoldStart = new HashMap<>();


    // ============================================================
    // SHIFT EVENT (PRESS + RELEASE + HOLD + COMBOS)
    // ============================================================
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();

        // RING CHECK
        RingPower ring = plugin.getRingManager().getHeldRing(p);
        boolean hasRing = (ring != null && RingManager.isRingEnabled(p));

        // COOKIE CHECK
        String cookie = plugin.getCookieManager().getLastCookieEaten(p);
        boolean hasCookie = (cookie != null);

        // SHIFT RELEASE → RESET
        if (!e.isSneaking()) {
            shiftCount.put(id, 0);
            return;
        }

        // SHIFT HOLD (ONLY FOR COOKIES)
        shiftHoldStart.put(id, System.currentTimeMillis());

        if (hasCookie) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (p.isSneaking()) {
                    long start = shiftHoldStart.getOrDefault(id, 0L);
                    if (System.currentTimeMillis() - start >= 3000) {
                        plugin.getCookieManager().getCookie(cookie).activateGear6(p);
                        shiftCount.put(id, 0);
                    }
                }
            }, 60L);
        }

        // DOUBLE/TRIPLE SHIFT LOGIC
        long now = System.currentTimeMillis();
        long last = lastShift.getOrDefault(id, 0L);

        if (now - last <= 3000) {
            shiftCount.put(id, shiftCount.getOrDefault(id, 0) + 1);
        } else {
            shiftCount.put(id, 1);
        }

        lastShift.put(id, now);

        // TRIPLE SHIFT → GEAR 3
        if (shiftCount.get(id) == 3) {

            if (hasRing) {
                ring.abilityD(p);
                shiftCount.put(id, 0);
                return;
            }

            if (hasCookie) {
                plugin.getCookieManager().getCookie(cookie).activateGear3(p);
                shiftCount.put(id, 0);
            }
        }
    }


    // ============================================================
    // RIGHT CLICK (Gear 1, Gear 4)
    // ============================================================
    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();

        if (!p.isSneaking()) return;

        if (e.getAction() != Action.RIGHT_CLICK_AIR &&
                e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        RingPower ring = plugin.getRingManager().getHeldRing(p);
        boolean hasRing = (ring != null && RingManager.isRingEnabled(p));

        String cookie = plugin.getCookieManager().getLastCookieEaten(p);
        boolean hasCookie = (cookie != null);

        int shifts = shiftCount.getOrDefault(id, 0);

        // GEAR 4 — DOUBLE SHIFT + RIGHT CLICK
        if (shifts == 2) {

            if (hasRing) {
                ring.abilityC(p);
                shiftCount.put(id, 0);
                return;
            }

            if (hasCookie) {
                plugin.getCookieManager().getCookie(cookie).activateGear4(p);
                shiftCount.put(id, 0);
                return;
            }
        }

        // GEAR 1 — SHIFT + RIGHT CLICK
        if (shifts == 1) {

            if (hasRing) {
                ring.abilityA(p);
                shiftCount.put(id, 0);
                return;
            }

            if (hasCookie) {
                plugin.getCookieManager().getCookie(cookie).activateGear1(p);
                shiftCount.put(id, 0);
            }
        }
    }


    // ============================================================
    // LEFT CLICK (Gear 2, Gear 5)
    // ============================================================
    @EventHandler
    public void onLeftClick(PlayerAnimationEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();

        if (!p.isSneaking()) return;

        RingPower ring = plugin.getRingManager().getHeldRing(p);
        boolean hasRing = (ring != null && RingManager.isRingEnabled(p));

        String cookie = plugin.getCookieManager().getLastCookieEaten(p);
        boolean hasCookie = (cookie != null);

        int shifts = shiftCount.getOrDefault(id, 0);

        // GEAR 5 — DOUBLE SHIFT + LEFT CLICK
        if (shifts == 2) {

            if (hasRing) {
                ring.abilityC(p);
                shiftCount.put(id, 0);
                return;
            }

            if (hasCookie) {
                plugin.getCookieManager().getCookie(cookie).activateGear5(p);
                shiftCount.put(id, 0);
                return;
            }
        }

        // GEAR 2 — SHIFT + LEFT CLICK
        if (shifts == 1) {

            if (hasRing) {
                ring.abilityB(p);
                shiftCount.put(id, 0);
                return;
            }

            if (hasCookie) {
                plugin.getCookieManager().getCookie(cookie).activateGear2(p);
                shiftCount.put(id, 0);
            }
        }
    }
}
