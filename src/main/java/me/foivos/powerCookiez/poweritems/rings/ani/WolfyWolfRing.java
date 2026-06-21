package me.foivos.powerCookiez.poweritems.rings.ani;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import me.foivos.powerCookiez.poweritems.rings.RingCategory;
import me.foivos.powerCookiez.poweritems.rings.RingPower;
import me.foivos.powerCookiez.poweritems.rings.WolfModelManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class WolfyWolfRing implements RingPower {

    private final WolfModelManager wolfModelManager = new WolfModelManager();
    private final Map<UUID, Long> toggleCooldown = new HashMap<>();
    private final long TOGGLE_COOLDOWN_MS = 1500L;

    @Override
    public String getName() {
        return "WolfyWolfRing";
    }

    @Override
    public RingCategory getCategory() {
        return RingCategory.ANI;
    }

    @Override
    public ItemStack getDisplayItem() {
        ItemStack item = new ItemStack(Material.BONE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§f§lWolfy Wolf Ring");
        meta.setLore(Arrays.asList(
                "§7Transform into a wolf spirit.",
                "§7Gain speed, jump boost and stealth.",
                "",
                "§eAbility A: §fWolf Form Toggle"
        ));

        meta.getPersistentDataContainer().set(
                new NamespacedKey(PowerCookiezMAIN.getInstance(), "ringName"),
                PersistentDataType.STRING,
                getName()
        );

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void abilityA(Player player) {

        long now = System.currentTimeMillis();
        long last = toggleCooldown.getOrDefault(player.getUniqueId(), 0L);
        if (now - last < TOGGLE_COOLDOWN_MS) return;
        toggleCooldown.put(player.getUniqueId(), now);

        if (!wolfModelManager.isTransformed(player)) {
            transformToWolf(player);
        } else {
            transformToPlayer(player);
        }
    }

    private void transformToWolf(Player player) {

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, Integer.MAX_VALUE, 1, false, false, false));

        wolfModelManager.spawnWolf(player);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WOLF_AMBIENT, 1f, 1f);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 20, 0.3, 0.3, 0.3, 0.01);
    }

    private void transformToPlayer(Player player) {

        wolfModelManager.removeWolf(player);

        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.JUMP_BOOST);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WOLF_DEATH, 1f, 0.8f);
        player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation(), 25, 0.4, 0.4, 0.4, 0.02);
    }

    @Override public void abilityB(Player p) {}
    @Override public void abilityC(Player p) {}
    @Override public void abilityD(Player p) {}
    @Override public void applyPassives(Player p) {}

    public WolfModelManager getWolfModelManager() {
        return wolfModelManager;
    }
}
