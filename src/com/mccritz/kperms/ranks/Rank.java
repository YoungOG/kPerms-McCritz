package com.mccritz.kperms.ranks;

import com.mccritz.kperms.kPerms;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Getter
@Setter
public class Rank {

    private kPerms main = kPerms.getInstance();
    private MongoCollection<Document> rankCollection = main.getMongoDatabase().getCollection("ranks");

    private String name;
    private Set<String> permissions;
    private String prefix, suffix;

    public Rank(String name) {
        this.name = name;
        this.permissions = new HashSet<>();
        this.prefix = "";
        this.suffix = "";
    }

    public void saveRankData() {
        Document document = new Document("name", name);
        document.append("permissions", permissions);
        document.append("prefix", prefix);
        document.append("suffix", suffix);

        rankCollection.replaceOne(Filters.eq("name", name), document, new UpdateOptions().upsert(true));
    }

    public void deleteRankData() {
        if (rankCollection.find(Filters.eq("name", Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE))).first() != null) {
            rankCollection.deleteOne(rankCollection.find(Filters.eq("name", name)).first());
        }
    }
}
