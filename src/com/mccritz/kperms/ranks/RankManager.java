package com.mccritz.kperms.ranks;

import com.mccritz.kperms.kPerms;
import com.mccritz.kperms.utils.PlayerUtility;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RankManager {

    private kPerms main = kPerms.getInstance();
    private MongoCollection<Document> rankCollection = main.getMongoDatabase().getCollection("ranks");

    private Set<Rank> ranks = new HashSet<>();
    private Rank defaultRank;
    private boolean defaulRankLoaded = false;

    public RankManager() {
        loadRanks();

//        updater = new RankUpdater();
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                reloadRanks();
//            }
//        }.runTaskTimerAsynchronously(main, 0L, 60L);
    }

    public void loadRanks() {
        if (rankCollection.count() > 0) {
            System.out.println("Preparing to load " + rankCollection.count() + " ranks.");

            for (Document document : rankCollection.find()) {
                Rank rank = new Rank(document.getString("name"));

                HashSet<String> permissionsList = new HashSet<>();
                List<String> permissions = (List<String>) document.get("permissions");
                for (String s : permissions) {
                    permissionsList.add(s);
                }

                rank.setPermissions(permissionsList);
                rank.setPrefix(document.getString("prefix"));
                rank.setSuffix(document.getString("suffix"));

                ranks.add(rank);

                System.out.println("Loading: " + rank.getName());
                System.out.println("  Permissions: " + rank.getPermissions().toString().replace("[", "").replace("]", ""));
                System.out.println("  Prefix: " + rank.getPrefix());
                System.out.println("  Suffix: " + rank.getSuffix());
            }

            System.out.println("Successfully loaded " + ranks.size() + " ranks.");
        } else {
            System.out.print("There were no ranks to load.");
        }

        if (!defaulRankLoaded && getByName(main.getConfig().getString("general.default-rank")) != null) {
            defaulRankLoaded = true;
            System.out.println("Default rank has been set to: " + main.getConfig().getString("general.default-rank"));
            defaultRank = getByName(main.getConfig().getString("general.default-rank"));
            ranks.add(defaultRank);
        } else if (!defaulRankLoaded && defaultRank == null) {
            defaulRankLoaded = true;
            System.out.println("Default rank was not found, creating a blank rank.");
            defaultRank = new Rank(main.getConfig().getString("general.default-rank"));
            ranks.add(defaultRank);
        }
    }

    public void saveRanks() {
        if (ranks.size() > 0) {
            System.out.println("Preparing to save " + ranks.size() + " ranks.");

            for (Rank rank : ranks) {
                rank.saveRankData();
            }

            System.out.println("Successfully saved " + ranks.size() + " ranks.");
        }
    }

    public void reloadRanks() {
        System.out.println("Reloading ranks.");

        ranks.clear();

        loadRanks();

        PlayerUtility.updatePermissions();
        PlayerUtility.updateTabList();
    }

    public Rank getByName(String name) {
        for (Rank rank : ranks) {
            if (rank.getName().equalsIgnoreCase(name)) {
                return rank;
            }
        }

        return null;
    }

    public Set<Rank> getRanks() {
        return ranks;
    }

    public Rank getDefaultRank() {
        return defaultRank;
    }
}
