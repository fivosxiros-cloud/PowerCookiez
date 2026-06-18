package me.foivos.powerCookiez;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CookiezCommand implements CommandExecutor {

    private final PowerCookiezMAIN plugin;

    public CookiezCommand(PowerCookiezMAIN plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this.");
            return true;
        }

        Player p = (Player) sender;

        CookiezMenu menu = new CookiezMenu(plugin);
        menu.open(p);   // 👈 αυτό, όχι openMenu(p, 1)

        return true;
    }
}
