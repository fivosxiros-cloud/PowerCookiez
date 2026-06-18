package me.foivos.powerCookiez.poweritems.rings.ani;

import me.foivos.powerCookiez.PowerCookiezMAIN;
import me.foivos.powerCookiez.poweritems.RingManager;
import me.foivos.powerCookiez.poweritems.rings.RingCategory;
import me.foivos.powerCookiez.poweritems.rings.RingPower;
import me.foivos.powerCookiez.poweritems.rings.FoxFollowerTask;
import me.foivos.powerCookiez.poweritems.rings.FoxModelManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class FoxyFoxRing implements RingPower {

    private final Plugin plugin;
    private final FoxModelManager foxModelManager;

    private final Map<UUID, Long> toggleCooldown = new HashMap<>();
    private final long TOGGLE_COOLDOWN_MS = 1500L;

    public FoxyFoxRing() {
        this.plugin = PowerCookiezMAIN.getInstance();
        this.foxModelManager = new FoxModelManager();
    }


    @Override
    public String getName() {
        return "FoxyFoxRing";
    }
    @Override
    public RingCategory getCategory() {
        return RingCategory.ANI;
    }

    @Override
    public ItemStack getDisplayItem() {
        ItemStack item = new ItemStack(Material.RABBIT_FOOT);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§6§lFoxy Fox Ring");
        meta.setLore(Arrays.asList(
                "§7Transform into a fox at will.",
                "§7Gain agility, stealth and night vision.",
                "",
                "§eGear A: §fFox Form Toggle",
                "§eGear B: §7(coming soon)",
                "§eGear C: §7(coming soon)",
                "§eGear D: §7(coming soon)"
        ));

        meta.getPersistentDataContainer().set(
                new NamespacedKey(PowerCookiezMAIN.getInstance(), "ringName"),
                PersistentDataType.STRING,
                getName()
        );

        item.setItemMeta(meta);
        return item;
    }


    // ============================================================
    // ABILITY A (SHIFT + RIGHT CLICK)
    // ============================================================
    @Override
    public void abilityA(Player player) {
        long now = System.currentTimeMillis();
        long last = toggleCooldown.getOrDefault(player.getUniqueId(), 0L);
        if (now - last < TOGGLE_COOLDOWN_MS) return;
        toggleCooldown.put(player.getUniqueId(), now);

        if (!foxModelManager.isFoxTransformed(player)) {
            transformToFox(player);
        } else {
            transformToPlayer(player);
        }
    }

    private void transformToFox(Player player) {

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.INVISIBILITY,
                Integer.MAX_VALUE,
                1,
                false,
                false,
                false
        ));

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED,
                Integer.MAX_VALUE,
                0,
                false,
                false,
                false
        ));

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.JUMP_BOOST,
                Integer.MAX_VALUE,
                0,
                false,
                false,
                false
        ));

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.NIGHT_VISION,
                Integer.MAX_VALUE,
                0,
                false,
                false,
                false
        ));

        Fox fox = foxModelManager.spawnFox(player);

        int id = new FoxFollowerTask(plugin, foxModelManager, player).runTaskTimer(plugin, 1, 1).getTaskId();
        RingManager.registerTask(player, id);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FOX_AMBIENT, 1f, 1.2f);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 20, 0.3, 0.3, 0.3, 0.01);

        new FoxFollowerTask(plugin, foxModelManager, player).runTaskTimer(plugin, 1L, 1L);
    }

    private void transformToPlayer(Player player) {

        foxModelManager.removeFox(player);

        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.JUMP_BOOST);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FOX_DEATH, 1f, 0.8f);
        player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation(), 25, 0.4, 0.4, 0.4, 0.02);
    }

    @Override
    public void abilityB(Player player) {}
    @Override
    public void abilityC(Player player) {}
    @Override
    public void abilityD(Player player) {}

    @Override
    public void applyPassives(Player p) {}

    public FoxModelManager getFoxModelManager() {
        return foxModelManager;
    }

}
