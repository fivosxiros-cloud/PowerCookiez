package me.foivos.powerCookiez;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.UUID;

public class CookieEvents implements Listener {

    @EventHandler
    public void onFallDamage(EntityDamageEvent e) {
        if (e.getCause() == DamageCause.FALL && e.getEntity() instanceof Player p) {
            if (ZeroGravityCookie.ZeroGravityNoFall.contains(p.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }
}
