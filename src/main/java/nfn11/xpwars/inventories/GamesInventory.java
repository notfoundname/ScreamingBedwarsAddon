package nfn11.xpwars.inventories;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.lib.sgui.SimpleInventories;
import org.screamingsandals.bedwars.lib.sgui.builder.FormatBuilder;
import org.screamingsandals.bedwars.lib.sgui.events.PostActionEvent;
import org.screamingsandals.bedwars.lib.sgui.inventory.GuiHolder;
import org.screamingsandals.bedwars.lib.sgui.inventory.Options;
import org.screamingsandals.bedwars.lib.sgui.utils.MapReader;
import org.screamingsandals.bedwars.lib.sgui.utils.StackParser;

import nfn11.xpwars.XPWars;
import nfn11.xpwars.utils.XPWarsUtils;

public class GamesInventory implements Listener {
	private SimpleInventories menu;
	private Options options;
	private List<Player> openedForPlayers = new ArrayList<>();
	XPWars plugin;

	public GamesInventory(XPWars plugin) {
		
		this.plugin = plugin;
		
		options = new Options(XPWars.getInstance());
		options.setPrefix(ChatColor.translateAlternateColorCodes('&',
				XPWars.getConfigurator().getString("games-gui.title", "games")
						.replace("%free%", Integer.toString(XPWarsUtils.getFreeGamesInt()))
						.replace("%total%", Integer.toString(Main.getGameNames().size()))));
		options.setShowPageNumber(true);

		ItemStack backItem = Main.getConfigurator().readDefinedItem("shopback", "BARRIER");
		ItemMeta backItemMeta = backItem.getItemMeta();
		backItemMeta.setDisplayName(i18n("shop_back", false));
		backItem.setItemMeta(backItemMeta);
		options.setBackItem(backItem);

		ItemStack pageBackItem = Main.getConfigurator().readDefinedItem("pageback", "ARROW");
		ItemMeta pageBackItemMeta = backItem.getItemMeta();
		pageBackItemMeta.setDisplayName(i18n("page_back", false));
		pageBackItem.setItemMeta(pageBackItemMeta);
		options.setPageBackItem(pageBackItem);

		ItemStack pageForwardItem = Main.getConfigurator().readDefinedItem("pageforward", "ARROW");
		ItemMeta pageForwardItemMeta = backItem.getItemMeta();
		pageForwardItemMeta.setDisplayName(i18n("page_forward", false));
		pageForwardItem.setItemMeta(pageForwardItemMeta);
		options.setPageForwardItem(pageForwardItem);

		ItemStack cosmeticItem = Main.getConfigurator().readDefinedItem("shopcosmetic", "AIR");
		options.setCosmeticItem(cosmeticItem);

		options.setRender_header_start(0);
		options.setRender_offset(9);
		options.setRender_footer_start(45);
		options.setRows(4);
		options.setRender_actual_rows(6);

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		createData();

	}

	public void destroy() {
		openedForPlayers.clear();
		HandlerList.unregisterAll(this);
	}

	public void openForPlayer(Player player) {
		createData();
		menu.openForPlayer(player);
		openedForPlayers.add(player);
	}

	@SuppressWarnings("serial")
	private void createData() {
		SimpleInventories menu = new SimpleInventories(options);
		FormatBuilder builder = new FormatBuilder();

		if (XPWars.getConfigurator().config.getBoolean("games-gui.enable-categories")) {
			for (String s : XPWars.getConfigurator().config.getConfigurationSection("games-gui.categories")
					.getValues(false).keySet()) {
				ItemStack category = StackParser.parseNullable(XPWars.getConfigurator().config
						.getConfigurationSection("games-gui.categories." + s).get("stack"));
				if (category == null)
					return;

				ItemMeta cmeta = category.getItemMeta();
				String name = cmeta.getDisplayName();
				name = ChatColor.translateAlternateColorCodes('&', name);
				cmeta.setDisplayName(name);
				category.setItemMeta(cmeta);

				builder.add(category)
						.set("skip", XPWars.getConfigurator().config.getInt("games-gui.categories." + s + ".skip"))
						.set("items", new ArrayList<Object>() {
							{
								for (org.screamingsandals.bedwars.api.game.Game game : BedwarsAPI.getInstance()
										.getGames()) {
									if (XPWars.getConfigurator().config
											.getStringList("games-gui.categories." + s + ".arenas")
											.contains(game.getName())) {
										add(new HashMap<String, Object>() {
											{
												put("stack", formatStack(game));
												put("game", game);
											}
										});
									}
								}
							}
						});
			}
		} else {
			for (org.screamingsandals.bedwars.api.game.Game game : BedwarsAPI.getInstance().getGames()) {
				builder.add(formatStack(game)).set("game", game);
			}
		}

		menu.load(builder);
		menu.generateData();

		this.menu = menu;
	}

	private ItemStack formatStack(org.screamingsandals.bedwars.api.game.Game game) {
		ItemStack stack = StackParser.parse(XPWars.getConfigurator().config
				.getConfigurationSection("games-gui.itemstack").get(game.getStatus().toString()));

		ItemMeta meta = stack.getItemMeta();

		String name1 = meta.getDisplayName();

		name1 = ChatColor.translateAlternateColorCodes('&', name1);
		name1 = name1.replace("%arena%", game.getName());
		name1 = name1.replace("%players%", Integer.toString(game.countConnectedPlayers()));
		name1 = name1.replace("%maxplayers%", Integer.toString(game.getMaxPlayers()));
		name1 = name1.replace("%time_left%", Main.getGame(game.getName()).getFormattedTimeLeft().equals("00:0-1") ? ""
				: Main.getGame(game.getName()).getFormattedTimeLeft());

		meta.setDisplayName(name1);

		List<String> newLore = new ArrayList<>();
		List<String> lore = meta.getLore();

		for (String s : lore) {
			s = ChatColor.translateAlternateColorCodes('&', s);

			s = s.replaceAll("%arena%", game.getName());
			s = s.replaceAll("%time_left%", Main.getGame(game.getName()).getFormattedTimeLeft().equals("00:0-1") ? ""
					: Main.getGame(game.getName()).getFormattedTimeLeft());
			s = s.replaceAll("%players%", Integer.toString(game.countConnectedPlayers()));
			s = s.replaceAll("%maxplayers%", Integer.toString(game.getMaxPlayers()));

			newLore.add(s);
		}
		meta.setLore(newLore);

		stack.setItemMeta(meta);
		return stack;
	}

	public void repaint() {
		for (Player player : openedForPlayers) {
			GuiHolder guiHolder = menu.getCurrentGuiHolder(player);
			if (guiHolder == null) {
				return;
			}

			createData();
			guiHolder.setFormat(menu);
			guiHolder.repaint();
		}
	}

	@EventHandler
	public void onPostAction(PostActionEvent event) {
		if (event.getFormat() != menu) {
			return;
		}

		Player player = event.getPlayer();
		MapReader reader = event.getItem().getReader();
		if (reader.containsKey("game")) {
			Game game = (Game) reader.get("game");
			Main.getGame(game.getName()).joinToGame(player);
			player.closeInventory();

			repaint();
			openedForPlayers.remove(player);
		}
	}
}