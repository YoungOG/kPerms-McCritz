package com.mccritz.kperms.commands;

import com.mccritz.kperms.profiles.Profile;
import com.mccritz.kperms.profiles.ProfileRequest;
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

public class ClearUserSuffixCommand extends BaseCommand {

    @Command(name = "clearusersuffix", aliases = {"clearusuffix", "removeusersuffix", "removeusuffix", "deleteusersuffix", "deleteusuffix"}, permission = "kperms.clearusersuffix")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(sender, "&c/" + command.getLabel() + " <player>");
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
                    profile.setSuffix("");
                    profile.updateTabListName();
                    profile.saveProfileData(true);

                    MessageManager.sendMessage(sender, "&7The rank suffix for &c" + finalName + " &7has been cleared&7.");
                } else {
                    MessageManager.sendMessage(sender, MessageManager.PROFILE_NOT_FOUND.replace("%player%", finalName));
                }
            }
        });
    }
}
