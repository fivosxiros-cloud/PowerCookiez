package me.foivos.powerCookiez.poweritems.gui;

import me.foivos.powerCookiez.poweritems.RingManager;
import me.foivos.powerCookiez.poweritems.rings.RingCategory;
import me.foivos.powerCookiez.poweritems.rings.RingPower;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AllRingsGUI {

    public static void open(Player player) {

        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.GOLD + "All Rings");

        List<RingPower> ele = new ArrayList<>();
        List<RingPower> ani = new ArrayList<>();

        for (RingPower ring : RingManager.getRegisteredRings()) {
            if (ring.getCategory() == RingCategory.ELE) ele.add(ring);
            else ani.add(ring);
        }

        int slot = 0;

        // === ELE RINGS FIRST ===
        for (RingPower ring : ele) {
            inv.setItem(slot++, ring.getDisplayItem());
        }

        // === ANI RINGS SECOND ===
        for (RingPower ring : ani) {
            inv.setItem(slot++, ring.getDisplayItem());
        }

        // === BACKGROUND ===
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta gm = glass.getItemMeta();
        gm.setDisplayName(" ");
        glass.setItemMeta(gm);

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) inv.setItem(i, glass);
        }

        player.openInventory(inv);
    }
}
