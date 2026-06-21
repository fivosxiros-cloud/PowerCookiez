package me.foivos.powerCookiez;

import me.foivos.powerCookiez.Cookiez.CookiePower;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.entity.Player;

public class PlayerRespawnCookiez implements Listener {

    private final PowerCookiezMAIN plugin;

    public PlayerRespawnCookiez(PowerCookiezMAIN plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();

        // Πάρε το cookie που είχε φάει
        String cookie = plugin.getCookieManager().getLastCookieEaten(p);
        if (cookie == null) return;

        // Αν υπάρχει αυτό το cookie, ενεργοποίησε τα base abilities ξανά
        CookiePower power = plugin.getCookieManager().getCookie(cookie);
        if (power != null) {
            power.activate(p);
        }
    }
}
