package me.foivos.powerCookiez.poweritems.commands;

import me.foivos.powerCookiez.Cookiez.AllCookiez.CookiezMenu;
import me.foivos.powerCookiez.PowerCookiezMAIN;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class CookiezCommand implements CommandExecutor {

    private final PowerCookiezMAIN plugin;

    public CookiezCommand(PowerCookiezMAIN plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {

        if (!(sender instanceof Player p)) {
            sender.sendMessage("Only players can use this.");
            return true;
        }

        CookiezMenu menu = new CookiezMenu(plugin);
        menu.open(p);   // 👈 αυτό, όχι openMenu(p, 1)

        return true;
    }
}
