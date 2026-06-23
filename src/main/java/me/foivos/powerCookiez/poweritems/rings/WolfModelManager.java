package me.foivos.powerCookiez.poweritems.rings;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WolfModelManager {

    private final Map<UUID, Wolf> wolves = new HashMap<>();
    private final PowerCookiezMAIN plugin = PowerCookiezMAIN.getInstance();

    public boolean isTransformed(Player p) {return wolves.containsKey(p.getUniqueId());}
    public Wolf getWolf(Player player) {return wolves.get(player.getUniqueId());}

    public void spawnWolf(Player p) {

        removeWolf(p);

        Wolf wolf = (Wolf) p.getWorld().spawnEntity(
                p.getLocation().clone().add(0, 0, 0),
                EntityType.WOLF
        );

        wolf.setAI(false);
        wolf.setGravity(false);
        wolf.setInvulnerable(true);
        wolf.setSilent(true);
        wolf.setCollidable(false);
        wolf.setAdult();
        wolf.setCustomName("§fSpirit Wolf");
        wolf.setCustomNameVisible(false);//=========================================

        wolves.put(p.getUniqueId(), wolf);
        Team team = getOrCreateNoCollisionTeam();

        team.addEntry(p.getName());
        team.addEntry(wolf.getUniqueId().toString());

        // FOLLOW TASK
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isTransformed(p)) { cancel(); return; }

                Wolf w = wolves.get(p.getUniqueId());
                if (w == null || w.isDead()) { cancel(); return; }

                Location target = p.getLocation().clone().add(0,0,0);
                w.teleport(target);
                w.setVelocity(new Vector(0, 0, 0));
                w.setRotation(p.getYaw(), p.getPitch());
                plugin.getLogger().info("Wolf moved at: " + target.toString());
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    private Team getOrCreateNoCollisionTeam() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        Team team = scoreboard.getTeam("wolf_disguise_nocollision");

        if (team == null) {
            team = scoreboard.registerNewTeam("wolf_disguise_nocollision");
            team.setOption(
                    Team.Option.COLLISION_RULE,
                    Team.OptionStatus.NEVER
            );
        }
        return team;
    }

    public void removeWolf(Player p) {
        Wolf w = wolves.remove(p.getUniqueId());
        if (w != null && !w.isDead()) w.remove();
    }
}
