package nfn11.xpwars.listener;

import me.clip.placeholderapi.PlaceholderAPI;
import nfn11.xpwars.utils.XPWarsUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.events.*;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;

import nfn11.xpwars.XPWars;

public class LevelSystemListener implements Listener {

    public LevelSystemListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, XPWars.getInstance());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(BedwarsPlayerKilledEvent event) {
        Player player = event.getPlayer();
        String gamename = event.getGame().getName();
        int player_level = player.getLevel();

        ConfigurationSection arenaSettings = XPWars.getConfigurator().config.getConfigurationSection(
                "level.per-arena-settings." + gamename);
        ConfigurationSection globalSettings = XPWars.getConfigurator().config.getConfigurationSection("level");

        if (!arenaSettings.getBoolean("enable", true))
            return;

        int keep_from_death_player = arenaSettings.getInt("percentage.keep-from-death",
                globalSettings.getInt("percentage.keep-from-death",0));
        int to_killer = arenaSettings.getInt("percentage.give-from-killed-player",
                globalSettings.getInt("percentage.give-from-killed-player", 0));
        int max = arenaSettings.getInt("maximum-xp", globalSettings.getInt("maximum-xp", 0));

        if (event.getKiller() != null) {
            Player killer = event.getKiller();
            int killer_level = killer.getLevel();
            if (max != 0 && (killer_level + (player_level / 100) * to_killer) > max)
                killer.setLevel(max);
            else killer.setLevel(killer_level + (player_level / 100) * to_killer);
        }
        if (max != 0 && ((player_level / 100) * keep_from_death_player) > max)
            player.setLevel(max);
        else player.setLevel((player_level / 100) * keep_from_death_player);
    }

    @EventHandler(priority = EventPriority.MONITOR) 
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!Main.isPlayerInGame(player))
                return;

            ConfigurationSection arenaSettings = XPWars.getConfigurator().config.getConfigurationSection(
                    "level.per-arena-settings." + Main.getPlayerGameProfile(player).getGame().getName());
            ConfigurationSection globalSettings = XPWars.getConfigurator().config.getConfigurationSection("level");

            if (!arenaSettings.getBoolean("enable", globalSettings.getBoolean("enable", false)))
                return;

            ItemStack picked = event.getItem().getItemStack();
            int level = player.getLevel();

            String sound = arenaSettings.getString("sound.sound", globalSettings.getString
                    ("sound.sound","ENTITY_EXPERIENCE_ORB_PICKUP"));
            double volume = arenaSettings.getDouble("sound.volume", globalSettings.getDouble("sound.volume", 1));
            double pitch = arenaSettings.getDouble("sound.pitch", globalSettings.getDouble("sound.pitch", 1));
            
            int max = arenaSettings.getInt("maximum-xp", globalSettings.getInt("maximum-xp", 0));
            
            for (ItemSpawnerType type : Main.getInstance().getItemSpawnerTypes()) {
                int res = arenaSettings.getInt("spawners." + type.getConfigKey(), globalSettings.getInt
                        ("spawners." + type.getConfigKey()));
                if (picked.isSimilar(type.getStack()) && picked.getItemMeta().equals(type.getStack().getItemMeta())) {
                    event.setCancelled(true);
                    if (max != 0 && (level + (res * picked.getAmount())) > max) {
                        String msg = ChatColor.translateAlternateColorCodes('&',
                                arenaSettings.getString("messages.maxreached", globalSettings.getString(
                                        "messages.maxreached","&cYou can't have more than %max% levels!")
                                        .replace("%max%", Integer.toString(max))));
                        XPWarsUtils.sendActionBar(player, msg);
                        return;
                    }
                    player.setLevel(level + (res * picked.getAmount()));
                    event.getItem().getItemStack().setType(Material.AIR);
                }
            }
            if (!sound.equalsIgnoreCase("none"))
                player.playSound(player.getLocation(), Sound.valueOf(sound), (float) volume, (float) pitch);
        }
    }

}