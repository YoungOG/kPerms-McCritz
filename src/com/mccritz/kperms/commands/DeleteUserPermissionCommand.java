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

public class DeleteUserPermissionCommand extends BaseCommand {

    @Command(name = "deleteuserpermission", aliases = {"deluserperm", "deluperm", "deleteuperm"}, permission = "kperms.deleteuserpermission")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length != 2) {
            MessageManager.sendMessage(sender, "&c/" + command.getLabel() + " <player> <permission>");
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
                    if (!profile.getPermissions().contains(args[1])) {
                        MessageManager.sendMessage(sender, "&7The user &c" + finalName + " &7does not access to that permission.");
                        return;
                    }

                    profile.getPermissions().remove(args[1]);
                    profile.saveProfileData(true);

                    MessageManager.sendMessage(sender, "&7You have removed the permission &c" + args[1] + " &7from the user &c" + finalName + "&7.");
                } else {
                    MessageManager.sendMessage(sender, MessageManager.PROFILE_NOT_FOUND.replace("%player%", finalName));
                }
            }
        });
    }
}
