package me.foivos.powerCookiez.poweritems.commands;

import me.foivos.powerCookiez.Cookiez.CookieManager;
import me.foivos.powerCookiez.Cookiez.CookiePower;
import me.foivos.powerCookiez.PowerCookiezMAIN;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class SetGearCommand implements CommandExecutor {
    private final PowerCookiezMAIN plugin;

    public SetGearCommand(PowerCookiezMAIN plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String[] args) {


        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /setgear <player> <level>", NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (Exception e) {
            sender.sendMessage(Component.text("Gear must be a number!", NamedTextColor.RED));
            return true;
        }

        CookieManager cm = PowerCookiezMAIN.getInstance().getCookieManager();
        String cookieName = cm.getLastCookieEaten(target);

        if (cookieName == null) {
            sender.sendMessage(Component.text("This player has not eaten any cookie!", NamedTextColor.RED));
            return true;
        }

        CookiePower cookie = cm.getCookie(cookieName);
        int maxGears = cookie.getMaxGears();

        if (level < 0 || level > maxGears) {
            sender.sendMessage(Component.text(cookieName + " supports only 0–" + maxGears + " gears!", NamedTextColor.RED));
            return true;
        }

        cm.setGearLevel(target, level);

        sender.sendMessage(
                Component.text("Set " + target.getName() + "'s " + cookieName + " to Gear " + level + "!", NamedTextColor.GREEN));
        target.sendMessage(
                Component.text("Your " + cookieName + " is now Gear " + level + "!", NamedTextColor.AQUA));

        return true;
    }
}
