package com.mccritz.kperms.commands;

import com.mccritz.kperms.ranks.Rank;
import com.mccritz.kperms.utils.MessageManager;
import com.mccritz.kperms.utils.command.BaseCommand;
import com.mccritz.kperms.utils.command.Command;
import com.mccritz.kperms.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ListRanksCommand extends BaseCommand {

    @Command(name = "listranks", aliases = {"ranks", "listgroups", "groups"}, permission = "kperms.listranks")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length != 0) {
            MessageManager.sendMessage(sender, "&c/" + command.getLabel());
            return;
        }

        List<String> rankNames = new ArrayList<>();

        for (Rank rank : getRankManager().getRanks()) {
            rankNames.add("&c" + rank.getName());
        }

        if (getRankManager().getRanks().size() > 0) {
            MessageManager.sendMessage(sender, "&7Available ranks (&c" + getRankManager().getRanks().size() + "&7): &c" + rankNames.toString().replace("[", "").replace("]", "").replace(",", "&7,"));
        } else {
            MessageManager.sendMessage(sender, "&7There are no available ranks.");
        }
    }
}
