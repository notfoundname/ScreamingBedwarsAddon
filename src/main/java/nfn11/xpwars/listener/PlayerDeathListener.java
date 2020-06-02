package nfn11.xpwars.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerKilledEvent;
import nfn11.xpwars.XPWars;

public class PlayerDeathListener implements Listener {
	
	public PlayerDeathListener() {
		Bukkit.getServer().getPluginManager().registerEvents(this, XPWars.getInstance());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(BedwarsPlayerKilledEvent event) {
		Player player = event.getPlayer();
		Player killer = event.getKiller();
		String gamename = event.getGame().getName();
		
		int player_level = player.getLevel();
		int killer_level = killer.getLevel();
		
		int def_keep_from_death_player = XPWars.getConfigurator().getInt("level.percentage.loose-from-death", 0);
		int def_to_killer = XPWars.getConfigurator().getInt("level.percentage.give-from-killed-player", 0);
		
		int keep_from_death_player = XPWars.getConfigurator().getInt("level.games." + gamename+ ".percentage.loose-from-death", def_keep_from_death_player);
		int to_killer = XPWars.getConfigurator().getInt("level.games." + gamename+ "percentage.give-from-killed-player", def_to_killer);
		
		int defmax = XPWars.getConfigurator().getInt("level.maximum-xp", 0);
		int max = XPWars.getConfigurator().getInt("level.games." + gamename + ".maximum-xp", defmax);
		
		if (killer != null) {
			if (max != 0 && (killer_level + (player_level / 100) * to_killer) > max) {
				killer.setLevel(max);
			} else killer.setLevel(killer_level + (player_level / 100) * to_killer);
		}
		
		if (max != 0 && ((player_level / 100) * keep_from_death_player) > max) {
			player.setLevel(max);
		} else player.setLevel((player_level / 100) * keep_from_death_player);
	}
}