package com.mccritz.kperms;

import com.mccritz.kperms.commands.*;
import com.mccritz.kperms.profiles.ProfileListeners;
import com.mccritz.kperms.profiles.ProfileManager;
import com.mccritz.kperms.ranks.RankManager;
import com.mccritz.kperms.utils.TagManager;
import com.mccritz.kperms.utils.command.CommandFramework;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

@Getter
public class kPerms extends JavaPlugin {

    private static kPerms instance;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private RankManager rankManager;
    private ProfileManager profileManager;
    private TagManager tagManager;
    private CommandFramework framework;

    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        connectToMongo();

        rankManager = new RankManager();
        profileManager = new ProfileManager();
        tagManager = new TagManager();
        framework = new CommandFramework(this);

        registerListeners();
        registerCommands();
    }

    public void onDisable() {
        rankManager.saveRanks();
//        rankManager.getUpdater().cleanup();

        profileManager.saveProfiles();
        tagManager.getAllTeams().forEach(tagManager::sendPacketsRemoveTeam);

        mongoClient.close();
    }

    public void connectToMongo() {
        MongoClientOptions options = MongoClientOptions.builder().connectionsPerHost(200000).build();
        mongoClient = new MongoClient(getConfig().getString("database.host"), options);
        mongoDatabase = mongoClient.getDatabase(getConfig().getString("database.database-name"));
    }

    private void registerCommands() {
        new AddPermissionCommand();
        new AddRankCommand();
        new AddUserPermissionCommand();
        new CheckRankCommand();
        new ClearPrefixCommand();
        new ClearSuffixCommand();
        new ClearUserPrefixCommand();
        new ClearUserSuffixCommand();
        new DeletePermissionCommand();
        new DeleteRankCommand();
        new DeleteUserPermissionCommand();
        new ListPermissionsCommand();
        new ListRanksCommand();
        new ListUserPermissionsCommand();
        new SetPrefixCommand();
        new SetRankCommand();
        new SetSuffixCommand();
        new SetUserPrefixCommand();
        new SetUserSuffixCommand();
        new HasPermissionCommand();
        new UpdateRanksCommand();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ProfileListeners(), this);
    }

    public static kPerms getInstance() {
        return instance;
    }
}
