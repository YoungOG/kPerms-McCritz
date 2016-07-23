package com.mccritz.kperms.utils.command;

import com.mccritz.kperms.kPerms;
import com.mccritz.kperms.profiles.ProfileManager;
import com.mccritz.kperms.ranks.RankManager;
import com.mccritz.kperms.utils.TagManager;

public class BaseCommand {

    private kPerms main = kPerms.getInstance();
    private ProfileManager profileManager;
    private RankManager rankManager;
    private TagManager tagManager;

    public BaseCommand() {
        main.getFramework().registerCommands(this);

        profileManager = main.getProfileManager();
        rankManager = main.getRankManager();
        tagManager = main.getTagManager();
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public TagManager getTagManager() { return tagManager; }
}
