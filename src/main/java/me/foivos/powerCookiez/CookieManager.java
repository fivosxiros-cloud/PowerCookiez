package me.foivos.powerCookiez;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class CookieManager {

    private final PowerCookiezMAIN plugin;

    private final Map<String, CookiePower> cookies = new HashMap<>();
    private final Map<UUID, String> lastCookieEaten = new HashMap<>();
    private final Map<UUID, Integer> gearLevels = new HashMap<>();
    public static Map<UUID, String> activeCookies = new HashMap<>();
    public static final Set<UUID> ZeroGravityNoFall = new HashSet<>();
    private final Map<UUID, Boolean> cookieEnabled = new HashMap<>();

    public boolean isCookieEnabled(Player p) {
        return cookieEnabled.getOrDefault(p.getUniqueId(), true);
    }

    public void setCookieEnabled(Player p, boolean enabled) {
        cookieEnabled.put(p.getUniqueId(), enabled);
    }

    public static boolean hasCookie(Player p, String cookieName) {
        return activeCookies.containsKey(p.getUniqueId())
                && activeCookies.get(p.getUniqueId()).equals(cookieName);
    }

    public static void giveCookie(Player p, String cookieName) {
        activeCookies.put(p.getUniqueId(), cookieName);
    }

    private final Map<UUID, Map<Integer, Long>> gearCooldowns = new HashMap<>();
    private final Map<UUID, Map<Integer, Integer>> gearComboCount = new HashMap<>();
    private final Map<UUID, Map<Integer, Long>> gearComboWindow = new HashMap<>();

    private final Map<UUID, Long> frostAuraCooldown = new HashMap<>();
    private final Map<UUID, Long> smokeAuraCooldown = new HashMap<>();
    private final Map<UUID, Long> waterDamageCooldown = new HashMap<>();

    public CookieManager(PowerCookiezMAIN plugin) {
        this.plugin = plugin;

        registerCookie(new FrostyFrostCookie());
        registerCookie(new SmokySmokeCookie());
        registerCookie(new ZeroGravityCookie());
    }

    public Collection<CookiePower> getAllCookies() {
        return cookies.values();
    }

    public CookiePower getCookieByItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        String display = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        for (CookiePower cookie : cookies.values()) {
            String cookieName = ChatColor.stripColor(cookie.getDisplayItem().getItemMeta().getDisplayName());
            if (cookieName.equalsIgnoreCase(display)) {
                return cookie;
            }
        }
        return null;
    }

    public void eatCookie(Player p, String cookieName) {

        CookiePower cookie = cookies.get(cookieName);
        if (cookie == null) return;

        setLastCookieEaten(p, cookieName);
        setGearLevel(p, 0);

        cookie.activate(p);

        setCooldown(p);

        p.sendMessage(ChatColor.AQUA + "You consumed the " + cookieName + " cookie!");
    }


    public void registerCookie(CookiePower cookie) {
        cookies.put(cookie.getName(), cookie);
    }

    public CookiePower getCookie(String name) {
        return cookies.get(name);
    }

    public Collection<CookiePower> getAllCookieDisplays() {
        return cookies.values();
    }

    public void setLastCookieEaten(Player p, String cookie) {
        UUID id = p.getUniqueId();
        lastCookieEaten.put(id, cookie);
        plugin.playerData.set(id + ".cookie", cookie);
        plugin.savePlayerData();
    }

    public String getLastCookieEaten(Player p) {
        return lastCookieEaten.get(p.getUniqueId());
    }

    public void setGearLevel(Player p, int level) {
        UUID id = p.getUniqueId();
        gearLevels.put(id, level);
        plugin.playerData.set(id + ".gear", level);
        plugin.savePlayerData();
    }

    public int getGearLevel(Player p) {
        return gearLevels.getOrDefault(p.getUniqueId(), 0);
    }

    private final Map<UUID, Long> eatCooldowns = new HashMap<>();

    public boolean isOnCooldown(Player p) {
        long now = System.currentTimeMillis();
        long last = eatCooldowns.getOrDefault(p.getUniqueId(), 0L);
        return now - last < 5000;
    }

    public void setCooldown(Player p) {
        eatCooldowns.put(p.getUniqueId(), System.currentTimeMillis());
    }

    public boolean isGearOnCooldown(Player p, int gear, long cooldownMs) {
        UUID id = p.getUniqueId();
        gearCooldowns.putIfAbsent(id, new HashMap<>());
        long last = gearCooldowns.get(id).getOrDefault(gear, 0L);
        return System.currentTimeMillis() - last < cooldownMs;
    }

    public void setGearCooldown(Player p, int gear) {
        UUID id = p.getUniqueId();
        gearCooldowns.putIfAbsent(id, new HashMap<>());
        gearCooldowns.get(id).put(gear, System.currentTimeMillis());
    }

    public int getComboCount(Player p, int gear) {
        UUID id = p.getUniqueId();
        gearComboCount.putIfAbsent(id, new HashMap<>());
        return gearComboCount.get(id).getOrDefault(gear, 0);
    }

    public void setComboCount(Player p, int gear, int value) {
        UUID id = p.getUniqueId();
        gearComboCount.putIfAbsent(id, new HashMap<>());
        gearComboCount.get(id).put(gear, value);
    }

    public long getComboWindow(Player p, int gear) {
        UUID id = p.getUniqueId();
        gearComboWindow.putIfAbsent(id, new HashMap<>());
        return gearComboWindow.get(id).getOrDefault(gear, 0L);
    }

    public void setComboWindow(Player p, int gear, long value) {
        UUID id = p.getUniqueId();
        gearComboWindow.putIfAbsent(id, new HashMap<>());
        gearComboWindow.get(id).put(gear, value);
    }

    public void applyPassiveEffects(Player p, String cookieName) {

        if (cookieName == null) return;
        if (!isCookieEnabled(p)) return;

        if (cookieName.equals("FrostyFrostCookie")) {

            p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, 1, false, false, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 0, false, false, true));

            p.setFreezeTicks(0);

            if (p.getFireTicks() > 0) p.damage(1.0);

            for (Entity e : p.getNearbyEntities(1.5, 1.5, 1.5)) {
                if (e instanceof LivingEntity && e != p) {
                    LivingEntity le = (LivingEntity) e;
                    le.setFreezeTicks(le.getFreezeTicks() + 40);
                    le.damage(2.0, p);
                }
            }

            long now = System.currentTimeMillis();
            long last = frostAuraCooldown.getOrDefault(p.getUniqueId(), 0L);

            if (now - last >= 5000) {
                frostAuraCooldown.put(p.getUniqueId(), now);

                for (Entity e : p.getNearbyEntities(9, 9, 9)) {
                    if (e instanceof LivingEntity && e != p) {
                        LivingEntity le = (LivingEntity) e;
                        le.damage(6.0, p);
                        le.setFreezeTicks(le.getFreezeTicks() + 60);
                    }
                }
            }
        }

        if (cookieName.equals("SmokySmokeCookie")) {

            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1, false, false, true));

            if (p.getFireTicks() > 0) p.setFireTicks(p.getFireTicks() - 2);

            p.setFreezeTicks(0);

            if (p.getRemainingAir() < p.getMaximumAir()) p.damage(1.0);

            long now = System.currentTimeMillis();
            long last = smokeAuraCooldown.getOrDefault(p.getUniqueId(), 0L);

            if (now - last >= 5000) {
                smokeAuraCooldown.put(p.getUniqueId(), now);

                for (Entity e : p.getNearbyEntities(9, 9, 9)) {
                    if (e instanceof LivingEntity && e != p) {
                        LivingEntity le = (LivingEntity) e;
                        le.damage(6.0, p);
                    }
                }
            }
        }

        if ("ZeroGravityCookie".equals(cookieName)) {

            p.setFallDistance(0);

            p.addPotionEffect(new PotionEffect(
                    PotionEffectType.SPEED,
                    40,
                    0,
                    true, false, false
            ));

            if (p.getLocation().getBlock().isLiquid()) {

                long now = System.currentTimeMillis();
                long last = waterDamageCooldown.getOrDefault(p.getUniqueId(), 0L);

                if (now - last >= 1000) {
                    waterDamageCooldown.put(p.getUniqueId(), now);
                    p.damage(2.0);
                }
            }
        }

    }

    public void onEat(Player player, String name) {

        setLastCookieEaten(player, name);
        setGearLevel(player, 0);

        if (cookies.containsKey(name)) {
            cookies.get(name).activate(player);
        }

        player.sendMessage(ChatColor.AQUA + "You consumed the " + name + " cookie!");
    }
}
