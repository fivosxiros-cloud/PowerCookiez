package me.foivos.powerCookiez.poweritems.rings;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FoxModelManager {

    private final Map<UUID, Fox> foxes = new HashMap<>();
    private final PowerCookiezMAIN plugin = PowerCookiezMAIN.getInstance();

    public boolean isTransformed(Player p) {
        return foxes.containsKey(p.getUniqueId());
    }
    public Fox getFox(Player player) {
        return foxes.get(player.getUniqueId());
    }

    public void spawnFox(Player p) {

        removeFox(p);

        Fox fox = (Fox) p.getWorld().spawnEntity(
                p.getLocation().clone().add(0, 0, 0),
                EntityType.FOX
        );

        fox.setAI(false);
        fox.setGravity(false);
        fox.setInvulnerable(true);
        fox.setSilent(true);
        fox.setCollidable(false);
        fox.setCustomName("§6Foxy Fox");
        fox.setCustomNameVisible(true);
        //p.setCollidable(false);
        foxes.put(p.getUniqueId(), fox);
        Team team = getOrCreateNoCollisionTeam();

        team.addEntry(p.getName());
        team.addEntry(fox.getUniqueId().toString());
        // FOLLOW TASK
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isTransformed(p)) { cancel(); return; }

                Fox f = foxes.get(p.getUniqueId());
                if (f == null || f.isDead()) { cancel(); return; }

                Location target = p.getLocation().clone().add(0,0,0);
                f.teleport(target);
                f.setVelocity(new Vector(0, 0, 0));
                f.setRotation(p.getYaw(), p.getPitch());
                plugin.getLogger().info("Fox moved at: " + target.toString());
            }
        }.runTaskTimer(plugin, 1L, 1L);

    }
    private Team getOrCreateNoCollisionTeam() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        Team team = scoreboard.getTeam("fox_disguise_nocollision");

        if (team == null) {
            team = scoreboard.registerNewTeam("fox_disguise_nocollision");
            team.setOption(
                    Team.Option.COLLISION_RULE,
                    Team.OptionStatus.NEVER
            );
        }
        return team;
    }
    public void removeFox(Player p) {
        Fox f = foxes.remove(p.getUniqueId());
        if (f != null && !f.isDead()) f.remove();
    }
}
