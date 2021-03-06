package nfn11.xpwars.listener;

import nfn11.xpwars.XPWars;
import nfn11.xpwars.utils.XPWarsUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerJoinEvent;

import java.util.Arrays;

public class JoinGameListener implements Listener {

    public JoinGameListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, XPWars.getInstance());
    }

    @EventHandler
    public void onJoinGame(BedwarsPlayerJoinEvent event) {

        assert !event.isCancelled();

        ConfigurationSection section = XPWars.getConfigurator().config.getConfigurationSection("permission-to-join-game");
        assert section != null;

        section.getConfigurationSection("arenas").getKeys(false).forEach(key -> {
            String[] arenas = key.split(";");
            if (Arrays.toString(arenas).contains(event.getGame().getName())) {
                event.setCancelled(true);
                String message = section.getString("permission-to-join-game.message",
                        "You don't have permission %perm% to join arena %arena%!")
                        .replace("%perm%", section.getString("arenas." + key))
                        .replace("%arena%", event.getGame().getName());
                XPWarsUtils.sendActionBar(event.getPlayer(), message);
            }
        });
    }

}