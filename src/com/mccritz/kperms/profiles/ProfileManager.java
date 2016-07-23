package com.mccritz.kperms.profiles;

import com.mccritz.kperms.kPerms;
import com.mccritz.kperms.ranks.RankOptions;
import com.mccritz.kperms.ranks.RankType;
import com.mccritz.kperms.utils.PlayerUtility;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class ProfileManager {

    private kPerms main = kPerms.getInstance();
    private Set<Profile> profiles = new HashSet<>();

    public ProfileManager() {
        for (Player all : PlayerUtility.getOnlinePlayers()) {
            requestProfile(all.getUniqueId(), (result) -> {
                if (result != null) {
                    System.out.println("Loading " + all.getName() + "'s profile!");
                    getProfiles().add(result);

                    result.updatePermissions();
                    result.updateTabListName();
                    result.saveProfileData(true);
                }
            });
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Profile profile : getProfiles()) {
                    if (profile.getRank() != null) {
                        if (profile.getRankOptions() != null && profile.getRankOptions().getType() == RankType.TEMPORARY) {
                            if (System.currentTimeMillis() >= profile.getRankOptions().getDuration()) {
                                profile.setRank(main.getRankManager().getDefaultRank());
                                profile.setRankOptions(new RankOptions(RankType.PERMENANT));
                                return;
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(main, 0L, 20L);
    }

    public void saveProfiles() {
        if (getProfiles().size() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to save " + getProfiles().size() + " profiles.");

            int count = 0;

            for (Profile prof : getProfiles()) {
                count++;
                prof.saveProfileData(false);
            }

            getProfiles().clear();

            main.getLogger().log(Level.INFO, "Successfully saved " + count + " profiles.");
        }
    }

    public ProfileLoader requestProfile(UUID id, ProfileRequest<Profile> callback) {
        Profile profile = getProfile(id);

        if (profile != null) {
            return new BasicProfileLoader(profile, callback);
        }

        return new BasicProfileLoader(id, callback);
    }

    public void createProfile(UUID id) {
        Profile profile = new Profile(id);
        profile.setRank(main.getRankManager().getByName("Default"), new RankOptions(RankType.PERMENANT));
        profile.updatePermissions();
        profile.updateTabListName();

        profiles.add(profile);
    }

    public boolean hasLoadedProfile(UUID id) {
        for (Profile prof : getProfiles()) {
            if (prof.getUniqueId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    public Profile getProfile(UUID id) {
        for (Profile prof : getProfiles()) {
            if (prof.getUniqueId().equals(id)) {
                return prof;
            }
        }

        return null;
    }

    public Set<Profile> getProfiles() {
        return profiles;
    }
}
