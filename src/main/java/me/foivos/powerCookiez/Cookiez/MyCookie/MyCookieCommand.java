package me.foivos.powerCookiez.Cookiez.MyCookie;

import me.foivos.powerCookiez.Cookiez.CookieManager;
import me.foivos.powerCookiez.Cookiez.CookiePower;
import me.foivos.powerCookiez.PowerCookiezMAIN;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MyCookieCommand implements CommandExecutor {

    private final PowerCookiezMAIN plugin;

    public MyCookieCommand(PowerCookiezMAIN plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only.");
            return true;
        }

        Player p = (Player) sender;
        CookieManager cm = PowerCookiezMAIN.getInstance().getCookieManager();

        String cookieName = cm.getLastCookieEaten(p);

        if (cookieName == null) {
            p.sendMessage(ChatColor.RED + "You have not eaten any cookie!");
            return true;
        }

        CookiePower cookie = cm.getCookie(cookieName);
        int currentGear = cm.getGearLevel(p);
        int maxGear = cookie.getMaxGears();

        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.AQUA + "Your Cookie Info");

        // === SLOT 11: Cookie Info ===
        ItemStack cookieItem = new ItemStack(Material.COOKIE);
        ItemMeta meta = cookieItem.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Current Cookie: " + cookieName);
        meta.setLore(java.util.Arrays.asList(
                ChatColor.WHITE + "Gear Level: " + currentGear + "/" + maxGear
        ));
        cookieItem.setItemMeta(meta);
        gui.setItem(11, cookieItem);

        // === SLOT 13: Gear Descriptions ===
        ItemStack desc = new ItemStack(Material.BOOK);
        ItemMeta dMeta = desc.getItemMeta();
        dMeta.setDisplayName(ChatColor.GOLD + "Gear Abilities");

        java.util.List<String> lore = new java.util.ArrayList<>();

        for (int g = 1; g <= maxGear; g++) {
            long cd = cookie.getGearCooldownMs(g);
            lore.add(ChatColor.AQUA + "Gear " + g + ":");
            lore.add(ChatColor.GRAY + cookie.getGearDescription(g));
            lore.add(ChatColor.DARK_GRAY + "Cooldown: " + (cd / 1000) + "s");
            lore.add("");
        }

        dMeta.setLore(lore);
        desc.setItemMeta(dMeta);
        gui.setItem(13, desc);

        // === SLOT 15: Cooldowns ===
        ItemStack cds = new ItemStack(Material.CLOCK);
        ItemMeta cMeta = cds.getItemMeta();
        cMeta.setDisplayName(ChatColor.YELLOW + "Cooldown Status");

        java.util.List<String> cdLore = new java.util.ArrayList<>();

        for (int g = 1; g <= maxGear; g++) {
            long cdMs = cookie.getGearCooldownMs(g);
            boolean onCd = cm.isGearOnCooldown(p, g, cdMs);

            cdLore.add(ChatColor.AQUA + "Gear " + g + ": " +
                    (onCd ? ChatColor.RED + "ON COOLDOWN" : ChatColor.GREEN + "READY"));
        }

        cMeta.setLore(cdLore);
        cds.setItemMeta(cMeta);
        gui.setItem(15, cds);

        p.openInventory(gui);
        return true;
    }
}
