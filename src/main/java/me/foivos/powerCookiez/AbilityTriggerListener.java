package me.foivos.powerCookiez;

import me.foivos.powerCookiez.poweritems.RingManager;
import me.foivos.powerCookiez.poweritems.rings.RingPower;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityTriggerListener implements Listener {

    private final PowerCookiezMAIN plugin;

    private final Map<UUID, Long> lastShift = new HashMap<>();
    private final Map<UUID, Boolean> doubleShiftReady = new HashMap<>();

    public AbilityTriggerListener(PowerCookiezMAIN plugin) {
        this.plugin = plugin;
    }

    // ============================================================
    // DOUBLE SHIFT DETECTION (<= 250ms)
    // ============================================================
    @EventHandler
    public void onShift(PlayerToggleSneakEvent e) {
        if (!e.isSneaking()) return;

        Player p = e.getPlayer();
        UUID id = p.getUniqueId();

        long now = System.currentTimeMillis();
        long last = lastShift.getOrDefault(id, 0L);

        if (now - last <= 250) {
            doubleShiftReady.put(id, true);

            Bukkit.getScheduler().runTaskLater(plugin, () ->
                    doubleShiftReady.put(id, false), 20L);
        }

        lastShift.put(id, now);
    }

    // ============================================================
    // RINGS — DOUBLE SHIFT + 1/2/3/4
    // ============================================================
    @EventHandler
    public void onHotbarChange(PlayerItemHeldEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();

        if (!doubleShiftReady.getOrDefault(id, false)) return;

        RingPower ring = plugin.getRingManager().getHeldRing(p);
        if (ring == null) return;

        int slot = e.getNewSlot();

        switch (slot) {
            case 0 -> ring.abilityA(p);
            case 1 -> ring.abilityB(p);
            case 2 -> ring.abilityC(p);
            case 3 -> ring.abilityD(p);
            default -> { return; }
        }

        e.setCancelled(true);
        doubleShiftReady.put(id, false);
    }

    // ============================================================
    // COOKIE — DOUBLE SHIFT + Q → Gear 1
    // ============================================================
    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();

        if (!doubleShiftReady.getOrDefault(id, false)) return;

        e.setCancelled(true);

        triggerCookieGear(p, 1);

        doubleShiftReady.put(id, false);
    }

    // ============================================================
    // COOKIE — DOUBLE SHIFT + W/A/S → Gear 2/3/4
    // (velocity-based, 100% reliable)
    // ============================================================
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();

        if (!doubleShiftReady.getOrDefault(id, false)) return;

        double vx = e.getTo().getX() - e.getFrom().getX();
        double vz = e.getTo().getZ() - e.getFrom().getZ();

        if (Math.abs(vx) < 0.08 && Math.abs(vz) < 0.08) return;

        if (Math.abs(vx) > Math.abs(vz)) {
            if (vx < 0) triggerCookieGear(p, 3); // A
            else return;
        } else {
            if (vz < 0) triggerCookieGear(p, 2); // W
            else triggerCookieGear(p, 4);        // S
        }

        doubleShiftReady.put(id, false);
    }

    // ============================================================
    // COOKIE — DOUBLE SHIFT + Z → Gear 5
    // (SwapHandItemsEvent = F key, repurposed as Z)
    // ============================================================
    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();

        if (!doubleShiftReady.getOrDefault(id, false)) return;

        e.setCancelled(true);

        triggerCookieGear(p, 5);

        doubleShiftReady.put(id, false);
    }

    // ============================================================
    // COOKIE — DOUBLE SHIFT + X → Gear 6
    // (Left-click animation = ARM_SWING)
    // ============================================================
    @EventHandler
    public void onArmSwing(PlayerAnimationEvent e) {
        if (e.getAnimationType() != PlayerAnimationType.ARM_SWING) return;

        Player p = e.getPlayer();
        UUID id = p.getUniqueId();

        if (!doubleShiftReady.getOrDefault(id, false)) return;

        triggerCookieGear(p, 6);

        doubleShiftReady.put(id, false);
    }

    // ============================================================
    // HELPER — Trigger cookie gear safely
    // ============================================================
    private void triggerCookieGear(Player p, int gear) {
        String cookieName = plugin.getCookieManager().getLastCookieEaten(p);
        if (cookieName == null) return;

        var cookie = plugin.getCookieManager().getCookie(cookieName);
        if (cookie == null) return;

        switch (gear) {
            case 1 -> cookie.activateGear1(p);
            case 2 -> cookie.activateGear2(p);
            case 3 -> cookie.activateGear3(p);
            case 4 -> cookie.activateGear4(p);
            case 5 -> cookie.activateGear5(p);
            case 6 -> cookie.activateGear6(p);
        }
    }
}
