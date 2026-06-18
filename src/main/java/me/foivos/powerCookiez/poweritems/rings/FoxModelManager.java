package me.foivos.powerCookiez.poweritems.rings;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FoxModelManager {

    private final Map<UUID, Fox> foxMap = new HashMap<>();

    public boolean isFoxTransformed(Player player) {
        return foxMap.containsKey(player.getUniqueId());
    }

    public Fox getFox(Player player) {
        return foxMap.get(player.getUniqueId());
    }

    public Fox spawnFox(Player player) {
        if (isFoxTransformed(player)) return getFox(player);

        World world = player.getWorld();
        Location loc = player.getLocation().clone().add(0, 2.2, 0);

        Fox fox = (Fox) world.spawnEntity(loc, EntityType.FOX);
        fox.setAdult();
        fox.setSilent(true);
        fox.setInvulnerable(true);
        fox.setAI(false);
        fox.setCollidable(false);
        fox.setPersistent(true);
        fox.setCustomNameVisible(false);
        fox.setGravity(false);

        foxMap.put(player.getUniqueId(), fox);
        return fox;
    }

    public void removeFox(Player player) {
        Fox fox = foxMap.remove(player.getUniqueId());
        if (fox != null && !fox.isDead()) fox.remove();
    }

    public void removeAll() {
        for (Fox fox : foxMap.values()) {
            if (fox != null && !fox.isDead()) fox.remove();
        }
        foxMap.clear();
    }
}
