package com.mccritz.kperms.utils;

import com.mccritz.kperms.kPerms;
import com.mccritz.kperms.profiles.Profile;
import com.mccritz.kperms.profiles.ProfileRequest;
import com.mccritz.kperms.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerUtility {

    public static Player[] getOnlinePlayers() {
        return Bukkit.getOnlinePlayers();
    }

    public static void updatePermissions() {
        for (Player all : getOnlinePlayers()) {
            kPerms.getInstance().getProfileManager().requestProfile(all.getUniqueId(), new ProfileRequest<Profile>() {
                @Override
                public void onComplete(Profile result) {
                    if (result != null) {
                        result.updatePermissions();
                    }
                }
            });
        }
    }

    public static void updatePermissions(Rank rank) {
        for (Player all : getOnlinePlayers()) {
            kPerms.getInstance().getProfileManager().requestProfile(all.getUniqueId(), new ProfileRequest<Profile>() {
                @Override
                public void onComplete(Profile result) {
                    if (result != null) {
                        if (result.getRank().getName().equals(rank.getName())) {
                            result.updatePermissions();

                            if (kPerms.getInstance().getConfig().getBoolean("general.nametag-colors")) {
                                kPerms.getInstance().getTagManager().reloadPlayer(all);
                            }
                        }
                    }
                }
            });
        }
    }

    public static void updateTabList() {
        for (Player all : getOnlinePlayers()) {
            kPerms.getInstance().getProfileManager().requestProfile(all.getUniqueId(), new ProfileRequest<Profile>() {
                @Override
                public void onComplete(Profile result) {
                    if (result != null) {
                        result.updateTabListName();

                        if (kPerms.getInstance().getConfig().getBoolean("general.nametag-colors")) {
                            kPerms.getInstance().getTagManager().reloadPlayer(all);
                        }
                    }
                }
            });
        }
    }
}