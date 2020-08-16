package nfn11.xpwars;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.commands.BaseCommand;
import org.screamingsandals.bedwars.inventories.ShopInventory;
import nfn11.thirdparty.connorlinfoot.actionbarapi.ActionBarAPI;
import nfn11.xpwars.commands.GamesCommand;
import nfn11.xpwars.commands.JoinSortedCommand;
import nfn11.xpwars.commands.XPWarsCommand;
import nfn11.xpwars.inventories.GamesInventory;
import nfn11.xpwars.inventories.LevelShop;
import nfn11.xpwars.listener.XPWarsPlayerListener;
import nfn11.xpwars.special.listener.RegisterSpecialListeners;
import nfn11.xpwars.utils.XPWarsUtils;
import org.screamingsandals.bedwars.lib.sgui.listeners.InventoryListener;

public class XPWars extends JavaPlugin implements Listener {

    private static XPWars instance;
    private Configurator configurator;
    private HashMap<String, BaseCommand> commands;
    private GamesInventory gamesInventory;
    private LevelShop levelShop;
    private ShopInventory shopInventory;

    @Override
    public void onEnable() {
        instance = this;
        new XPWarsUtils();
        if (Main.getInstance() == null) {
            XPWarsUtils.xpwarsLog(Bukkit.getConsoleSender(), "did you download wrong bedwars plugin?"); // does this
                                                                                                        // work?
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        configurator = new Configurator(this);
        configurator.loadDefaults();

        InventoryListener.init(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        levelShop = new LevelShop();
        new XPWarsPlayerListener();
        new RegisterSpecialListeners();
        new ActionBarAPI();
        new XPWarsCommand();

        if (getConfigurator().config.getBoolean("features.games-gui")) {
            gamesInventory = new GamesInventory(this);
            new GamesCommand();
            new JoinSortedCommand();
        }
        try {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
                    && getConfigurator().config.getBoolean("features.placeholders")) {
                new nfn11.xpwars.placeholderapi.PlaceholderAPIHook().register();
            }
        } catch (Throwable ignored) {
        }

        commands = new HashMap<>();

        XPWarsUtils.xpwarsLog(Bukkit.getConsoleSender(),
                "&aLoaded XPWars &2" + XPWars.getInstance().getDescription().getVersion()
                        + (isSnapshotBuild() ? " " + getBuildNumber() + "&a!" : "&a!"));
        XPWarsUtils.xpwarsLog(Bukkit.getConsoleSender(), "&aXPWars addon by &enotfoundname11");
        XPWarsUtils.xpwarsLog(Bukkit.getConsoleSender(), "&9https://github.com/notfoundname/XPWars/wiki");
        new XPWarsUpdateChecker(Bukkit.getConsoleSender());
    }

    @EventHandler
    public void onBwReload(PluginEnableEvent event) {
        String plugin = event.getPlugin().getName();
        if (plugin.equalsIgnoreCase("BedWars")) {
            Bukkit.getServer().getPluginManager().disablePlugin(XPWars.getInstance());
            Bukkit.getServer().getPluginManager().enablePlugin(XPWars.getInstance());
        }
    }

    public static Configurator getConfigurator() {
        return instance.configurator;
    }

    public static XPWars getInstance() {
        return instance;
    }

    public static HashMap<String, BaseCommand> getCommands() {
        return instance.commands;
    }

    public static GamesInventory getGamesInventory() {
        return instance.gamesInventory;
    }

    public static LevelShop getLevelShop() {
        return instance.levelShop;
    }

    public static ShopInventory getShopInventory() {
        return instance.shopInventory;
    }

    public static boolean isSnapshotBuild() {
        return XPWars.getInstance().getDescription().getVersion().contains("-SNAPSHOT") && getBuildNumber() != 0;
    }

    public static float getVersion() {
        if (isSnapshotBuild()) {
            return Float.parseFloat(XPWars.getInstance().getDescription().getVersion().replace("-SNAPSHOT", ""));
        }
        return Float.parseFloat(XPWars.getInstance().getDescription().getVersion());
    }

    public static int getBuildNumber() {
        try {
            return Integer.parseInt(XPWars.getInstance().getDescription().getDescription());
        } catch (Throwable ignored) {
            return 0;
        }
    }

}
