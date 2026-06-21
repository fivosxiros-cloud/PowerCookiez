package me.foivos.powerCookiez.Cookiez;

import me.foivos.powerCookiez.Cookiez.Cookies.ZeroGravityCookie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class CookieEvents implements Listener {

    @EventHandler
    public void onFallDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p
                && e.getCause() == DamageCause.FALL
                && ZeroGravityCookie.isEnabledFor(p)) {

            e.setCancelled(true);
        }
    }
}
