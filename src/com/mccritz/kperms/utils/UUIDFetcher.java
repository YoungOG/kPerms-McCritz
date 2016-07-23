package com.mccritz.kperms.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

public class UUIDFetcher {

    /**
     * UUID Fetcher v.2.0 by Max_Plays (02/14/2016)
     * <p>
     * You may:
     * - Use this class in your project
     * - Share it only with your friends
     * <p>
     * You may not:
     * - Re-upload it on the internet
     * - Pretend it belongs to you
     * - Delete this note
     */

    public static UUID getUUID(String playerName) throws IOException {
        String uuid;

        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName + "?");

        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line = reader.readLine();

        if (line == null) {
            return null;
        }

        String[] id = line.split(",");

        uuid = id[0];
        uuid = uuid.substring(7, 39);

        return UUID.fromString(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32));
    }

    public static String getName(UUID id) throws IOException {
        return "";
    }
}