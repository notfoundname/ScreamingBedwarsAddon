package nfn11.xpwars.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.commands.BaseCommand;

import nfn11.xpwars.XPWars;
import nfn11.xpwars.inventories.GamesInventory;
import nfn11.xpwars.utils.XPWarsUtils;

public class GamesCommand extends BaseCommand {
	public GamesCommand() {
		super("games", null, true);
	}

	@Override
	public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
		if (!sender.isOp() || !sender.hasPermission(ADMIN_PERMISSION) || !sender
				.hasPermission(XPWars.getConfigurator().getString("games-gui.permission", "xpwars.gamesgui"))) {
			return;
		}
		if (args.size() == 1) {
			completion.addAll(XPWarsUtils.getOnlinePlayers());
		}
	}

	@Override
	public boolean execute(CommandSender sender, List<String> args) {
		if (!sender.isOp() || !sender.hasPermission(ADMIN_PERMISSION) || !sender
				.hasPermission(XPWars.getConfigurator().getString("games-gui.permission", "xpwars.gamesgui"))) {
			return true;
		}
		if (args.size() == 1) {
			Player player = Bukkit.getServer().getPlayer(args.get(0));
			if (player != null) {
				new GamesInventory(XPWars.getInstance()).openForPlayer(player);
				return true;
			}
		}
		if (sender instanceof Player) {
			new GamesInventory(XPWars.getInstance()).openForPlayer((Player) sender);
		}
		return true;
	}
}
