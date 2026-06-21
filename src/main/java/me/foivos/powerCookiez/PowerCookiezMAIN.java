package me.foivos.powerCookiez;

import me.foivos.powerCookiez.poweritems.RingManager;
import me.foivos.powerCookiez.poweritems.commands.PowerRingCommand;
import me.foivos.powerCookiez.poweritems.gui.MyRingGUI;
import me.foivos.powerCookiez.poweritems.gui.AllRingsGUI;
import me.foivos.powerCookiez.poweritems.rings.MyRingMenuListener;
import me.foivos.powerCookiez.poweritems.rings.PlayerListener;
import me.foivos.powerCookiez.poweritems.rings.ani.FoxyFoxRing;
import me.foivos.powerCookiez.poweritems.rings.ani.ShadowyDragonRing;
import me.foivos.powerCookiez.poweritems.rings.ani.WolfyWolfRing;
import me.foivos.powerCookiez.poweritems.rings.ele.AmberFlameRing;
import me.foivos.powerCookiez.poweritems.rings.ele.PlanetaryEarthRing;
import me.foivos.powerCookiez.poweritems.rings.ele.SplashyWaterRing;
import me.foivos.powerCookiez.poweritems.rings.ele.StrikyWindsRing;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PowerCookiezMAIN extends JavaPlugin {

    private static PowerCookiezMAIN instance;
    public FileConfiguration playerData;
    public File playerDataFile;

    private CookieManager cookieManager;
    private RingManager ringManager;

    @Override
    public void onEnable() {
        instance = this;

        // === PlayerData ===
        createPlayerData();

        // === Managers ===
        cookieManager = new CookieManager(this);
        ringManager = new RingManager();

        // === Commands για rings ===
        getCommand("pwring").setExecutor(new PowerRingCommand());
        getCommand("myring").setExecutor((sender, cmd, label, args) -> {
            if (sender instanceof Player p) MyRingGUI.open(p);
            return true;
        });
        getCommand("rings").setExecutor((sender, cmd, label, args) -> {
            if (sender instanceof Player p) AllRingsGUI.open(p);
            return true;
        });

        // === Passive loop για rings ===
        RingManager.startPassiveLoop(this);

        // === Register rings ===
        RingManager.registerRing(new AmberFlameRing());
        RingManager.registerRing(new PlanetaryEarthRing());
        RingManager.registerRing(new StrikyWindsRing());
        RingManager.registerRing(new SplashyWaterRing());
        RingManager.registerRing(new WolfyWolfRing());
        RingManager.registerRing(new FoxyFoxRing());
        RingManager.registerRing(new ShadowyDragonRing());
        cookieManager.registerCookie(new ZeroGravityCookie());

        // === Listeners ===
        MyCookieMenu myCookieMenu = new MyCookieMenu(this);
        getServer().getPluginManager().registerEvents(myCookieMenu, this);
        getCommand("mycookie").setExecutor((sender, cmd, label, args) -> {
            if (sender instanceof Player p) {
                myCookieMenu.open(p);
            }
            return true;
        });

        CookiezMenu cookiezMenu = new CookiezMenu(this);
        getServer().getPluginManager().registerEvents(cookiezMenu, this);
        getCommand("cookiez").setExecutor((sender, cmd, label, args) -> {
            if (sender instanceof Player p) {
                cookiezMenu.open(p);
            }
            return true;
        });

        getCommand("keybinds").setExecutor((sender, cmd, label, args) -> {
            if (sender instanceof Player p) {
                me.foivos.powerCookiez.poweritems.gui.KeybindTutorialGUI.open(p);
            }
            return true;
        });

        getServer().getPluginManager().registerEvents(new AbilityTriggerListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinLoad(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getServer().getPluginManager().registerEvents(new CookieEatListener(), this);
        getServer().getPluginManager().registerEvents(new CookieEvents(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // 🔹 Listener για /myring toggle GUI
        getServer().getPluginManager().registerEvents(new MyRingMenuListener(ringManager), this);

        // === Register commands (παλιά) ===
        getCommand("cookiez").setExecutor(new CookiezCommand(this));
        getCommand("setgear").setExecutor(new SetGearCommand(this));
        getCommand("mycookie").setExecutor(new MyCookieCommand(this));

        // === PASSIVE EFFECTS LOOP για cookies ===
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                String cookie = getCookieManager().getLastCookieEaten(p);
                getCookieManager().applyPassiveEffects(p, cookie);
            }
        }, 20L, 40L); // κάθε 2 δευτερόλεπτα

        getLogger().info("PowerCookiez enabled!");
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
        RingManager.disableRing(player);
        }
        savePlayerData();
        getLogger().info("PowerCookiez disabled!");
    }

    public static PowerCookiezMAIN getInstance() {
        return instance;
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    public RingManager getRingManager() {
        return ringManager;
    }

    // ============================================
    // PLAYERDATA SAVE SYSTEM
    // ============================================

    public void createPlayerData() {
        playerDataFile = new File(getDataFolder(), "playerdata.yml");

        if (!playerDataFile.exists()) {
            playerDataFile.getParentFile().mkdirs();
            saveResource("playerdata.yml", false);
        }

        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    public void savePlayerData() {
        try {
            playerData.save(playerDataFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
