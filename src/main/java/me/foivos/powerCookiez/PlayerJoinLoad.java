package me.foivos.powerCookiez;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinLoad implements Listener {

    private final PowerCookiezMAIN plugin;

    public PlayerJoinLoad(PowerCookiezMAIN plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        String cookie = plugin.playerData.getString(p.getUniqueId() + ".cookie");
        int gear = plugin.playerData.getInt(p.getUniqueId() + ".gear");

        if (cookie != null) {
            plugin.getCookieManager().setLastCookieEaten(p, cookie);
        }

        plugin.getCookieManager().setGearLevel(p, gear);
    }
}
