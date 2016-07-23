package com.mccritz.kperms.commands;

import com.mccritz.kperms.ranks.Rank;
import com.mccritz.kperms.utils.MessageManager;
import com.mccritz.kperms.utils.PlayerUtility;
import com.mccritz.kperms.utils.command.BaseCommand;
import com.mccritz.kperms.utils.command.Command;
import com.mccritz.kperms.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

public class UpdateRanksCommand extends BaseCommand {

    @Command(name = "updateranks", aliases = {"reloadranks", "importranks"}, permission = "kperms.updateranks")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length != 0) {
            MessageManager.sendMessage(sender, "&c/" + command.getLabel());
            return;
        }

        getRankManager().reloadRanks();
    }
}
