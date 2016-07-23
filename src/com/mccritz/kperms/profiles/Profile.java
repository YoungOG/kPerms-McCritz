package com.mccritz.kperms.profiles;

import com.mccritz.kperms.kPerms;
import com.mccritz.kperms.ranks.Rank;
import com.mccritz.kperms.ranks.RankOptions;
import com.mccritz.kperms.ranks.RankType;
import com.mccritz.kperms.utils.DateUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class Profile {

    private kPerms main = kPerms.getInstance();
    private MongoCollection<Document> profileCollection = main.getMongoDatabase().getCollection("profiles");

    private Profile instance;
    private UUID uniqueId;
    private Set<String> permissions;
    private String prefix, suffix;
    private Rank rank;
    private RankOptions rankOptions;
    private PermissionAttachment attachment;

    public Profile(UUID uniqueId) {
        this.instance = this;
        this.uniqueId = uniqueId;
        this.permissions = new HashSet<>();
        this.prefix = "";
        this.suffix = "";
    }

    public void loadProfileData(@Nullable ProfileRequest<Profile> callback) {
       new BukkitRunnable() {
           @Override
           public void run() {
               Document document = profileCollection.find(Filters.eq("uniqueId", uniqueId.toString())).first();

               if (document == null) {
                   System.out.println("Failed to load " + uniqueId + "'s profile. (Document not found)");

                   if (callback != null)
                       callback.onComplete(null);

                   return;
               }

               HashSet<String> permissionsList = new HashSet<>();
               List<String> permissions = (List<String>) document.get("permissions");
               for (String s : permissions) {
                   permissionsList.add(s);
               }

               setUniqueId(UUID.fromString(document.getString("uniqueId")));
               setPermissions(permissionsList);
               setPrefix(document.getString("prefix"));
               setSuffix(document.getString("suffix"));

               if (document.getString("rankName") != null) {
                   String rankName = document.getString("rankName");
                   RankType rankType = RankType.valueOf(document.getString("rankType"));
                   RankOptions rankOptions = (document.getLong("rankDuration") != null ? new RankOptions(rankType, document.getLong("rankDuration")) : new RankOptions(rankType));

                   if (main.getRankManager().getByName(rankName) != null) {
                       if (rankOptions.getDuration() > 0 && System.currentTimeMillis() >= rankOptions.getDuration()) {
                           setRank(main.getRankManager().getDefaultRank());
                           setRankOptions(new RankOptions(RankType.PERMENANT));
                           return;
                       }

                       setRank(main.getRankManager().getByName(rankName));
                       setRankOptions(rankOptions);

                       System.out.println("Setting rank to " + rank.getName() + (rankOptions.getType() == RankType.TEMPORARY && rankOptions.getDuration() > 0 ? " for " + DateUtil.formatDateDiff(rankOptions.getDuration()) : ""));
                   } else {
                       setRank(main.getRankManager().getDefaultRank());
                       setRankOptions(new RankOptions(RankType.PERMENANT));
                   }
               } else {
                   setRank(main.getRankManager().getDefaultRank());
                   setRankOptions(new RankOptions(RankType.PERMENANT));
               }

               updatePermissions();
               updateTabListName();

               if (callback != null)
                   callback.onComplete(instance);
           }
       }.runTaskAsynchronously(main);
    }

    public void saveProfileData(boolean async) {
        Document document = new Document("uniqueId", uniqueId.toString());
        document.append("permissions", permissions);
        document.append("prefix", prefix);
        document.append("suffix", suffix);

        if (rank != null) {
            document.append("rankName", rank.getName());

            if (rankOptions != null) {
                document.append("rankType", rankOptions.getType().toString());

                if (rankOptions.getType() == RankType.TEMPORARY && rankOptions.getDuration() > 0) {
                    document.append("rankDuration", rankOptions.getDuration());
                }
            }
        }

        if (async) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    profileCollection.replaceOne(Filters.eq("uniqueId", uniqueId.toString()), document, new UpdateOptions().upsert(true));

                    System.out.println("Document found for " + uniqueId + ", updating.");
                }
            }.runTaskAsynchronously(main);
        } else {
            profileCollection.replaceOne(Filters.eq("uniqueId", uniqueId.toString()), document, new UpdateOptions().upsert(true));
            System.out.println("Document found for " + uniqueId + ", updating.");

        }
    }

    public void setRank(Rank rank, RankOptions rankOptions) {
        this.rank = rank;
        this.rankOptions = rankOptions;

        updateTabListName();
        updatePermissions();
    }

    public void updatePermissions() {
        if (Bukkit.getPlayer(uniqueId) != null) {
            Player player = Bukkit.getPlayer(uniqueId);

            if (attachment != null) {
                player.removeAttachment(attachment);
                attachment.remove();
                attachment = null;
            }

            attachment = player.addAttachment(kPerms.getInstance());

            if (attachment != null) {
                for (String permssion : permissions) {
                    if (permssion.startsWith("-")) {
                        attachment.setPermission(permssion, false);
                    } else {
                        attachment.setPermission(permssion, true);
                    }
                }

                if (rank != null) {
                    for (String permission : rank.getPermissions()) {
                        if (permission.startsWith("-")) {
                            attachment.setPermission(permission, false);
                        } else {
                            attachment.setPermission(permission, true);
                        }
                    }
                }
            }

            player.recalculatePermissions();
        }
    }

    public void updateTabListName() {
        if (main.getConfig().getBoolean("general.tablist-colors")) {
            if (Bukkit.getPlayer(uniqueId) != null) {
                Player player = Bukkit.getPlayer(uniqueId);
                String prefix = "";
                String suffix = "";

                if (rank != null) {
                    if (rank.getPrefix() != null && !rank.getPrefix().isEmpty()) {
                        prefix = rank.getPrefix();
                    }

                    if (rank.getSuffix() != null && !rank.getPrefix().isEmpty()) {
                        suffix = rank.getSuffix();
                    }
                }

                if (this.prefix != null && !this.prefix.isEmpty()) {
                    prefix = this.prefix;
                }

                if (this.suffix != null && !this.suffix.isEmpty()) {
                    suffix = this.suffix;
                }

                String name = ChatColor.translateAlternateColorCodes('&', prefix + player.getName() + suffix);

                player.setPlayerListName((name.length() >= 16 ? name.substring(0, 15) : name));
            }
        }
    }
}
