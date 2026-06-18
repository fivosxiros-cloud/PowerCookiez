package me.foivos.powerCookiez.poweritems.rings;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WolfModelManager {

    private final Map<UUID, Wolf> wolfMap = new HashMap<>();

    public boolean isWolf(Player player) {
        return wolfMap.containsKey(player.getUniqueId());
    }

    public Wolf getWolf(Player player) {
        return wolfMap.get(player.getUniqueId());
    }

    public Wolf spawnWolf(Player player) {
        if (isWolf(player)) return getWolf(player);

        World world = player.getWorld();
        Location loc = player.getLocation().clone().add(0, 2.2, 0);

        Wolf wolf = (Wolf) world.spawnEntity(loc, EntityType.WOLF);
        wolf.setAdult();
        wolf.setAI(false);
        wolf.setInvulnerable(true);
        wolf.setCollidable(false);
        wolf.setSilent(true);
        wolf.setCustomNameVisible(false);
        wolf.setAngry(false);
        wolf.setGravity(false);

        wolfMap.put(player.getUniqueId(), wolf);
        return wolf;
    }

    public void removeWolf(Player player) {
        Wolf wolf = wolfMap.remove(player.getUniqueId());
        if (wolf != null && !wolf.isDead()) wolf.remove();
    }

    public void removeAll() {
        for (Wolf wolf : wolfMap.values()) {
            if (wolf != null && !wolf.isDead()) wolf.remove();
        }
        wolfMap.clear();
    }
}
