package com.mccritz.kperms.commands;

import com.mccritz.kperms.profiles.Profile;
import com.mccritz.kperms.ranks.Rank;
import com.mccritz.kperms.utils.MessageManager;
import com.mccritz.kperms.utils.command.BaseCommand;
import com.mccritz.kperms.utils.command.Command;
import com.mccritz.kperms.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

public class DeleteRankCommand extends BaseCommand {

    @Command(name = "deleterank", aliases = {"delrank", "delgroup", "deletegroup", "removegroup", "removerank"}, permission = "kperms.deleterank")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(sender, "&c/" + command.getLabel() + " <rank>");
            return;
        }

        Rank rank = getRankManager().getByName(args[0]);

        if (rank == null) {
            MessageManager.sendMessage(sender, "&7The rank &c" + args[0] + " &7does not exist.");
            return;
        }

        getRankManager().getRanks().remove(rank);
        rank.deleteRankData();

//        getRankManager().getUpdater().publish("update");

        for (Profile profile : getProfileManager().getProfiles()) {
            profile.setRank(null);
            profile.setRankOptions(null);
            profile.updatePermissions();
            profile.updateTabListName();
            profile.saveProfileData(true);
        }

        MessageManager.sendMessage(sender, "&7The rank &c" + args[0] + " &7has been deleted.");
    }
}
