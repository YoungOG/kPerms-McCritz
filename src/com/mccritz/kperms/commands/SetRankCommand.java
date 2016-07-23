package com.mccritz.kperms.commands;

import com.mccritz.kperms.profiles.Profile;
import com.mccritz.kperms.profiles.ProfileRequest;
import com.mccritz.kperms.ranks.Rank;
import com.mccritz.kperms.ranks.RankOptions;
import com.mccritz.kperms.ranks.RankType;
import com.mccritz.kperms.utils.DateUtil;
import com.mccritz.kperms.utils.MessageManager;
import com.mccritz.kperms.utils.UUIDFetcher;
import com.mccritz.kperms.utils.command.BaseCommand;
import com.mccritz.kperms.utils.command.Command;
import com.mccritz.kperms.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;

public class SetRankCommand extends BaseCommand {

    @Command(name = "setrank", aliases = {"setgroup"}, permission = "kperms.setrank")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length != 2 && args.length != 3) {
            MessageManager.sendMessage(sender, "&c/" + command.getLabel() + " <user> <rank> [time]");
            return;
        }

        UUID uuid = null;
        String name = null;
        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            try {
                uuid = UUIDFetcher.getUUID(args[0]);
                name = args[0];
            } catch (IOException e) {
                e.printStackTrace();
                MessageManager.sendMessage(sender, MessageManager.PLAYER_NOT_FOUND.replace("%player%", args[0]));
            }
        } else {
            uuid = player.getUniqueId();
            name = player.getName();
        }

        if (uuid == null) {
            MessageManager.sendMessage(sender, MessageManager.PLAYER_NOT_FOUND.replace("%player%", args[0]));
            return;
        }

        Rank rank = getRankManager().getByName(args[1]);

        if (rank == null) {
            MessageManager.sendMessage(sender, "&7The rank &c" + args[1] + " &7does not exist.");
            return;
        }

        final String finalName = name;
        final UUID finalUuid = uuid;
        getProfileManager().requestProfile(uuid, new ProfileRequest<Profile>() {
            @Override
            public void onComplete(Profile result) {
                if (result == null) {
                    getProfileManager().createProfile(finalUuid);
                } else {
                    RankOptions rankOptions = new RankOptions(RankType.PERMENANT);

                    if (args.length == 3) {
                        try {
                            rankOptions = new RankOptions(RankType.TEMPORARY, DateUtil.parseDateDiff(args[2], true));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (result.getRank() != null && result.getRank() == rank) {
                        MessageManager.sendMessage(sender, "&7The user &c" + finalName + " &7already has the rank &c" + rank.getName() + "&7.");
                        return;
                    }

                    result.setRank(rank, rankOptions);
                    result.updatePermissions();
                    result.updateTabListName();
                    result.saveProfileData(true);

                    if (rankOptions.getType() == RankType.PERMENANT) {
                        MessageManager.sendMessage(sender, "&7The rank of &c" + finalName + " &7has been changed to &c" + rank.getName() + "&7.");
                    } else {
                        MessageManager.sendMessage(sender, "&7The rank of &c" + finalName + " &7has been changed to &c" + rank.getName() + "&7 for &c" + DateUtil.formatDateDiff(rankOptions.getDuration()) + "&7.");
                    }
                }
            }
        });
    }
}