package com.mccritz.kperms.commands;

import com.mccritz.kperms.profiles.Profile;
import com.mccritz.kperms.profiles.ProfileRequest;
import com.mccritz.kperms.utils.MessageManager;
import com.mccritz.kperms.utils.UUIDFetcher;
import com.mccritz.kperms.utils.command.BaseCommand;
import com.mccritz.kperms.utils.command.Command;
import com.mccritz.kperms.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;

public class SetUserPrefixCommand extends BaseCommand {

    @Command(name = "setuserprefix", aliases = {"setuprefix", "adduserprefix", "adduprefix"}, permission = "kperms.setuserprefix")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length != 2) {
            MessageManager.sendMessage(sender, "&c/" + command.getLabel() + " <player> <prefix>");
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

        final String finalName = name;
        final UUID finalUuid = uuid;
        getProfileManager().requestProfile(uuid, new ProfileRequest<Profile>() {
            @Override
            public void onComplete(Profile result) {
                Profile profile;

                if (result == null) {
                    getProfileManager().createProfile(finalUuid);

                    profile = getProfileManager().getProfile(finalUuid);
                } else {
                    profile = result;
                }

                if (profile != null) {
                    profile.setPrefix(args[1]);
                    profile.updateTabListName();
                    profile.saveProfileData(true);

                    sender.sendMessage(ChatColor.GRAY + "The user prefix for " + ChatColor.RED + finalName + ChatColor.GRAY + " has been set to " + ChatColor.RED + args[1] + ChatColor.GRAY + ".");
                } else {
                    MessageManager.sendMessage(sender, MessageManager.PROFILE_NOT_FOUND.replace("%player%", finalName));
                }
            }
        });
    }
}
