package me.foivos.powerCookiez;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetGearCommand implements CommandExecutor {
    private final PowerCookiezMAIN plugin;

    public SetGearCommand(PowerCookiezMAIN plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {


        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /setgear <player> <level>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Gear must be a number!");
            return true;
        }

        CookieManager cm = PowerCookiezMAIN.getInstance().getCookieManager();
        String cookieName = cm.getLastCookieEaten(target);

        if (cookieName == null) {
            sender.sendMessage(ChatColor.RED + "This player has not eaten any cookie!");
            return true;
        }

        CookiePower cookie = cm.getCookie(cookieName);
        int maxGears = cookie.getMaxGears();

        if (level < 0 || level > maxGears) {
            sender.sendMessage(ChatColor.RED + cookieName + " supports only 0–" + maxGears + " gears!");
            return true;
        }

        cm.setGearLevel(target, level);

        sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s " + cookieName +
                " to Gear " + level + "!");
        target.sendMessage(ChatColor.AQUA + "Your " + cookieName + " is now Gear " + level + "!");

        return true;
    }
}
