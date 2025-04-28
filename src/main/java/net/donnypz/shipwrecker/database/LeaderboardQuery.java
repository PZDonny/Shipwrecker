package net.donnypz.shipwrecker.database;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import net.donnypz.shipwrecker.Shipwrecker;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public final class LeaderboardQuery {

    public static final String KILLS_FIELD = "kills";
    public static final String WINS_FIELD = "matches_won";
    private static final int LIMIT = 10;

    private LeaderboardQuery(){}

    public static List<Document> getHighestKills(){
        MongoCollection<Document> collection = Shipwrecker.getInstance().getPlayersCollection();

        AggregateIterable<Document> iter = collection.aggregate(List.of(
                                Aggregates.addFields(new Field<>(KILLS_FIELD, new Document("$add", List.of("$kills_melee", "$kills_projectile")))),
                                Aggregates.limit(LIMIT),
                                Aggregates.sort(Sorts.descending(KILLS_FIELD)),
                                Aggregates.project(Projections.include("uuid", KILLS_FIELD))));
        List<Document> docs = new ArrayList<>();

        for (Document d : iter){
            docs.add(d);
        }
        return docs;
    }

    public static List<Document> getHighestWins(){
        MongoCollection<Document> collection = Shipwrecker.getInstance().getPlayersCollection();

        FindIterable<Document> iter = collection
                .find()
                .projection(Projections.include("uuid", WINS_FIELD))
                .limit(LIMIT)
                .sort(Sorts.descending(WINS_FIELD));

        List<Document> docs = new ArrayList<>();

        for (Document d : iter){
            docs.add(d);
        }
        return docs;
    }
}
