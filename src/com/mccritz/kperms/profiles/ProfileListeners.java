package com.mccritz.kperms.profiles;

import com.mccritz.kperms.kPerms;
import com.mccritz.kperms.ranks.Rank;
import com.mccritz.kperms.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ProfileListeners implements Listener {

    private kPerms main = kPerms.getInstance();
    private ProfileManager profileManager = main.getProfileManager();

    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        profileManager.requestProfile(player.getUniqueId(), (result) -> {
            if (result != null) {
                String prefix = "";
                String suffix = "";

                Rank rank = result.getRank();

                if (rank != null) {
                    if (rank.getPrefix() != null && !rank.getPrefix().isEmpty()) {
                        prefix = rank.getPrefix();
                    }

                    if (rank.getSuffix() != null && !rank.getPrefix().isEmpty()) {
                        suffix = rank.getSuffix();
                    }
                }

                if (result.getPrefix() != null && !result.getPrefix().isEmpty()) {
                    prefix = result.getPrefix();
                }

                if (result.getSuffix() != null && !result.getPrefix().isEmpty()) {
                    suffix = result.getSuffix();
                }

                event.setFormat(ChatColor.translateAlternateColorCodes('&', "&f<" + prefix + player.getName() + suffix + "&f> ") + event.getMessage().replace("%", "%%"));
            } else {
                MessageManager.sendMessage(player, "&cYour rank has not been loaded yet, please wait a few moments.");
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        profileManager.requestProfile(player.getUniqueId(), (result) -> {
            if (result != null) {
                profileManager.getProfiles().add(result);

                result.updatePermissions();
                result.updateTabListName();
            } else {
                profileManager.createProfile(player.getUniqueId());
            }

            if (main.getConfig().getBoolean("general.nametag-colors")) {
                main.getTagManager().initPlayer(player);
                main.getTagManager().sendTeamsToPlayer(player);
                main.getTagManager().reloadPlayer(player);
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Profile prof = profileManager.getProfile(event.getPlayer().getUniqueId());

        if (prof != null) {
            prof.saveProfileData(true);
        }

        profileManager.getProfiles().remove(prof);

        if (main.getConfig().getBoolean("general.nametag-colors")) {
            main.getTagManager().getTeamMap().remove(event.getPlayer().getName());
        }
    }
}