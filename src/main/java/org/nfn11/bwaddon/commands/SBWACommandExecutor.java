package org.nfn11.bwaddon.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.nfn11.bwaddon.BwAddon;
import org.screamingsandals.bedwars.commands.BaseCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SBWACommandExecutor implements CommandExecutor, TabCompleter {
	@Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completionList = new ArrayList<>();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                for (BaseCommand c : BwAddon.getCommands().values()) {
                    if (c.hasPermission(player)) {
                        completionList.add(c.getName());
                    }
                }
            } else if (args.length > 1) {
                ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
                arguments.remove(0);
                BaseCommand bCommand = BwAddon.getCommands().get(args[0]);
                if (bCommand != null) {
                    if (bCommand.hasPermission(player)) {
                        bCommand.completeTab(completionList, sender, arguments);
                    }
                }
            }
        }
        List<String> finalCompletionList = new ArrayList<>();
        StringUtil.copyPartialMatches(args[args.length - 1], completionList, finalCompletionList);
        return finalCompletionList;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            args = new String[]{"help"};
        }

        String command = args[0];
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
        arguments.remove(0);

        BaseCommand bCommand = BwAddon.getCommands().get(command.toLowerCase());

        if (bCommand == null) {
            sender.sendMessage(BwAddon.getConfigurator().config.getString("messages.commands.unknown"));
            return true;
        }

        if (sender instanceof ConsoleCommandSender) {
            if (!bCommand.isConsoleCommand()) {
                sender.sendMessage("�cConsole can't use this command!");
                return true;
            }
        }

        if (!bCommand.hasPermission(sender)) {
            sender.sendMessage(BwAddon.getConfigurator().config.getString("messages.commands.noperm"));
            return true;
        }

        boolean result = bCommand.execute(sender, arguments);

        if (!result) {
            sender.sendMessage(BwAddon.getConfigurator().config.getString("messages.commands.unknown"));
        }

        return true;
    }

}
